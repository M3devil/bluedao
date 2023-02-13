package com.bluedao.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/7 21:58
 * @created: json工具
 */
public class JsonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = LogUtil.getLogger();

    public static String toJson(Object value) {
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(value + " ->error to json, because " + e);
        }
        return jsonString;
    }

    public static Map<String, Object> toMap(String jsonString) {
        if(jsonString == null || "".equals(jsonString)) {
            return null;
        }
        Map<String, Object> map;
        try {
            map = mapper.readValue(jsonString, HashMap.class);
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException("error to convert to Map : "+ e.getMessage());
        }
        return map;
    }

    public static <T> T toJavaBean(String jsonString, Class<T> tClass) {
        T t;
        try {
            t = mapper.readValue(jsonString, tClass);
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException("error to convert to " + tClass.getName() + " : "+e);
        }
        return t;
    }
}
