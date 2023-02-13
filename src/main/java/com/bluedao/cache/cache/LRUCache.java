package com.bluedao.cache.cache;

import com.bluedao.cache.BloomFilter;
import com.bluedao.cache.LocalCache;
import com.bluedao.cache.build.LocalCacheBuilder;
import com.bluedao.cache.iterator.CacheIterator;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * v1.0
 * 基于 双向链表 + HashMap 的 LRU 算法实现，对算法的解释如下：
 * 访问某个节点时，将其从原来的位置删除，并重新插入到链表头部。这样就能保证链表尾部存储的
 * 就是最近最久未使用的节点，当节点数量大于缓存最大空间时就淘汰链表尾部的节点。
 * 为了使删除操作时间复杂度为 O(1)，就不能采用遍历的方式找到某个节点。HashMap 存储着 Key
 * 到节点的映射，通过 Key 就能以 O(1) 的时间得到节点，然后再以 O(1) 的时间将其从双向队列中删除。
 *
 * v2.0
 * 并发 读写锁 + synchronized 分段锁

 * v3.0
 * 升级 使用冷热链表
 * 布隆过滤器
 *
 * v4.0
 * 锁条件下移
 *
 * v5.0
 * 扩展
 *
 * @author Senn
 * @create 2022/3/3 20:39
 */
public class LRUCache<K, V> implements LocalCache<K, V> {

    /**
     * K V 映射
     */
    protected final Map<K, Node<K, V>> cache;

    /**
     * 热数据 大小0
     */
    protected AtomicInteger hotSize;

    /**
     * 冷数据 大小
     */
    protected AtomicInteger coldSize;

    /**
     * 热数据阈值
     */
    protected int hotThreshold;

    /**
     * 冷数据阈值
     */
    protected int coldThreshold;

    /**
     * 默认 冷数据升级 时间间隔
     */
    protected int interval;

    /**
     * 头节点
     */
    protected final Node<K, V> head;

    /**
     * 冷头节点
     */
    protected final Node<K, V> coldHead;

    /**
     * 尾节点
     */
    protected final Node<K, V> tail;

    /**
     * 热点数据迭代器
     */
    private final CacheIterator<K, V> hotEntry;

    /**
     * 冷数据迭代器
     */
    private final CacheIterator<K, V> coldEntry;

    /**
     * 布隆过滤器
     */
    private final BloomFilter<K> filter;

    /**
     * 节点
     */
    public static class Node<K, V> {
        K key;
        V value;
        Node<K, V> pre;
        Node<K, V> next;
        public Node(){}
        public Node(K k, V v) {
            key = k;
            value = v;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }
    }

    /**
     * 冷节点
     */
    public static class ColdNode<K, V> extends Node<K, V> {
        long interval;

        public ColdNode() {
        }

        public ColdNode(K k, V v, long interval) {
            super(k, v);
            this.interval = interval;
        }

        long getInterval() {
            return interval;
        }
        void setInterval(long interval) {
            this.interval = interval;
        }
    }

    /**
     * 使用建造者模式创建
     * @param builder 建造者
     */
    public LRUCache(LocalCacheBuilder<K, V> builder) {
        this.coldThreshold = builder.getColdThreshold();
        this.hotThreshold = builder.getHotThreshold();
        this.interval = builder.getInterval();
        this.filter = builder.getFilter();
        this.cache = builder.getMap();
        this.hotSize = new AtomicInteger(0);
        this.coldSize = new AtomicInteger(0);
        head = new Node<K, V>();
        tail = new ColdNode<K, V>();
        coldHead = new ColdNode<K, V>();
        head.next = coldHead;
        coldHead.pre = head;
        coldHead.next = tail;
        tail.pre = coldHead;
        hotEntry = new CacheIterator<K, V>(head);
        coldEntry = new CacheIterator<K, V>(coldHead);
    }

    /**
     * 获取value
     * @param key key
     * @return 无 则 null
     */
    @Override
    public V get(K key) {
        Node<K, V> node = cache.get(key);
        if (node == null) {
            return null;
        }
        //判断是否为 冷节点
        if (node instanceof ColdNode) {
            ColdNode<K, V> coldNode = (ColdNode<K, V>) node;
            if (needToHot(coldNode)) {
                moveToHotHead(coldNode);
                coldSize.decrementAndGet();
                hotSize.incrementAndGet();
                if (hotSize.get() >= hotThreshold) {
                    moveFromHotToColdHead(coldHead.pre);
                }
            }else {
                moveToColdHead(coldNode);
            }
        }else {
            moveToHotHead(node);
        }
        afterNodeAccess(node);
        return node.value;

    }

    /**
     * 添加缓存
     * @param key key
     * @param value value
     */
    @Override
    public void put(K key, V value) {
        Node<K, V> node = cache.get(key);
        if (node == null) {
            ColdNode<K, V> newNode = new ColdNode<K, V>(key, value, localTime());
            addToHead(newNode, coldHead);
            coldSize.incrementAndGet();
            // 删除 尾节点 保持冷链表个数
            while (coldSize.get() >= coldThreshold) {
                Node<K, V> tail = removeTail();
                cache.remove(tail.key);
                coldSize.decrementAndGet();
                afterNodeRemoval(tail);
            }
            node = cache.put(key, newNode);
        } else {
            node.value = value;
        }
        afterNodeInsertion(node != null || cache.containsKey(key));
    }

    @Override
    public long size() {
        return hotSize.get() + coldSize.get();
    }

    @Override
    public void remove(K key) {
        Node<K, V> node = cache.get(key);
        removeNode(node);
    }

    @Override
    public void clear() {

    }

    @Override
    public void stats() {

    }

    /**
     * 判断是否可热化
     * @param coldNode 数据
     * @return 是否热化
     */
    protected boolean needToHot(ColdNode<K, V> coldNode) {
        return localTime() - coldNode.getInterval() >= this.interval;
    }

    /**
     * 移动到热点数据头
     * @param node 数据
     */
    protected void moveToHotHead(Node<K,V> node) {
        synchronized (head){
            removeNode(node);
            if (node instanceof ColdNode) {
                node = new Node<>(node.key, node.value);
            }
            addToHead(node, head);
        }
    }

    /**
     * 从冷数据移动到 头
     * @param node 冷数据
     */
    protected void moveToColdHead(ColdNode<K,V> node) {
        synchronized (coldHead) {
            removeNode(node);
            addToHead(node, coldHead);
        }
    }

    /**
     * 从 热点数据移动到冷数据
     * @param node 热点数据
     */
    protected void moveFromHotToColdHead(Node<K,V> node) {
        synchronized (coldHead){
            removeNode(node);
            node = new ColdNode<>(node.key, node.value, localTime());
            addToHead(node, coldHead);
        }
    }

    /**
     * 删除尾节点 （tail 前一节点）
     * @return  删除的节点
     */
    protected Node<K, V> removeTail() {
        synchronized (tail) {
            Node<K, V> temp = tail.pre;
            filter.remove(temp.key);
            removeNode(temp);
            cache.remove(temp.key);
            return temp;
        }
    }

    /**
     * 删除节点
     * @param node 节点
     */
    protected void removeNode(Node<K, V> node) {
        node.pre.next = node.next;
        node.next.pre = node.pre;
    }

    /**
     * 将节点添加 头
     * @param node 节点
     */
    protected void addToHead(Node<K, V> node, Node<K, V> head) {
        synchronized (head) {
            node.pre = head;
            node.next = head.next;
            head.next.pre = node;
            head.next = node;
        }
    }

    /**
     * 获取当前时间
     * @return localTime
     */
    private long localTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取热点数据迭代器
     * @return 热点数据迭代器
     */
    public CacheIterator<K, V> getHotEntry(){
        hotEntry.setCapacity(hotSize.get());
        return hotEntry;
    }

    /**
     * 获取冷数据迭代器
     * @return 冷数据迭代器
     */
    public CacheIterator<K, V> getColdEntry(){
        coldEntry.setCapacity(coldSize.get());
        return coldEntry;
    }

    void afterNodeAccess(Node<K, V> p) { }
    void afterNodeInsertion(boolean evict) { }
    void afterNodeRemoval(Node<K, V> p) { }
}
