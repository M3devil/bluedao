package com.bluedao.pool;

import com.bluedao.bean.Constant;
import com.bluedao.session.Configuration;
import com.bluedao.util.LogUtil;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/16 16:07
 */
public class MyDataSourceImpl implements MyDataSource {

    /**
     * 数据库连接属性
     */
    private static String DRIVER;
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    /**
     * 初始连接数量
     */
    private static int initCount = 5;

    /**
     * 最小连接数量
     */
    private static int minCount = 5;

    /**
     * 最大连接数量
     */
    private static int maxCount = 20;

    /**
     * 已创建的连接数量
     */
    private static int createdCount;

    /**
     * 连接数增长步长
     */
    private static int increasingCount = 2;

    /**
     * 获取连接的最大等待时间
     */
    private static int maxWaitingTime = 5000;

    /**
     * 空闲连接的最大存活时间
     */
    private static int maxIdleTime = 20000;

    /**
     * 存储配置文件信息
     */
    private static Configuration configuration;

    /**
     * 存储连接的集合
     */
    private LinkedList<Connection> conns = new LinkedList<>();

    /**
     * 用于获取连接和归还连接的同步锁对象
     */
    private static final Object MONITOR = new Object();

    /**
     * 日志管理器
     */
    private static final Logger LOGGER = LogUtil.getLogger();

    /**
     * 归还连接的线程数
     */
    private ExecutorService returnConnectThreadPool = Executors.newFixedThreadPool(maxCount);

    /**
     * 连接池对象
     */
    private static volatile MyDataSourceImpl instance;

    /**
     * 私有化构造器，单例
     */
    private MyDataSourceImpl(){
        //数据库连接池初始化
        init();
    }

    //使用枚举类来确保单例
    private enum DataSourceHolder{
        HOLDER;

        private void setDataSourceHolder() {
            instance = new MyDataSourceImpl();
        }
    }

    public static MyDataSourceImpl getInstance(){
        DataSourceHolder.HOLDER.setDataSourceHolder();
        return instance;
    }

    //获取初始化连接池配置
    static {
        //连接配置
        DRIVER = Configuration.getProperty(Constant.JDBC_DRIVER);
        URL = Configuration.getProperty(Constant.JDBC_URL);
        USERNAME = Configuration.getProperty(Constant.JDBC_USERNAME);
        PASSWORD = Configuration.getProperty(Constant.JDBC_PASSWORD);

        //连接池配置
        try {
            initCount = Integer.parseInt(Configuration.getProperty(Constant.JDBC_INIT_COUNT));
        } catch (Exception e) {
            LOGGER.debug("initCount使用默认值：" + initCount);
        }
        try {
            minCount = Integer.parseInt(Configuration.getProperty(Constant.JDBC_MIN_COUNT));
        } catch (Exception e) {
            LOGGER.debug("minCount使用默认值：" + minCount);
        }
        try {
            maxCount = Integer.parseInt(Configuration.getProperty(Constant.JDBC_MAX_COUNT));
        } catch (Exception e) {
            LOGGER.debug("maxCount使用默认值：" + maxCount);
        }
        try {
            increasingCount = Integer.parseInt(Configuration.getProperty(Constant.JDBC_INCREASING_COUNT));
        } catch (Exception e) {
            LOGGER.debug("increasingCount使用默认值：" + increasingCount);
        }
        try {
            maxWaitingTime = Integer.parseInt(Configuration.getProperty(Constant.JDBC_MAX_WAITING_TIME));
        } catch (Exception e) {
            LOGGER.debug(" maxWaitingTime使用默认值：" + maxWaitingTime);
        }
        try {
            maxIdleTime = Integer.parseInt(Configuration.getProperty(Constant.JDBC_MAX_IDLE_TIME));
        } catch (Exception e) {
            LOGGER.debug(" maxIdleTime使用默认值：" + maxIdleTime);
        }
    }

    //初始化连接池
    private void init(){
        //给集合中添加初始连接
        for (int i = 0; i < initCount; i++) {
            boolean flag = conns.add(createConnection());
            if (flag){
                createdCount ++;
            }
        }
        LOGGER.debug("连接池初始化: 连接池对象 "+ this + "::连接池可用连接数量 "+ createdCount);
    }

    /**
     * 创建数据库连接对象
     * @return 连接对象
     */
    private Connection createConnection(){
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error("创建数据库连接失败:" ,e);
            throw new RuntimeException("创建数据库连接失败: " + e.getMessage());
        }
    }

    private synchronized void autoAdd(){
        //增长步长默认为2
        if (createdCount == maxCount) {
            LOGGER.error("连接池中连接已达最大数量,无法再次创建连接");
            throw new RuntimeException("连接池中连接已达最大数量,无法再次创建连接");
        }
        //临界时判断增长个数
        for (int i = 0; i < increasingCount; i++) {
            if (createdCount == maxCount) {
                break;
            }
            conns.add(createConnection());
            createdCount++;
        }
    }

    /**
     * 获取连接池中的连接
     * @return 返回连接
     */
    @Override
    public Connection getConnection(){
        synchronized (MONITOR){
            //判断池中是否还有连接
            if (conns.size() > 0){
                LOGGER.debug("获取到连接： " +conns.peek() + "，当前已创建连接数量 : "+ createdCount + ",当前空闲连接: "+ (conns.size() - 1 ));
                return conns.poll();
            }
            //如果没有空连接，自动增长
            if (createdCount < maxCount){
                autoAdd();
                return getConnection();
            }
            //如果使用的连接池的连接数量上限
            LOGGER.debug("连接池中连接已用尽，请等待连接归还");
            try {
                MONITOR.wait(maxWaitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return conns.size() > 0 ? getConnection() : null;
        }
    }

    /**
     * 归还连接，关闭连接
     * @param conn 归还连接
     * @param message 描述
     */
    @Override
    public void returnConnection(Connection conn, String message) {
        synchronized (MONITOR){
            LOGGER.debug(message + ": 准备归还数据库连接 " + conn);
            conns.offer(conn);
            MONITOR.notify();
            Runnable closeConnectionTask = () -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                autoReduce(conn);
            };
            returnConnectThreadPool.execute(closeConnectionTask);
        }
    }

    @Override
    public void returnConnection(Connection conn) {
        returnConnection(conn,"");
    }

    /**
     * 自动减少连接
     * @param conn 需要进行关闭的连接
     */
    private void autoReduce(Connection conn) {
        synchronized (MONITOR){
            if (createdCount > minCount && conns.contains(conn)){
                //关闭空闲连接数
                try {
                    conns.remove(conn);
                    conn.close();
                    createdCount --;
                    LOGGER.debug("已关闭多余空闲连接。当前所有已创建连接数：" + createdCount + ",当前空闲连接数: " + conns.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else {
                LOGGER.debug("所归还连接 " + conn+ "保留到连接池中或已被使用。当前创建连接数量: "+ createdCount+",当前空闲连接数: "+ conns.size());
            }
        }
    }

    /**
     * 获取空闲连接数
     * @return 空闲连接数
     */
    @Override
    public int getIdleCount() {
        return conns.size();
    }

    /**
     * 获取已有连接数
     * @return 连接数
     */
    @Override
    public int getCreatedCount() {
        return createdCount;
    }

    /**
     * 关闭所有连接
     */
    public void close(){
        LOGGER.debug("正在关闭数据库连接池");
        for (Connection conn : conns) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.debug("关闭连接出错：{}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        LOGGER.debug("数据库连接池关闭完毕!");
    }
}
