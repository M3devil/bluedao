package com.bluedao.cache.cache.manage;

import com.bluedao.cache.LocalCache;
import com.bluedao.cache.NetCache;
import com.bluedao.cache.cache.time.TimeCount;
import com.bluedao.session.Configuration;
import com.bluedao.util.LogUtil;
import com.bluedao.util.ObjectConvertUtil;
import com.bluedao.util.ObjectValueUtil;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/11 16:59
 * @created: 缓存管理
 */
public class CacheManage {

    private static final Logger LOGGER = LogUtil.getLogger();

    private final TimeCount timeCount;

    public CacheManage(TimeCount timeCount) {
        this.timeCount = timeCount;
    }

    public  Object searchDataFromCache(LocalCache<String, Map<String, Object>> localCache, NetCache<String, Map<String, Object>> netCache, String namespace, String cacheId) {
        // 先查一级
        Object localData = searchDataFromLocal(localCache, namespace, cacheId);
        if(localData != null) {
            return localData;
        }

        // 后查二级
        Object netData = searchDataFromNet(localCache, netCache, namespace, cacheId);
        if(netData != null) {
            // 放入到一级缓存
            putToLocal(localCache, namespace, cacheId, netData);
            return netData;
        }

        return null;
    }

    public void putDataToCache(LocalCache<String, Map<String, Object>> localCache, NetCache<String, Map<String, Object>> netCache, String namespace, String cacheId, Object result) {
        // 先放一级
        putToLocal(localCache, namespace, cacheId, result);
        // 后放二级
        putToNet(netCache, namespace, cacheId, result);
    }

    private <E> List<E> searchDataFromLocal(LocalCache<String, Map<String, Object>> localCache, String namespace, String cacheId) {
        AtomicReference<Object> data = new AtomicReference<>(null);
        // 一级缓存
        if (localCache != null) {
            Optional.ofNullable(localCache.get(namespace)).map(map -> map.get(cacheId)).ifPresent(data::set);

            if(data.get() != null && !"".equals(data.get())) {
                timeCount.hitIncrease();
                LOGGER.debug("local cache is hit : " + timeCount.print() + ",this hit named: " + namespace + "-->" + cacheId);
                return (List<E>) data.get();
            }
        }

        return null;
    }

    private <E> List<E> searchDataFromNet(LocalCache<String, Map<String, Object>> localCache, NetCache<String, Map<String, Object>> netCache, String namespace, String cacheId) {
        AtomicReference<Object> data = new AtomicReference<>(null);
        // 二级缓存
        if (netCache != null) {
            Optional.ofNullable(netCache.get(namespace)).map(map -> map.get(cacheId)).ifPresent(data::set);

            if (data.get() != null && !"".equals(data.get())) {
                Configuration.getTimeCount().hitIncrease();
                LOGGER.debug("net cache is hit : " + Configuration.getTimeCount().print() + ",this hit named: " + namespace + "-->" + cacheId);
                return ObjectConvertUtil.toGenericity((List<Map<String, Object>>) data.get());
            }
        }

        return null;
    }

    private void putToLocal(LocalCache<String, Map<String, Object>> localCache, String namespace, String cacheId, Object result){
        // 添加缓存
        HashMap<String, Object> map = new HashMap<>();
        boolean goodAnswer = result != null;

        if(localCache != null) {
            // 本地缓存随便添加一下
            Map<String, Object> objectMap = localCache.get(namespace);
            if(ObjectValueUtil.isEmpty(objectMap)) {
                map.put(cacheId, goodAnswer ? result : "");
                localCache.put(namespace, map);
            } else {
                objectMap.put(cacheId, goodAnswer ? result : "");
            }
            timeCount.hitFailed();
//            LOGGER.debug("local cache is not hit :" + timeCount.print() + ",this no hit named: " + namespace + "-->" + cacheId);
        }
    }

    private  void putToNet(NetCache<String, Map<String, Object>> netCache, String namespace, String cacheId, Object result){
        HashMap<String, Object> map = new HashMap<>();
        boolean goodAnswer = result != null;

        if(netCache != null) {
            Map<String, Object> oriMap = netCache.get(namespace);
            if (ObjectValueUtil.isEmpty(oriMap)) {
                map.put(cacheId, goodAnswer ? result : "");
                netCache.put(namespace, map);
            } else {
                oriMap.put(cacheId, goodAnswer ? result : "");
                netCache.put(namespace, oriMap);
            }
            Configuration.getTimeCount().hitFailed();
//            LOGGER.debug("net cache is not hit :" + Configuration.getTimeCount().print() + ",this no hit named: " + namespace + "-->" + cacheId);

            // todo redis缓存失效时间优化

        }
    }

}
