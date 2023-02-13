package com.bluedao.cache;

import java.util.*;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/1/27 12:18
 * @created: Jedis基本数据操作接口
 */
public interface NetCache<K, V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);

    /**
     * 添加字符串
     */
    void setString(String key, String value);

    /**
     * 添加集合
     */
    void setList(List<V> list);

    /**
     * 添加列表
     */
    void setSet(LinkedHashSet<V> set);

    /**
     * 添加Hash
     */
    void setMap(String key, HashMap<K,V> map);

    /**
     * 添加有序集合
     */
    void setZSet(TreeSet<V> set);

    /**
     * 设置失效时间
     */
    void setTimeOut(K key, long time);

}
