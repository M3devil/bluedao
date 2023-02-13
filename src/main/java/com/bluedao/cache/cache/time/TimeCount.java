package com.bluedao.cache.cache.time;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/9 13:47
 * @created: 计数
 */
public class TimeCount {

    private long all;

    private long hit;

    public TimeCount() {
        this.all = 0;
        this.hit = 0;
    }

    public void hitIncrease() {
        hit++;
        all++;
    }

    public void hitFailed() {
        all++;
    }

    public String print() {
        return "总查询次数:" + all + ", 总命中次数:" + hit + ", 命中率为"+ getHitRate() * 100 + "%";
    }

    public long getAll() {
        return all;
    }

    public long getHit() {
        return hit;
    }

    public double getHitRate() {
        return (double) hit / all;
    }
}
