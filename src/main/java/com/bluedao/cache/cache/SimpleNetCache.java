package com.bluedao.cache.cache;

import com.bluedao.cache.ConnectionControl;
import com.bluedao.cache.NetCache;
import com.bluedao.util.JsonUtil;
import com.bluedao.util.LogUtil;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/6 21:21
 * @created: Redis缓存
 */
public class SimpleNetCache implements NetCache<String, Map<String, Object>>, ConnectionControl {

    private final Logger logger = LogUtil.getLogger();
    private Jedis jedis;

    public SimpleNetCache(String ip, int port, String password) {
        boolean isConnect = connect(ip, port, password);
        if (isConnect) {
            logger.info("jedis connect to redis is success");
        } else {
            logger.error("jedis connect to redis is error");
        }
    }

    @Override
    public Map<String, Object> get(String key) {
        String value = jedis.get(key);
        return JsonUtil.toMap(value);
    }

    @Override
    public void put(String key, Map<String, Object> value) {
        String jsonString = JsonUtil.toJson(value);
        jedis.set(key, jsonString);
    }

    @Override
    public void remove(String key) {
        long time = jedis.del(key);
        if (time != 0) {
            logger.debug("清除了一条redis缓存数据 :" + key);
        } else {
            logger.debug("没有清除redis缓存数据 :" + key);
        }
    }

    @Override
    public void setString(String key, String value) {
        jedis.set(key, value);
    }

    @Override
    public void setList(List<Map<String, Object>> list) {

    }

    @Override
    public void setSet(LinkedHashSet<Map<String, Object>> set) {

    }

    public void setMap(String key, HashMap<String, Map<String, Object>> map) {
        Map<String, Object> objectMap = map.get(key);
        String jsonString = JsonUtil.toJson(objectMap);
        jedis.set(key, jsonString);
    }

    @Override
    public void setZSet(TreeSet<Map<String, Object>> set) {

    }

    @Override
    public void setTimeOut(String key, long time) {
        jedis.expireAt(key, time);
    }

    @Override
    public boolean connect(Object... params) {
        String ip = "";
        String password = "";
        int port = -1;
        if (params[0] instanceof String) {
            ip = (String) params[0];
        }
        if (params[1] instanceof Integer) {
            port = (int) params[1];
        }
        if (params[2] instanceof String) {
            password = (String) params[2];
        }
        if("".equals(ip) || port == -1) {
            return false;
        }

        jedis = new Jedis(ip, port);

        if(!"".equals(password)) {
            jedis.auth(password);
        }
        return true;
    }

    @Override
    public Object ping() {
        return jedis.ping();
    }

    @Override
    public boolean close() {
        try {
            jedis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
