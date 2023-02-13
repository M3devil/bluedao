package com.bluedao.crud.entity;

import com.bluedao.crud.sql.SqlCondition;
import com.bluedao.crud.sql.SqlRunner;
import com.bluedao.session.SqlSession;

import java.io.Serializable;
import java.util.List;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/9 22:58
 * @created: Entity
 */
public abstract class Entity<T> {

    private final Class<?> entityClass = this.getClass();

    public boolean insert() {
        return getSqlSession().insert(this) != 0;
    }

    public boolean deleteById(Serializable id) {
        return false;
    }

    public boolean deleteById() {
        return false;
    }

    public boolean delete(SqlCondition<T> condition){
        return false;
    }

    public boolean updateById() {
        return false;
    }

    public boolean update(SqlCondition<T> condition) {
        return false;
    }

    public T selectById() {
        return null;
    }

    public T selectById(Serializable id) {
        return null;
    }

    public T selectOne(SqlCondition<T> condition) {
        return null;
    }

    public List<T> selectList(SqlCondition<T> condition) {
        return null;
    }

    public long selectCount(SqlCondition<T> condition) {
        return 0;
    }

    public List<T> selectAll() {
        return null;
    }

    protected SqlSession getSqlSession() {
        return SqlRunner.getSqlSession();
    }

    protected void closeSqlSession(SqlSession sqlSession) {
        sqlSession.close();
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
