package com.bluedao.callback;

import com.bluedao.bean.ColumnInfo;
import com.bluedao.bean.MappedStatement;
import com.bluedao.bean.TableInfo;
import com.bluedao.executor.statement.StatementHandler;

import javax.security.auth.callback.Callback;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 14:46
 * @类职责：回调接口，默认实现，不必重写全部方法
 */
public interface MyCallback extends Callback {

    /**
     * 具体的数据库操作代码，区分查询和更新
     * @param statementHandler 执行sql操作的对象
     * @param preparedStatement 填充处理好的sql对象
     * @return 返回List集合或者受影响的行数
     */
    default Object doExecutor(StatementHandler statementHandler, PreparedStatement preparedStatement){
        return null;
    }

    /**
     * 通过传入的po类来构建增删改sql语句
     * @param fields 该类的属性
     * @param tableInfo 表名
     * @param primaryKeys 主键
     * @param sql 拼接的sql语句
     * @param mappedStatement 封装sql信息的对象
     * @param params po类传入的参数
     */
    default void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params){

    }
}
