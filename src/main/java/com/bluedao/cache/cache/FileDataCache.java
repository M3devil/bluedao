package com.bluedao.cache.cache;


import com.bluedao.cache.LocalCache;
import com.bluedao.cache.entity.FileData;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Senn
 * @create 2022/2/4 20:44
 */
public class FileDataCache implements LocalCache<String, FileData> {

    private static volatile FileDataCache fileDataCache;

    private static LoadingCache<String, FileData> CACHE;

    public static FileDataCache getInstance() {
        if (fileDataCache == null) {
           throw new RuntimeException("file cache not init...");
        }
        return fileDataCache;
    }

    public static void init(CacheLoader<String, FileData> cacheLoader) {
        CACHE = CacheBuilder
                .newBuilder()
                .softValues()
                .initialCapacity(100)
                .maximumSize(500)
                .recordStats()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build(cacheLoader);
        fileDataCache = new FileDataCache();
    }


    private FileDataCache() {
    }

    @Override
    public FileData get(String key) {
        try {
            return CACHE.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException("the key named :" + key + "is not in cache" + e);
        }
    }

    public FileData getIfPresent(String key) {
        return CACHE.getIfPresent(key);
    }

    @Override
    public void put(String key , FileData fileData) {
        CACHE.put(key, fileData);
    }

    @Override
    public long size() {
        return CACHE.size();
    }

    @Override
    public void remove(String key) {
        CACHE.invalidate(key);
    }

    @Override
    public void clear() {
        CACHE.invalidateAll();
    }

    @Override
    public void stats() {
        CACHE.stats();
    }
}
