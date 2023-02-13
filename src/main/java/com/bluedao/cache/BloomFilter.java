package com.bluedao.cache;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/1/27 12:19
 * @created: 布隆过滤器规范
 */
public interface BloomFilter<T> {

    /**
     * 添加元素
     */
    void add(T t);

    /**
     * 删除元素
     */
    default void remove(T t) { };

    /**
     * 判断元素是否在集合中
     */
    boolean contain(T t);
}
