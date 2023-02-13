package com.bluedao.session;

import com.bluedao.bean.ColumnInfo;
import com.bluedao.bean.Constant;
import com.bluedao.bean.TableInfo;
import com.bluedao.cache.build.LocalCacheBuilder;
import com.bluedao.cache.cache.SimpleNetCache;
import com.bluedao.cache.filter.SimpleBloomFilter;
import com.bluedao.pool.MyDataSource;
import com.bluedao.util.LogUtil;
import com.bluedao.util.ObjectValueUtil;
import com.bluedao.util.XmlParseUtil;
import org.slf4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 10:41
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private static volatile DefaultSqlSessionFactory instance;

    private Configuration configuration;

    private static Logger LOGGER = LogUtil.getLogger();

    private DefaultSqlSessionFactory(){}

    private DefaultSqlSessionFactory(Configuration configuration){
        if (instance != null){
            throw new RuntimeException("Object has been instanced,please do not create Object by Reflect!");
        }
        this.configuration = configuration;
        //将sql信息存储，创建注册代理工厂
        loadMapperInfo(Configuration.getProperty(Constant.MAPPER_LOCATION).replaceAll("\\.","/"));
        //加载数据库表结构
        loadTableInfo(configuration);
        //全局配置对象初始化
        loadInitInstance();
    }

    /**
     * 加载配置的对象
     */
    private void loadInitInstance() {
        loadLocalCache();
        loadNetCache();
    }

    /**
     * 加载网络缓存
     */
    private void loadNetCache() {
        if("true".equals(Configuration.getProperty(Constant.NET_CACHE))) {
            LOGGER.debug("start to init net cache");
            String ip = Configuration.getProperty(Constant.NET_CACHE_IP, "");
            String portString = Configuration.getProperty(Constant.NET_CACHE_PORT, "");
            int port = -1;
            if(!"".equals(portString)) {
                port = Integer.parseInt(portString);
            }
            String password = Configuration.getProperty(Constant.NET_CACHE_PASSWORD, "");
            try {
                if(!"".equals(ip) && port != 0) {
                    configuration.setNetCache(new SimpleNetCache(ip, port, password));
                }
            } catch (Exception e) {
                throw new RuntimeException("failed to connect redis : " + e);
            }
            LOGGER.debug("success to init net cache");
        }
    }

    /**
     * 加载本地缓存
     */
    private void loadLocalCache() {
        if("true".equals(Configuration.getProperty(Constant.LOCAL_CACHE))) {
            LOGGER.debug("start to init local cache");
            configuration.setLocalCache(new LocalCacheBuilder<String, Map<String, Object>>()
                    .setCapacity(100)  // 设置容量
                    .setFactor(0.25f)  // 3 : 1 热冷
                    .setInterval(3000) // 冷热替换时间
                    .setFilter(new SimpleBloomFilter<>())
                    .build());
            LOGGER.debug("success to init local cache");
        }
    }

    /**
     * 解析mapper.xml中信息封装到mappedStatementMap中
     * @param mapperLocation mapper.xml映射文件所在位置
     */
    private void loadMapperInfo(String mapperLocation) {
        LOGGER.debug("mapperLocation : {}", mapperLocation);
        String resource = Objects.requireNonNull(
                DefaultSqlSessionFactory.class.getClassLoader().
                        getResource(mapperLocation)).getPath();
        LOGGER.debug("加载资源路径 : "+ resource);
        File mapperDir = new File(resource);
        //判断是否为文件夹
        if (mapperDir.isDirectory()){
            File[] mappers = mapperDir.listFiles();
            if (!ObjectValueUtil.isEmpty(mappers)){
                for (File mapper : mappers) {
                    if (mapper.isDirectory()){
                        loadMapperInfo(mapperLocation + "/" + mapper.getName());
                    }else if (mapper.getName().endsWith(Constant.MAPPER_FILE_SUFFIX)){
                        //获取.xml文件中的所有标签信息，封装到mappedStatement中，并注册一个该类的代理工厂
                        XmlParseUtil.mapperParser(mapper, this.configuration);
                    }
                }
            }
        }
    }

    /**
     * 获取到数据库表的信息，封装到tableInfoMap中
     * @param configuration 配置类对象
     */
    private void loadTableInfo(Configuration configuration) {
        //获取数据库连接，用于读取数据表数据
        MyDataSource dataSource = configuration.getDataSource();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            //获取数据库元数据
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            //获取数据库名称
            String catalog = Configuration.getProperty(Constant.CATALOG);
            //获取所有表信息
            ResultSet tablesResult = databaseMetaData.getTables(catalog, "%", "%", new String[]{"TABLE"});
            //遍历表
            while (tablesResult.next()){
                String tableName = tablesResult.getString("TABLE_NAME");
                //反射获取该表对应的po类，如果po包中没有对应的数据表，则跳过读取该表的数据
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(Configuration.getProperty(Constant.PO_LOCATION) + "." + ObjectValueUtil.tableNameToClassName(tableName));
                } catch (ClassNotFoundException e) {
                    LOGGER.debug("There is need a {"+ tableName +"} po.Skip loading the table which tableName is :" + tableName);
                    continue;
                }
                TableInfo tableInfo = new TableInfo(tableName, new HashMap<>(10), new ArrayList<>(), new ArrayList<>());
                //获取表中所有列信息
                ResultSet columnResultSet = databaseMetaData.getColumns(catalog, "%", tableName, null);
                //遍历所有列，放入表中字段
                while (columnResultSet.next()){
                    ColumnInfo columnInfo = new ColumnInfo(columnResultSet.getString("COLUMN_NAME"),
                            columnResultSet.getString("TYPE_NAME"), ColumnInfo.Key.DEFAULT);
                    tableInfo.getColumnInfoMap().put(columnInfo.getName(), columnInfo);
                }
                //获取主键列，放入表中主键列
                ResultSet primaryKeysResultSet = databaseMetaData.getPrimaryKeys(catalog, "%", tableName);
                while (primaryKeysResultSet.next()){
                    ColumnInfo primaryColumnInfo = tableInfo.getColumnInfoMap().get(primaryKeysResultSet.getString("COLUMN_NAME"));
                    primaryColumnInfo.setKey(ColumnInfo.Key.PRIMARY);
                    tableInfo.getPrimaryKeys().add(primaryColumnInfo);
                }
                //当表中没有主键，抛出异常
                if (tableInfo.getPrimaryKeys().size() == 0){
                    LOGGER.error("数据库表" + tableName + "未检测到主键！");
                }
                //获取外键列信息
                ResultSet importedKeysResultSet = databaseMetaData.getImportedKeys(catalog, null, tableName);
                while (importedKeysResultSet.next()){
                    ColumnInfo foreignColumnInfo = tableInfo.getColumnInfoMap().get(importedKeysResultSet.getString("FKCOLUMN_NAME"));
                    tableInfo.getForeignKeys().add(foreignColumnInfo);
                }
                //将数据存入配置对象的tableInfoMap中
                LOGGER.debug("获取数据库表对象：" + tableInfo);
                Configuration.getTableInfoMap().put(clazz, tableInfo);
                LOGGER.debug("加载实体类与数据库映射："+ Configuration.getProperty(Constant.PO_LOCATION)+ "." + ObjectValueUtil.tableNameToClassName(tableName) + "<------>" + tableName);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }finally {
            dataSource.returnConnection(conn, this.getClass().getName()+",loadTableInfo");
        }
    }

    /**
     * 获取到SqlSessionFactory单例实例
     * @param configuration 核心配置类
     * @return 返回SqlSession工厂对象
     */
    public static DefaultSqlSessionFactory getInstance(Configuration configuration){
        if (instance == null){
            synchronized (DefaultSqlSessionFactory.class){
                if (instance == null){
                    instance =new DefaultSqlSessionFactory(configuration);
                }
            }
        }
        return instance;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(this.configuration);
    }
}
