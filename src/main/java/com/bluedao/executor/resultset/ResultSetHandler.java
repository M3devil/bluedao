package com.bluedao.executor.resultset;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 14:51
 * @类职责：结果集处理器
 */
public interface ResultSetHandler {

    /**
     * 对查询的结果集进行封装，封装成对应类的List集合
     * @param resultSet 结果集
     * @param <E> 对应类型
     * @return 对应类的list集合
     */
    <E> List<E> handlerResult(ResultSet resultSet);

}
