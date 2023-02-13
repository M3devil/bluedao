package com.bluedao.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/16 15:59
 * @类职责：数据库连接池接口
 */
public interface MyDataSource extends DataSource {

    //定义自己的DataSource方法

    /**
     * 归还连接
     * @param conn 需要归还的连接
     */
    void returnConnection(Connection conn);

    /**
     * 有描述的规划连接
     * @param conn 归还连接
     * @param message 描述
     */
    void returnConnection(Connection conn, String message);

    /**
     * 获取空闲连接数
     * @return 空闲连接数
     */
    int getIdleCount();

    /**
     * 获取已创建连接数
     * @return 已创建连接数
     */
    int getCreatedCount();


    //DataSource的方法，全部提供空默认实现，由子类具体实现

    default Connection getConnection() throws SQLException{
        return null;
    };

    default Connection getConnection(String username, String password) throws SQLException{
        return null;
    };

    default <T> T unwrap(Class<T> iface) throws SQLException{
        return null;
    };

    default boolean isWrapperFor(Class<?> iface) throws SQLException{
        return false;
    };

    default PrintWriter getLogWriter() throws SQLException{
        return null;
    };

    default void setLogWriter(PrintWriter out) throws SQLException{

    };

    default void setLoginTimeout(int seconds) throws SQLException{

    };

    default int getLoginTimeout() throws SQLException{
        return 0;
    };

    default Logger getParentLogger() throws SQLFeatureNotSupportedException{
        return null;
    };
}
