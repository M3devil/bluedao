package com.bluedao.session;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 10:09
 * @类职责：sqlSession工厂对象接口
 */
public interface SqlSessionFactory {

    /**
     * 创建session对象，开启数据库会话
     * @return 数据库会话对象
     */
    SqlSession openSession();
}
