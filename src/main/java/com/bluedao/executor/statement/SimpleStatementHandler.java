package com.bluedao.executor.statement;

import com.bluedao.bean.MappedStatement;
import com.bluedao.util.LogUtil;
import com.bluedao.util.ObjectValueUtil;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/19 15:17
 */
public class SimpleStatementHandler implements StatementHandler {

    /**
     * 正则匹配语句中的#{}标签
     */
    private static final Pattern PARAM_PATTERN = Pattern.compile("#\\{([^{}]*)}");

    private static final Logger LOGGER = LogUtil.getLogger();

    private final MappedStatement mappedStatement;

    public SimpleStatementHandler(MappedStatement mappedStatement) {
        this.mappedStatement = mappedStatement;
    }

    @Override
    public PreparedStatement prepared(Connection connection) throws SQLException {
        String originSql = mappedStatement.getSql();
        if (!ObjectValueUtil.isEmpty(originSql)){
            return connection.prepareStatement(parseSymbol(originSql));
        }else {
            LOGGER.error("sql is null");
            throw new RuntimeException("sql is null");
        }
    }

    @Override
    public ResultSet query(PreparedStatement preparedStatement) throws SQLException {
        return preparedStatement.executeQuery();
    }

    @Override
    public int update(PreparedStatement preparedStatement) throws SQLException {
        return preparedStatement.executeUpdate();
    }

    /**
     * 将SQL语句中的#{}替换为？，源码中是在SqlSourceBuilder类中解析的
     *
     * @param originSql 原始的sql语句
     * @return 处理好的替换好？的sql语句
     */
    private static String parseSymbol(String originSql) {
        originSql = originSql.trim();
        Matcher matcher = PARAM_PATTERN.matcher(originSql);
        return matcher.replaceAll("?");
    }
}
