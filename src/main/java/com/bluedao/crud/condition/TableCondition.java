package com.bluedao.crud.condition;

import com.bluedao.crud.sql.SqlKeyword;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/10 0:22
 * @created: 多表表条件
 */
public class TableCondition {

    private String table;

    private String column;

    private SqlKeyword sqlKeyword;

    private Object value;

    public TableCondition(String table, SqlKeyword sqlKeyword, String column, Object value) {
        this.table = table;
        this.column = column;
        this.sqlKeyword = sqlKeyword;
        this.value = value;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public SqlKeyword getSqlKeyword() {
        return sqlKeyword;
    }

    public void setSqlKeyword(SqlKeyword sqlKeyword) {
        this.sqlKeyword = sqlKeyword;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
