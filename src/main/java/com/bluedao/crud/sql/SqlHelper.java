package com.bluedao.crud.sql;

import com.bluedao.crud.condition.ColumnCondition;
import com.bluedao.crud.condition.TableCondition;
import com.bluedao.crud.condition.WhereCondition;

import java.util.List;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/11 13:17
 * @created: SQL生成器 各种拼接
 */
public class SqlHelper {

    private static final StringBuffer sb = new StringBuffer();

    /**
     * 生成sql
     * @param entityClass 实体类
     * @param whereCondition where条件
     * @param tableCondition table条件
     * @param columnCondition 列条件
     * @return sql
     * @param <T> 实体类型
     */
    public static <T> String generateSql(Class<T> entityClass, List<WhereCondition> whereCondition, List<TableCondition> tableCondition, List<ColumnCondition> columnCondition) {
        return sb.toString();
    }

    /**
     * 通过主键生成SQL
     */
    public static void generateSelectSql() {

    }

}
