package com.bluedao.cache.filter;

import com.bluedao.cache.BloomFilter;

import java.util.BitSet;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/6 22:11
 * @created: 字符串布隆过滤器
 */
public class StringBloomFilter implements BloomFilter<String> {

    /**
     * 存储散列值列表
     */
    private BitSet bitSet;

    /**
     * 布隆过滤器最大容量
     */
    private int size;

    /**
     * 使用Hash的位数
     */
    private int[] seeds;

    public StringBloomFilter() {
    }

    public StringBloomFilter(int capacity) {
        this(SeedsEnum.MIDDLE, capacity);
    }

    public StringBloomFilter(SeedsEnum seedsEnum, int capacity) {
        this.size = seedsEnum.seeds.length * capacity;
        this.bitSet = new BitSet(this.size);
        this.seeds = seedsEnum.seeds;
    }


    @Override
    public void add(String str) {
        for(int seed : seeds) {
            setBitSet(hash(str, seed));
        }
    }

    @Override
    public boolean contain(String str) {
        boolean isExit = true;

        for(int seed : seeds) {
            if(!bitSet.get(hash(str, seed))) {
                isExit = false;
                break;
            }
        }
        return isExit;
    }

    /**
     * 如果元素存在则返回true，不存在则增加并返回false
     */
    public boolean addIfNoExit(String element) {
        boolean exit = true;
        int[] hashCodes = new int[seeds.length];

        for (int i = 0; i < seeds.length; i++) {
            //计算hash值
            int hashCode = hash(element, seeds[i]);
            hashCodes[i] = hashCode;
            //遍历整个hash判断，如果存在就去bitSet中判断当前hash是否为true，如果为fale，说明不存在，
            //那么当前hash值及之前都要存入bitSet中，if(exit)会自动将剩余hash值放入bitSet中
            if (exit) {
                if (!bitSet.get(hashCode)) {
                    exit = false;
                    //补充原有
                    for (int j = 0; j < i + 1; j++) {
                        setBitSet(hashCodes[j]);
                    }
                }
            } else {
                setBitSet(hashCode);
            }

        }
        return exit;
    }

    /**
     * hash操作函数
     * @return hash后的结果
     */
    private int hash(String element, int seed) {
        char[] chars = element.toCharArray();
        int hashCode = 0;

        for (int i = 0; i < chars.length; i++) {
            hashCode = i * hashCode + chars[i];
        }

        hashCode = hashCode * seed % size;
        // 防止溢出变成负数
        return Math.abs(hashCode);
    }

    private void setBitSet(int hashCode) {
        bitSet.set(hashCode, true);
    }

    /**
     * Hash位数类型的枚举,分配位数越多误判的概率越低，占用的内存更大
     */
    public enum SeedsEnum {
        /**
         * 每个字符串分配4个位
         */
        VERY_SMALL(new int[]{2, 3, 5, 7}),
        /**
         * 每个字符串分配8个位
         */
        SMALL(new int[]{2, 3, 5, 7, 11, 13, 17, 19}),
        /**
         * 每个字符串分配16个位
         */
        MIDDLE(new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53}),
        /**
         * 每个字符串分配32个位
         */
        HIGH(new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
                101, 103, 107, 109, 113, 127, 131});

        private int[] seeds;

        private SeedsEnum(int[] seeds) {
            this.seeds = seeds;
        }

        public int[] getSeeds() {
            return seeds;
        }

        public void setSeeds(int[] seeds) {
            this.seeds = seeds;
        }
    }
}
