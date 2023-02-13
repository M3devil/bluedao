package com.bluedao.executor;

import com.bluedao.bean.MappedStatement;
import com.bluedao.cache.LocalCache;
import com.bluedao.cache.NetCache;
import com.bluedao.cache.cache.time.TimeCount;
import com.bluedao.cache.cache.manage.CacheManage;
import com.bluedao.callback.MyCallback;
import com.bluedao.executor.parameter.DefaultParameterHandler;
import com.bluedao.executor.resultset.DefaultResultSetHandler;
import com.bluedao.executor.statement.SimpleStatementHandler;
import com.bluedao.executor.statement.StatementHandler;
import com.bluedao.pool.MyDataSource;
import com.bluedao.session.Configuration;
import com.bluedao.util.LogUtil;
import com.bluedao.util.ObjectValueUtil;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 10:54
 * @类职责： 默认执行器
 */
public class SimpleExecutor implements Executor {

    /**
     * 连接池对象
     */
    private final MyDataSource dataSource;

    /**
     * 连接对象.
     */
    private final Connection connection;

    /**
     * 设置自动提交
     */
    private boolean autoCommit = false;

    private final static Logger LOGGER = LogUtil.getLogger();

    /**
     * 一级缓存
     */
    private final LocalCache<String ,Map<String, Object>> localCache;

    /**
     * 二级缓存
     */
    private final NetCache<String, Map<String, Object>> netCache;

    /**
     * 一级缓存命中
     */
    private final TimeCount timeCount;

    public SimpleExecutor(Configuration configuration) {
        dataSource = configuration.getDataSource();
        netCache = configuration.getNetCache();
        localCache = configuration.getLocalCache();
        timeCount = new TimeCount();
        try {
            connection = dataSource.getConnection();
            if (connection==null){
                throw new RuntimeException("连接获取失败，请重试!");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> doQuery(MappedStatement mappedStatement, Object parameter) {

        // 查询缓存数据
        String namespace = mappedStatement.getNamespace();
        String cacheId = mappedStatement.getSql();

        // 查缓存
        CacheManage cacheManage = new CacheManage(timeCount);
        Object cache = cacheManage.searchDataFromCache(localCache, netCache, namespace, cacheId);
        if (cache != null) {
            return (List<E>) cache;
        }

        return (List<E>) executeTemplate(mappedStatement, parameter, new MyCallback() {
            @Override
            public Object doExecutor(StatementHandler statementHandler, PreparedStatement preparedStatement) {
                try {
                    ResultSet resultSet = statementHandler.query(preparedStatement);
                    //处理结果集
                    DefaultResultSetHandler resultSetHandler = new DefaultResultSetHandler(mappedStatement);
                    //封装到List集合上
                    List<E> result = resultSetHandler.handlerResult(resultSet);

                    // 将数据放入缓存
                    cacheManage.putDataToCache(localCache, netCache, namespace, cacheId, result);
                    // 返回结果
                    return result;
                }catch (Exception e){
                    LOGGER.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public int doUpdate(MappedStatement mappedStatement, Object parameter) {
        Integer res = (Integer) executeTemplate(mappedStatement, parameter, new MyCallback() {
            @Override
            public Object doExecutor(StatementHandler statementHandler, PreparedStatement preparedStatement) {
                try {
                    return statementHandler.update(preparedStatement);
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        if (!ObjectValueUtil.isEmpty(res)){
            // 删除缓存
            String namespace = mappedStatement.getNamespace();

            Optional.ofNullable(localCache).map(lc -> lc.get(namespace)).ifPresent(lc -> localCache.remove(namespace));
            Optional.ofNullable(netCache).map(nc -> nc.get(namespace)).ifPresent(nc -> netCache.remove(namespace));

            return res;
        }else {
            LOGGER.error("更新出现错误，受影响行数为空值");
            throw new RuntimeException("更新出现错误，受影响行数为空值");
        }

    }

    @Override
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit  = autoCommit;
    }

    @Override
    public void rollback() {
        try {
            connection.rollback();
        }catch (SQLException e){
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            connection.commit();
        }catch (SQLException e){
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        }catch (SQLException e){
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 抽取出执行sql操作获取当前连接、预处理sql语句、参数赋值成一个模板
     * @param mappedStatement sql信息
     * @param parameter 参数
     * @param callback 回调接口
     * @return 返回执行结果
     */
    private Object executeTemplate(MappedStatement mappedStatement, Object parameter, MyCallback callback){
        try {
            connection.setAutoCommit(autoCommit);
            //创建sql处理对象
            StatementHandler  statementHandler = new SimpleStatementHandler(mappedStatement);
            //对sql信息预处理器，获取?预处理语句
            PreparedStatement preparedStatement = statementHandler.prepared(connection);
            //创建参数处理器，将参数代替?
            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(parameter);
            parameterHandler.setParameters(preparedStatement);
            LOGGER.debug("prepareStatement : "+ preparedStatement);
            //回调接口执行代码
            return callback.doExecutor(statementHandler, preparedStatement);
        }catch (Exception e){
            LOGGER.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }finally {
            dataSource.returnConnection(connection);
        }
    }


}
