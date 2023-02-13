package com.bluedao.crud.sql;

import com.bluedao.session.DefaultSqlSessionFactory;
import com.bluedao.session.SqlSession;
import com.bluedao.session.SqlSessionFactory;
import com.bluedao.session.SqlSessionFactoryBuilder;
import com.bluedao.util.LogUtil;
import org.slf4j.Logger;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/9 23:12
 * @created: Sql运行
 */
public class SqlRunner {

    private static final Logger logger = LogUtil.getLogger();

    private static final String APPLICATION_LOCATION = "application.properties";

    private static final SqlSessionFactory SQL_SESSION_FACTORY;

    static {
        SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(APPLICATION_LOCATION);
        logger.debug("Single SqlSessionFactory has been built successfully!");
    }

    /**
     * 获取到单机Session
     */
    public static SqlSession getSqlSession() {
        SqlSession sqlSession = SQL_SESSION_FACTORY.openSession();
        logger.debug("open a new SqlSession Instance from factory!");
        return sqlSession;
    }

}
