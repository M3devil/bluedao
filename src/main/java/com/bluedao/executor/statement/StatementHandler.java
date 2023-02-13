package com.bluedao.executor.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 14:51
 * @类职责：sql处理器
 */
public interface StatementHandler {

    /**
     * SQL预处理填充
     * @param connection 连接对象
     * @return 完成预处理对象
     * @throws SQLException sql异常
     */
    PreparedStatement prepared(Connection connection) throws SQLException;

    /**
     * 查询操作
     * @param preparedStatement 完成预处理后的对象
     * @return 查询后的结果集
     * @throws SQLException sql异常
     */
    ResultSet query(PreparedStatement preparedStatement) throws SQLException;

    /**
     * 更新操作
     * @param preparedStatement 完成预处理后的对象
     * @return 受到影响的行数
     * @throws SQLException sql异常
     */
    int update(PreparedStatement preparedStatement) throws SQLException;
}
