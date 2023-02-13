package com.bluedao.util;

import com.bluedao.session.Configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/9 16:07
 * @created: 对象转换工具类
 */
public class ObjectConvertUtil {

    private static final String ENTITY = "entityClass";

    public static <E> List<E> toGenericity(List<Map<String, Object>> list) {
        Set<Class<?>> classSet = Configuration.getTableInfoMap().keySet();
        Set<String> keySet = list.get(0).keySet();
        List<E> res = new ArrayList<>();
        // 遍历entity
        classSet.forEach(c -> {
            // 寻找实体类
            Field[] fields = c.getDeclaredFields();
            int size = keySet.size();
            for (Field field : fields) {
                if (keySet.contains(field.getName())) {
                    size--;
                }
            }

            // 实体类赋值
            if(size == 0) {
                list.forEach(l -> {
                    try {
                        Object instance = c.newInstance();
                        for (Field field : fields) {
                            String fieldName = field.getName();
                            if(ENTITY.equals(fieldName)) {
                                continue;
                            }
                            ReflectUtil.invokeSet(instance, fieldName, l.get(fieldName));
                        }
                        res.add((E) instance);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        });
        return res;
    }

}
