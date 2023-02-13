package com.bluedao.crud.sql;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/9 23:36
 * @created: sql关键字枚举
 */
public enum SqlKeyword {
    /**
     * SQL关键字
     */
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    ASC("ASC"),
    DESC("DESC"),
    DISTINCT("DISTINCT");

    /**
     * 关键字字符
     */
    private final String keyword;

    /**
     * 获取关键字字符
     */
    public String getSqlSegment() {
        return this.keyword;
    }

    /**
     * 私有构造方法
     */
    private SqlKeyword(final String keyword) {
        this.keyword = keyword;
    }

}
