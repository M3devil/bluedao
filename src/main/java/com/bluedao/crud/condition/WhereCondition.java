package com.bluedao.crud.condition;

import com.bluedao.crud.sql.SqlKeyword;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/10 0:16
 * @created: 条件实体
 */
public class WhereCondition {

    private SqlKeyword sqlKeyword;

    private String columnName;

    private Object value;

    public WhereCondition() {
    }

    public WhereCondition(SqlKeyword sqlKeyword, String columnName, Object value) {
        this.sqlKeyword = sqlKeyword;
        this.columnName = columnName;
        this.value = value;
    }

    public SqlKeyword getSqlKeyword() {
        return sqlKeyword;
    }

    public void setSqlKeyword(SqlKeyword sqlKeyword) {
        this.sqlKeyword = sqlKeyword;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
