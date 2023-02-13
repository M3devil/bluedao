package com.bluedao.crud.mapper;

import com.bluedao.crud.sql.SqlCondition;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/11 13:38
 * @created: BaseMapper接口
 */
public interface BaseMapper<T> {

    int insert(T entity);

    int deleteById(Serializable id);

    int deleteById(T entity);

    int deleteByMap(Map<String, Object> columnMap);

    int delete(SqlCondition<T> queryCondition);

    int deleteBatchIds(Collection<?> idList);

    int updateById(T entity);

    int update(T entity, SqlCondition<T> updateCondition);

    T selectById(Serializable id);

    List<T> selectBatchIds(Collection<?> idList);

    List<T> selectByMap(Map<String, Object> columnMap);

    default T selectOne(SqlCondition<T> queryWrapper) {
        List<T> list = this.selectList(queryWrapper);
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    Long selectCount(SqlCondition<T> queryWrapper);

    List<T> selectList(SqlCondition<T> queryWrapper);

    List<Map<String, Object>> selectMaps(SqlCondition<T> queryWrapper);

    List<Object> selectObjs(SqlCondition<T> queryWrapper);

}
