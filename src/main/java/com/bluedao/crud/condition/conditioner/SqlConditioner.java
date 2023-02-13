package com.bluedao.crud.condition.conditioner;

import com.bluedao.crud.condition.ColumnCondition;
import com.bluedao.crud.condition.TableCondition;
import com.bluedao.crud.condition.WhereCondition;
import com.bluedao.crud.sql.SqlCondition;
import com.bluedao.crud.sql.SqlHelper;
import com.bluedao.crud.sql.SqlKeyword;
import com.bluedao.util.ObjectValueUtil;

import java.util.*;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/9 23:35
 * @created: 条件构造器抽象父类
 */
public abstract class SqlConditioner<T, SQL extends SqlConditioner<T, SQL>> implements SqlCondition<SQL> {

    /**
     *  当前条件
     */
    protected final SQL mine = (SQL) this;

    /**
     * 查列元素
     */
    protected List<ColumnCondition> columnCondition;

    /**
     * where条件参数集
     */
    protected List<WhereCondition> whereCondition;

    /**
     * 表拼接参数集
     */
    protected List<TableCondition> tableCondition;

    /**
     * 实体类
     */
    protected T entity;

    /**
     * 实体Class
     */
    protected Class<T> entityClass;

    /**
     * 最终的Sql
     */
    protected String finalSql;


    /**
     * 添加条件
     */
    protected SQL addWhereCondition(SqlKeyword sqlKeyword, String column, Object value) {
        whereCondition.add(new WhereCondition(sqlKeyword, column, value));
        return this.mine;
    }

    /**
     * 添加表条件
     */
    protected SQL addTableCondition(String table, SqlKeyword sqlKeyword, String column, Object value) {
        tableCondition.add(new TableCondition(table, sqlKeyword, column, value));
        return this.mine;
    }

    protected SQL addColumnCondition(String table, String[] column) {
        columnCondition.add(new ColumnCondition(table, Arrays.asList(column)));
        return this.mine;
    }

    /**
     * 创建Sql
     */
    @Override
    public String buildOrGet() {
        if (!ObjectValueUtil.isEmpty(finalSql)) {
            return finalSql;
        }
        finalSql = SqlHelper.generateSql(entityClass, whereCondition, tableCondition, columnCondition);
        return finalSql;
    }
}
