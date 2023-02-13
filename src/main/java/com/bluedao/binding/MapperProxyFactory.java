package com.bluedao.binding;

import com.bluedao.session.SqlSession;

import java.lang.reflect.Proxy;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/7/19 15:59
 */
public class MapperProxyFactory<T>{

    /**
     * 被代理的接口
     */
    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> type) {
        this.mapperInterface = type;
    }

    /**
     * 创建mapper代理对象
     * @param sqlSession 当前会话
     * @return 代理实现类
     */
    public T newInstance(SqlSession sqlSession) {
        MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, this.mapperInterface);
        return newInstance(mapperProxy);
    }

    /**
     * 根据mapper代理的类型返回对应的代理实例
     * @param mapperProxy 代理对象
     * @return 代理对象
     */
    public T newInstance(MapperProxy<T> mapperProxy){
        return (T) Proxy.newProxyInstance(this.mapperInterface.getClassLoader(),
                new Class[]{this.mapperInterface},
                mapperProxy);
    }

//    public T newCGInstance(MapperProxy<T> mapperProxy){
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(this.mapperInterface);
//        enhancer.setCallback(new MyMethodInterceptor(mapperProxy));
//        return (T) enhancer.create();
//    }

}
