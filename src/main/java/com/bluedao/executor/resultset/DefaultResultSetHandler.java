package com.bluedao.executor.resultset;

import com.bluedao.bean.MappedStatement;
import com.bluedao.util.ObjectValueUtil;
import com.bluedao.util.ReflectUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/19 15:18
 */
public class DefaultResultSetHandler implements ResultSetHandler {

    private final MappedStatement mappedStatement;

    public DefaultResultSetHandler(MappedStatement mappedStatement) {
        this.mappedStatement = mappedStatement;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> handlerResult(ResultSet resultSet) {
        try {
            //真正返回的泛型结果集
            List<E> result = new ArrayList<>();
            //中间转换的Object结果集
            List<Object> tempResult = new ArrayList<>();
            if (ObjectValueUtil.isEmpty(resultSet)){
                return null;
            }
            //获取数据库元数据
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                String rowObjectType = mappedStatement.getReturnType();
                //基本数据类型的包装类无需反射
                Object temp;
                //基本类型判断:int、byte、float、boolean等基本数据类型无法转为泛型，所以需要进行特殊处理
                if (rowObjectType.equals(Long.class.getName())||
                        rowObjectType.equals(Double.class.getName())){
                    //getObject的结果默认转换为基本类型的高精度类型Long/Double，而高精度无法转低精度，所以需要特殊处理
                    result.add((E) resultSet.getObject(1));
                }else if (rowObjectType.equals(Integer.class.getName())){
                    temp = resultSet.getInt(1);
                    tempResult.add(temp);
                }else if (rowObjectType.equals(Float.class.getName())){
                    temp = resultSet.getFloat(1);
                    tempResult.add(temp);
                }else if (rowObjectType.equals(Byte.class.getName())){
                    temp = resultSet.getByte(1);
                    tempResult.add(temp);
                }else if (rowObjectType.equals(Boolean.class.getName())){
                    temp = resultSet.getBoolean(1);
                    tempResult.add(temp);
                }else {
                    //集合类型
                    //通过反射给po类设置值
                    E rowObject = (E) Class.forName(rowObjectType).newInstance();
                    for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                        String columnName = resultSetMetaData.getColumnLabel(i + 1);
                        Object columnValue = resultSet.getObject(i + 1);

                        if (!ObjectValueUtil.isEmpty(columnValue)){
                            ReflectUtil.invokeSet(rowObject, columnName, columnValue);
                        }
                    }
                    result.add(rowObject);
                }
            }
            //结果转化为泛型
            result = result.size() == 0 ? transformResultTypeToE(tempResult) : result;
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将Object的list结果集转换为E类型
     * @param tempResult 临时结果集
     * @param <E> 泛型
     */
    @SuppressWarnings("unchecked")
    private <E> List<E> transformResultTypeToE(List<Object> tempResult) {
        List<E> result = new ArrayList<>();
        for (Object obj : tempResult) {
            result.add((E)obj);
        }
        return result;
    }
}
