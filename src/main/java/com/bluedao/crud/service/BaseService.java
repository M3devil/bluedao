package com.bluedao.crud.service;

import com.bluedao.crud.mapper.BaseMapper;
import com.bluedao.crud.sql.SqlCondition;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/11 13:38
 * @created: Service CRUD
 */
public interface BaseService<T> {

    default boolean save(T entity) {
        return false;
    }

    default boolean saveBatch(Collection<T> entityList) {
        return false;
    }

    default boolean removeById(Serializable id) {
        return false;
    }

    default boolean removeById(T entity) {
        return false;
    }

    default boolean removeByMap(Map<String, Object> columnMap) {
        return false;
    }

    default boolean remove(SqlCondition<T> querySqlCondition) {
        return false;
    }

    default boolean removeByIds(Collection<?> list) {
        return false;
    }

    default boolean removeBatchByIds(Collection<?> list) {
        return false;
    }

    default boolean removeBatchByIds(Collection<?> list, int batchSize) {
        return false;
    }

    default boolean updateById(T entity) {
        return false;
    }

    default boolean update(SqlCondition<T> updateSqlCondition) {
        return false;
    }

    default boolean update(T entity, SqlCondition<T> updateSqlCondition) {
        return false;
    }

    default boolean updateBatchById(Collection<T> entityList) {
        return false;
    }

    default T getById(Serializable id) {
        return null;
    }

    default List<T> listByIds(Collection<? extends Serializable> idList) {
        return null;
    }

    default List<T> listByMap(Map<String, Object> columnMap) {
        return null;
    }

    default T getOne(SqlCondition<T> querySqlCondition) {
        return null;
    }

    default long count() {
        return 0;
    }

    default long count(SqlCondition<T> querySqlCondition) {
        return 0;
    }

    default List<T> list(SqlCondition<T> querySqlCondition) {
        return null;
    }

    default List<T> list() {
        return null;
    }

    default List<Map<String, Object>> listMaps(SqlCondition<T> querySqlCondition) {
        return null;
    }

    default List<Map<String, Object>> listMaps() {
        return null;
    }

    default List<Object> listObjs() {
        return null;
    }

    default <V> List<V> listObjs(Function<? super Object, V> mapper) {
        return null;
    }

    default List<Object> listObjs(SqlCondition<T> querySqlCondition) {
        return null;
    }

    default <V> List<V> listObjs(SqlCondition<T> querySqlCondition, Function<? super Object, V> mapper) {
        return null;
    }

    BaseMapper<T> getBaseMapper();


    Class<T> getEntityClass();
}
