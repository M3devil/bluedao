package com.bluedao.bean;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 9:46
 * @类职责： 封装xml映射文件的信息
 */
public class MappedStatement {

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * sql标签id
     */
    private String id;

    /**
     * sql标签类型insert...
     */
    private String sqlType;

    /**
     * 返回值类型
     */
    private String returnType;

    /**
     * sql语句
     */
    private String sql;

    /**
     * 涉及的表
     */
    private Class<?>[] clazz;

    public MappedStatement() {
    }

    public MappedStatement(String namespace, String id, String sqlType, String returnType, String sql) {
        this.namespace = namespace;
        this.id = id;
        this.sqlType = sqlType;
        this.returnType = returnType;
        this.sql = sql;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class<?>[] getClazz() {
        return clazz;
    }

    public void setClazz(Class<?>[] clazz) {
        this.clazz = clazz;
    }
}
