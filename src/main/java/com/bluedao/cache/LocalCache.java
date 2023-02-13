package com.bluedao.cache;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/1/27 12:18
 * @created: 本地缓存操作接口
 */
public interface LocalCache<K, V> {

    /**
     * 存数据
     */
    void put(K key, V value);

    /**
     * 取数据
     */
    V get(K key);

    /**
     * 获取数据大小
     */
    long size();

    /**
     * 清除一个值
     */
    void remove(K key);

    /**
     * 清空数据
     */
    void clear();

    /**
     * 显示缓存状态信息
     */
    void stats();
}
