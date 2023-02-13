package com.bluedao.cache.filter;

import java.util.BitSet;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/6 21:59
 * @created: 经典布隆过滤器
 */
public class SimpleBloomFilter<T> implements com.bluedao.cache.BloomFilter<T> {

    // 位数组大小
    private static final int DEFAULT_SIZE = 2 << 24;
    // 位数组
    private final BitSet bitSet = new BitSet(DEFAULT_SIZE);
    // 传入不同的seed创建 6 个不同的哈希函数
    private static final int[] SEEDS = new int[]{3, 13, 46, 71, 91, 134};

    // 生成的六个哈希函数
    private final SimpleHash[] func = new SimpleHash[SEEDS.length];

    /**
     * 静态内部类,用于hash操作
     */
    public static class SimpleHash {
        private final int cap;
        private final int seed;

        public SimpleHash(int cap, int seed) {
            super();
            this.cap = cap;
            this.seed = seed;
        }

        // 根据不同的seed生成不同的哈希函数，并运算获得对应的hash结果值
        public int hash(Object value) {
            int h;
            return (value == null) ? 0 : Math.abs(seed * (cap - 1) & ((h = value.hashCode()) ^ (h >>> 16)));
        }
    }

    public SimpleBloomFilter() {
        for(int i = 0; i < SEEDS.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, SEEDS[i]);
        }
    }

    @Override
    public void add(T t) {
        for(SimpleHash sf : func) {
            bitSet.set(sf.hash(t), true);
        }
    }

    @Override
    public void remove(T t) {
        for (SimpleHash sf : func) {
            bitSet.clear(sf.hash(t));
        }
    }

    @Override
    public boolean contain(T t) {
        boolean ret = true;
        for (SimpleHash f : func) {
            ret = ret && bitSet.get(f.hash(t));
        }
        return ret;
    }
}
