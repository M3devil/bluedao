package com.bluedao.session;

import com.bluedao.util.LogUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 10:37
 * @类职责：构建者模式构建sqlSessionFactory对象
 */
public class SqlSessionFactoryBuilder {

    private final Logger LOGGER = LogUtil.getLogger();

    /**
     * 读取配置文件，创建sqlSessionFactory工厂
     * 将配置文件解析成输入流
     * @param configFileName 配置文件
     * @return 工厂对象
     */
    public SqlSessionFactory build(String configFileName){
        return build(SqlSessionFactoryBuilder.class.getClassLoader().getResourceAsStream(configFileName));
    }

    public SqlSessionFactory build(InputStream inputStream){
        try {
            Configuration.getProps().load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DefaultSqlSessionFactory defaultSqlSessionFactory = DefaultSqlSessionFactory.getInstance(new Configuration());
        LOGGER.debug("SqlSessionFactory has been build!");
        return defaultSqlSessionFactory;
    }
}
