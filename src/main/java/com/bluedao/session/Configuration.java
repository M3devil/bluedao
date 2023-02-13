package com.bluedao.session;

import com.bluedao.bean.MappedStatement;
import com.bluedao.bean.TableInfo;
import com.bluedao.binding.MapperRegistry;
import com.bluedao.cache.LocalCache;
import com.bluedao.cache.NetCache;
import com.bluedao.cache.cache.time.TimeCount;
import com.bluedao.pool.MyDataSource;
import com.bluedao.pool.MyDataSourceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/16 16:29
 * @类职责：mybatis核心配置信息类
 */
public class Configuration {

    /**
     * 配置文件配置信息
     */
    private static Properties props = new Properties();

    /**
     * 数据库连接池
     */
    private MyDataSource dataSource = MyDataSourceImpl.getInstance();

    /**
     * xml文件的sql信息
     */
    private final Map<String, MappedStatement> mappedStatementMap = new HashMap<String,MappedStatement>();

    /**
     * 连接数据库的所有的表信息和类映射
     */
    private static final Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>();

    /**
     * mapper代理注册器
     */
    protected final MapperRegistry mapperRegistry = new MapperRegistry();

    /**
     * 二级缓存
     */
    private NetCache<String, Map<String, Object>> netCache;

    /**
     * 二级缓存命中
     */
    private static final TimeCount TIME_COUNT = new TimeCount();

    /**
     * 一级缓存
     */
    private LocalCache<String, Map<String, Object>> localCache;

    /**
     * 获取配置文件键值信息
     * @param key 键
     * @param defaultValue 属性不存在值的返回值
     * @return 值
     */
    public static String getProperty(String key,String defaultValue){
        return props.containsKey(key) ? props.getProperty(key) : defaultValue;
    }

    public static String getProperty(String key) {
        return getProperty(key,"");
    }

    /**
     * 通过指定的sql唯一标识获取到sql信息
     * @param statementId sql唯一标识
     * @return sql信息
     */
    public MappedStatement getMappedStatement(String statementId){
        return mappedStatementMap.get(statementId);
    }

    /**
     * 将sql信息对象和sql唯一标识存储进map中
     * @param statementId sql唯一标识
     * @param mappedStatement sql信息
     */
    public void addMappedStatement(String statementId,MappedStatement mappedStatement){
        mappedStatementMap.put(statementId, mappedStatement);
    }

    public MyDataSource getDataSource() {
        return dataSource;
    }

    public static Map<Class<?>, TableInfo> getTableInfoMap() {
        return tableInfoMap;
    }

    public static Properties getProps() {
        return props;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return this.mapperRegistry.getMapper(type, sqlSession);
    }

    public <T> void addMapper(Class<T> type){
        this.mapperRegistry.addMapper(type);
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public NetCache<String, Map<String, Object>> getNetCache() {
        return netCache;
    }

    public void setNetCache(NetCache<String, Map<String, Object>> netCache) {
        this.netCache = netCache;
    }

    public LocalCache<String, Map<String, Object>> getLocalCache() {
        return localCache;
    }

    public void setLocalCache(LocalCache<String, Map<String, Object>> localCache) {
        this.localCache = localCache;
    }

    public static TimeCount getTimeCount() {
        return TIME_COUNT;
    }
}
