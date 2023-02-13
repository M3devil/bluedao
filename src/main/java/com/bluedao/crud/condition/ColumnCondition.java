package com.bluedao.crud.condition;

import java.util.List;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/11 16:13
 * @created:
 */
public class ColumnCondition {

    private String tableName;

    private List<String> columnName;

    public ColumnCondition() {
    }

    public ColumnCondition(String tableName, List<String> columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColumnName() {
        return columnName;
    }

    public void setColumnName(List<String> columnName) {
        this.columnName = columnName;
    }
}
