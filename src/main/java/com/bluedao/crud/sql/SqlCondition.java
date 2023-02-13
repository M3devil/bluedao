package com.bluedao.crud.sql;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/9 23:42
 * @created: sql条件规范方法
 */
public interface SqlCondition<SQL> {

    SQL eq(String column, Object value);

    SQL gt(String column, Object value);

    SQL lt(String column, Object value);

    SQL ne(String column, Object value);

    SQL and(String column, Object value);

    SQL or(String column, Object value);

    SQL not(String column, Object value);

    SQL in(String column, Object value);

    SQL notIn(String column, Object value);

    SQL ge(String column, Object value);

    SQL le(String column, Object value);

    SQL isNull(String column, Object value);

    SQL isNotNull(String column, Object value);

    SQL having(String column, Object value);

    SQL group(String column, Object value);

    SQL order(String column, Object value);

    SQL exists(String column, Object value);

    SQL notExists(String column, Object value);

    SQL between(String column, Object value);

    SQL notBetween(String column, Object value);

    SQL asc(String column, Object value);

    SQL desc(String column, Object value);

    SQL distinct(boolean isOpenDistinct);

    String buildOrGet();
}
