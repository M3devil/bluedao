package com.bluedao.util;

import java.lang.reflect.Method;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/18 15:10
 * @类职责：反射调用类的set、get方法
 */
public class ReflectUtil {

    /**
     * 调用Object对应属性的get方法
     *
     * @param obj    类对象
     * @param fieldName 与类对象属性对应的数据库字段名
     * @return 类对象的属性
     */
    public static Object invokeGet(Object obj, String fieldName){
        Class<?> clazz = obj.getClass();
        Method method;
        Object res;
        try {
            method = clazz.getDeclaredMethod("get" + ObjectValueUtil.firstUpperCase(fieldName));
            res = method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("[" + Thread.currentThread().getName() + "]" +
                    clazz.getName()+ "--->" +
                    e.getMessage());
        }
        return res;
    }

    /**
     * 调用Object对应属性的get方法
     *
     * @param obj        类对象
     * @param columnName 与类对象属性对应的数据库字段名
     * @param value      属性值
     */
    public static void invokeSet(Object obj, String columnName, Object value){
        Class<?> clazz = obj.getClass();
        Method method;
        try {
            method = clazz.getDeclaredMethod("set"+ObjectValueUtil.columnNameToMethodName(columnName), value.getClass());
            method.setAccessible(true);
            method.invoke(obj,value);
        }catch (Exception e){
            LogUtil.getLogger().error("通过反射将数据库字段值注入类属性失败:",e);
            throw new RuntimeException("[" + Thread.currentThread().getName() + "]" +
                    clazz.getName()+ "--->" + "value = "+
                    value.getClass());
        }
    }
}
