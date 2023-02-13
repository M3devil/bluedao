package com.bluedao.binding;

import com.bluedao.bean.Constant;
import com.bluedao.bean.MappedStatement;
import com.bluedao.session.SqlSession;
import com.bluedao.util.LogUtil;
import org.slf4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/19 16:05
 */
public class MapperProxy<T> implements InvocationHandler {

    private final SqlSession sqlSession;

    private final Class<T> mapperInterface;

    private static final Logger LOGGER = LogUtil.getLogger();

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return this.execute(method, args);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Object execute(Method method, Object[] args){
        String statementId = this.mapperInterface.getName()+ "." + method.getName();
        MappedStatement mappedStatement = this.sqlSession.getConfiguration().getMappedStatement(statementId);

        Object result = null;
        String sqlType = mappedStatement.getSqlType();

        if (Constant.SqlType.SELECT.value().equals(sqlType)){
            Class<?> returnType = method.getReturnType();
            if (Collection.class.isAssignableFrom(returnType)){
                result = sqlSession.selectList(statementId, args);
            }else {
                result = sqlSession.selectOne(statementId, args);
            }
        }

        if (Constant.SqlType.UPDATE.value().equals(sqlType)||
            Constant.SqlType.INSERT.value().equals(sqlType)||
            Constant.SqlType.DELETE.value().equals(sqlType)){
            result = sqlSession.update(statementId, args);
        }

        return result;
    }
}
