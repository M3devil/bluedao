package com.bluedao.executor;

import com.bluedao.bean.MappedStatement;

import java.util.List;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 10:09
 * @类职责：操作数据库的接口
 */
public interface Executor {

    /**
     * 查操作
     * @param mappedStatement sql信息
     * @param parameter 参数
     * @param <E> sql信息中的返回类型相同
     * @return 结果集
     */
    <E> List<E> doQuery(MappedStatement mappedStatement, Object parameter);

    /**
     * 增删改操作
     * @param mappedStatement sql信息
     * @param parameter 参数
     * @return 更改数
     */
    int doUpdate(MappedStatement mappedStatement, Object parameter);

    /**
     * 开启事务自动提交
     * @param autoCommit 是否开启
     */
    void setAutoCommit(boolean autoCommit);

    /**
     * 事务回滚
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
