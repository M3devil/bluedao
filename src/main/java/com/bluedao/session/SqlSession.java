package com.bluedao.session;

import com.bluedao.executor.Executor;

import java.util.List;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 10:05
 * @类职责：数据库操作的主要接口
 */
public interface SqlSession {

    <T> T selectOne(String statementId,Object parameter);

    <E> List<E> selectList(String statementId, Object parameter);

    int update(String statementId, Object parameter);

    <T> int update(T type);


    <T> int insert(T type);


    <T> int delete(T type);

    /**
     * 获取mapper
     * @param type mapper字节码对象
     * @param <T> mapper的接口类型
     * @return 绑定到session的mapper
     */
    <T> T getMapper(Class<T> type);

    /**
     * 获取配置类
     * @return 配置类对象
     */
    Configuration getConfiguration();

    /**
     * 获取执行器
     * @return 执行器对象
     */
    Executor getExecutor();

    /**
     * 设置事务自动提交
     * @param flag 是否开启
     */
    void setAutoCommit(boolean flag);

    /**
     * 回滚事务
     */
    void rollback();

    /**
     * 提交事务
     */
    void commit();

    /**
     * 关闭连接
     */
    void close();


}
