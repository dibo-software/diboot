package com.diboot.core.service;

import java.io.Serializable;
import java.util.Collection;

/**
 * 通用服务接口Service
 * @author mazc@dibo.ltd
 * @version 3.0
 * @date 2023/05/25
 */
public interface GeneralService<T> {

    /**
     * 创建Entity实体
     * @param entity
     * @return true:成功, false:失败
     */
    boolean createEntity(T entity);

    /***
     * 批量创建Entity
     * @param entityList 实体对象列表
     * @return true:成功, false: 失败
     */
    boolean createEntities(Collection entityList);

    /**
     * 更新Entity实体
     * @param entity
     * @return
     */
    boolean updateEntity(T entity);

    /**
     * 批量更新entity
     * @param entityList
     * @return
     */
    boolean updateEntities(Collection entityList);

    /***
     * 创建或更新entity（entity.id存在则新建，否则更新）
     * @param entity
     * @return
     */
    boolean createOrUpdateEntity(T entity);

    /**
     * 批量创建或更新entity（entity.id存在则新建，否则更新）
     * @param entityList
     * @return
     */
    boolean createOrUpdateEntities(Collection entityList);

    /**
     * 根据主键删除实体
     * @param id 主键
     * @return true:成功, false:失败
     */
    boolean deleteEntity(Class<T> entityClass, Serializable id);

    /**
     * 批量删除指定id的实体
     * @param entityIds
     * @return
     * @throws Exception
     */
    boolean deleteEntities(Class<T> entityClass, Collection<? extends Serializable> entityIds);

}