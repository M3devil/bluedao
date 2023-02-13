package com.bluedao.binding;

import com.bluedao.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/17 10:02
 */
public class MapperRegistry {

    /**
     * 存储代理mapper的工厂
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    /**
     * 注册代理工厂
     * @param type 被代理的接口的字节码类型
     * @param <T> 接口类型
     */
    public <T> void addMapper(Class<T> type){
        this.knownMappers.put(type, new MapperProxyFactory<>(type));
    }

    /**
     * 获取工厂实例
     * @param type 被代理接口类的字节码类型
     * @param sqlSession 当前的sqlSession对象
     * @param <T> 接口类型
     * @return 接口的代理
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession){
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) this.knownMappers.get(type);
        return mapperProxyFactory.newInstance(sqlSession);
    }

    public Map<Class<?>, MapperProxyFactory<?>> getKnownMappers() {
        return knownMappers;
    }
}
