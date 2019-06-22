package com.diboot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.EntityBinder;
import com.diboot.core.binding.EntityListBinder;
import com.diboot.core.binding.FieldBinder;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 基础服务Service
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
public interface BaseService<T> {

    /**
     * 获取Entity实体
     * @param id 主键
     * @return entity
     */
    T getEntity(Serializable id);

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
    boolean createEntities(Collection<T> entityList);

    /**
     * 更新Entity实体
     * @param entity
     * @return
     */
    boolean updateEntity(T entity);

    /**
     * 更新Entity实体（更新符合条件的所有非空字段）
     * @param entity
     * @param updateCriteria
     * @return
     */
    boolean updateEntity(T entity, Wrapper updateCriteria);

    /**
     * 更新Entity实体（仅更新updateWrapper.set指定的字段）
     * @param updateWrapper
     * @return
     */
    boolean updateEntity(Wrapper updateWrapper);

    /***
     * 创建或更新entity（entity.id存在则新建，否则更新）
     * @param entity
     * @return
     */
    boolean createOrUpdateEntity(T entity);

    /**
     * 根据主键删除实体
     * @param id 主键
     * @return true:成功, false:失败
     */
    boolean deleteEntity(Serializable id);

    /**
     * 按条件删除实体
     * @param queryWrapper
     * @return
     */
    boolean deleteEntities(Wrapper queryWrapper) throws Exception;

    /**
     * 获取符合条件的entity记录总数
     * @return
     * @throws Exception
     */
    int getEntityListCount(Wrapper queryWrapper);

    /**
     * 获取model列表
     * @param queryWrapper
     * @return
     * @throws Exception
     */
    List<T> getEntityList(Wrapper queryWrapper);

    /**
     * 获取model列表
     * @param queryWrapper
     * @param pagination
     * @return
     * @throws Exception
     */
    List<T> getEntityList(Wrapper queryWrapper, Pagination pagination);

    /**
     * 获取指定数量的entity记录
     * @param queryWrapper
     * @param limitCount
     * @return
     * @throws Exception
     */
    List<T> getEntityListLimit(Wrapper queryWrapper, int limitCount);

    /**
     * 获取指定属性的Map列表
     * @param queryWrapper
     * @return
     */
    List<Map<String, Object>> getMapList(Wrapper queryWrapper);

    /**
     * 获取指定属性的Map列表
     * @param queryWrapper
     * @param pagination
     * @return
     */
    List<Map<String, Object>> getMapList(Wrapper queryWrapper, Pagination pagination);

    /***
     * 获取键值对的列表，用于构建select下拉选项等
     *
     * @param queryWrapper
     * @return
     */
    List<KeyValue> getKeyValueList(Wrapper queryWrapper);

    /**
     * 获取View Object对象
     * @param id 主键
     * @param voClass vo类
     * @return entity
     */
    <VO> VO getViewObject(Serializable id, Class<VO> voClass);

    /**
     * 根据查询条件获取vo列表
     * @param queryWrapper
     * @param pagination
     * @return
     * @throws Exception
     */
    <VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> voClass);

    /***
     * 绑定字段值到VO列表的元素中
     * @param voList
     * @return
     */
    FieldBinder<T> bindingFieldTo(List voList);

    /***
     * 绑定entity对象到VO列表元素中
     * @param voList
     * @return
     */
    EntityBinder<T> bindingEntityTo(List voList);

    /***
     * 绑定entity对象列表到VO列表元素中(适用于VO-Entity一对多的关联)
     * @param voList vo列表
     * @return
     */
    EntityListBinder<T> bindingEntityListTo(List voList);

}