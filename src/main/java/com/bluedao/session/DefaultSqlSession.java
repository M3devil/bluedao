package com.bluedao.session;

import com.bluedao.bean.ColumnInfo;
import com.bluedao.bean.Constant;
import com.bluedao.bean.MappedStatement;
import com.bluedao.bean.TableInfo;
import com.bluedao.callback.MyCallback;
import com.bluedao.executor.Executor;
import com.bluedao.executor.SimpleExecutor;
import com.bluedao.util.LogUtil;
import com.bluedao.util.ObjectValueUtil;
import com.bluedao.util.ReflectUtil;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 10:52
 * @类职责： 默认会话对象
 */
public class DefaultSqlSession implements SqlSession {

    /**
     * 配置类
     */
    private final Configuration configuration;

    /**
     * 执行器
     */
    private final Executor executor;

    /**
     * 日志管理
     */
    private final Logger LOGGER = LogUtil.getLogger();

    private final String MapperString = "Mapper";

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        executor = new SimpleExecutor(configuration);
    }

    /**
     * 查询一条数据
     * @param statementId sql语句唯一标识
     * @param parameter 参数
     * @param <T> 泛型
     * @return 结果
     */
    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        List<T> oneResult = this.selectList(statementId, parameter);
        if (ObjectValueUtil.isEmpty(oneResult)){
            return null;
        }
        if (oneResult.size() == 1){
            return oneResult.get(0);
        }
        throw new RuntimeException("查询结果出错，查询到多条数据： " + oneResult.size());
    }

    /**
     * 查询多条数据
     * @param statementId 完好sql语句的唯一id
     * @param parameter 参数
     * @param <E> 泛型
     * @return 结果集
     */
    @Override
    public <E> List<E> selectList(String statementId, Object parameter) {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statementId);
        return this.executor.doQuery(mappedStatement,parameter);
    }

    /**
     * 增删改操作
     * @param statementId sql语句的唯一id
     * @param parameter 参数
     * @return 受影响行数
     */
    @Override
    public int update(String statementId, Object parameter) {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statementId);
        return this.executor.doUpdate(mappedStatement,parameter);
    }

    /**
     * 更新操作
     * @param type po类
     * @param <T> 泛型
     * @return 受影响行数
     */
    @Override
    public <T> int update(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("update ").append(tableInfo.getTableName()).append(" set ");
                for (Field field : fields) {
                    String fieldName = field.getName();
                    Object fieldValue = ReflectUtil.invokeGet(type, fieldName);
                    if (!ObjectValueUtil.isEmpty(fieldValue)){
                        //判断是否为主键列
                        for (ColumnInfo columnInfo : primaryKeys) {
                            if (!fieldName.equals(columnInfo.getName())){
                                sql.append(ObjectValueUtil.humpToLine(fieldName)).append("=?,");
                                params.add(fieldValue);
                            }
                        }
                    }
                }
                //去除当前最后一个逗号
                sql.setCharAt(sql.length() - 1, ' ');

                //添加更新条件
                sql.append("where ");
                for (ColumnInfo columnInfo : primaryKeys) {
                    sql.append(ObjectValueUtil.humpToLine(columnInfo.getName())).append("=?,");
                    params.add(ReflectUtil.invokeGet(type,ObjectValueUtil.lineToHump(columnInfo.getName())));
                }
            }
        });
    }

    @Override
    public <T> int insert(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("insert into ").append(tableInfo.getTableName()).append("(");
                //遍历该类属性
                for (Field field : fields) {
                    String fieldName = field.getName();
                    Object fieldValue = ReflectUtil.invokeGet(type, fieldName);
                    if (!ObjectValueUtil.isEmpty(fieldValue)){
                        sql.append(ObjectValueUtil.humpToLine(fieldName)).append(",");
                        params.add(fieldValue);
                    }
                }
                //将最后一个逗号替换成右括号
                sql.setCharAt(sql.length() - 1 ,')');
                sql.append(" values(");
                for (int i = 0; i < params.size(); i++) {
                    sql.append("?,");
                }
                //将最后一个逗号替换成右括号
                sql.setCharAt(sql.length() - 1 ,' ');
                sql.append("),");
            }
        });
    }

    @Override
    public <T> int delete(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("delete from ").append(tableInfo.getTableName()).append(" where ");
                for (ColumnInfo columnInfo : primaryKeys) {
                    sql.append(ObjectValueUtil.humpToLine(columnInfo.getName())).append("=?,");
                    //获取主键字段对应值并存入参数中
                    params.add(ReflectUtil.invokeGet(type, ObjectValueUtil.lineToHump(columnInfo.getName())));
                }
            }
        });
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return this.configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    @Override
    public void setAutoCommit(boolean flag) {
        this.executor.setAutoCommit(flag);
    }

    @Override
    public void rollback() {
        this.executor.rollback();
    }

    @Override
    public void commit() {
        this.executor.commit();
    }

    @Override
    public void close() {
        this.executor.close();
    }

    /**
     * 生成sql语句的模板方法
     * @param type po类型
     * @param callback 回调接口
     * @param <T> 泛型
     * @return 受影响行数
     */
    private <T> Object generateSqlTemplate(T type, MyCallback callback){
        Class<?> clazz = type.getClass();
        Field[] fields = clazz.getDeclaredFields();
        TableInfo tableInfo = Configuration.getTableInfoMap().get(clazz);
        List<ColumnInfo> primaryKeys = tableInfo.getPrimaryKeys();

        List<Object> params = new ArrayList<>();
        MappedStatement mappedStatement = new MappedStatement();

        // namespace约定： User -> {mapperLocation}.{TableName}Mapper
        String namespace = Configuration.getProperty(Constant.MAPPER_LOCATION) + "." + ObjectValueUtil.tableNameToClassName(tableInfo.getTableName()) + MapperString;
        mappedStatement.setNamespace(namespace);

        StringBuilder sql = new StringBuilder();
        //调用回调接口生成sql
        callback.generateSqlExecutor(fields,tableInfo,primaryKeys,sql,mappedStatement,params);

        sql.setCharAt(sql.length() - 1 , ' ');

        LOGGER.debug("generate sql : {} ",sql);
        LOGGER.debug("sql params : {} ", params);

        mappedStatement.setSql(sql.toString());

        return this.executor.doUpdate(mappedStatement, params.toArray());
    }

}
