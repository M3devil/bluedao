package com.bluedao.crud.service;

import com.bluedao.crud.mapper.BaseMapper;

/**
 * @author FireRain
 * @version 1.0
 * @date 2023/2/11 13:38
 * @created: BaseService实现类
 */
public class BaseServiceImpl<T, M extends BaseMapper<T>> implements BaseService<T> {

    protected T entity;

    protected Class<T> entityClass;

    protected M baseMapper;

    @Override
    public BaseMapper<T> getBaseMapper() {
        return this.baseMapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return this.entityClass;
    }
}
