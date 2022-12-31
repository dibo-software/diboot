/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.diboot.core.util.IGetter;
import com.diboot.core.util.ISetter;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Pagination;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 基础服务Service
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
public interface BaseService<T> {

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    BaseMapper<T> getMapper();

    /**
     * 构建mybatis-plus的query
     * @return
     */
    QueryChainWrapper<T> query();
    /**
     * 构建mybatis-plus的lambdaQuery
     * @return
     */
    LambdaQueryChainWrapper<T> lambdaQuery();
    /**
     * 构建mybatis-plus的update
     * @return
     */
    UpdateChainWrapper<T> update();
    /**
     * 构建mybatis-plus的lambdaUpdate
     * @return
     */
    LambdaUpdateChainWrapper<T> lambdaUpdate();

    /**
     * 获取Entity实体
     * @param id 主键
     * @return entity
     */
    T getEntity(Serializable id);

    /**
     * 获取entity某个属性值
     * @param idGetterFn id getter
     * @param idVal id值
     * @param getterFn 返回属性getter
     * @return
     */
    <FT> FT getValueOfField(SFunction<T, ?> idGetterFn, Serializable idVal, SFunction<T, FT> getterFn);

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
     * 添加entity 及 其关联子项entities
     * @param entity 主表entity
     * @param relatedEntities 关联表entities
     * @param relatedEntitySetter 关联Entity类的setter
     * @return
     */
    <RE, R> boolean createEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter);

    /**
     * 创建或更新n-n关联
     * （在主对象的service中调用，不依赖中间表service实现中间表操作）
     *
     * @param driverIdGetter   驱动对象getter
     * @param driverId         驱动对象ID
     * @param followerIdGetter 从动对象getter
     * @param followerIdList   从动对象id集合
     * @return
     */
    <R> boolean createOrUpdateN2NRelations(SFunction<R, ?> driverIdGetter, Object driverId,
                                           SFunction<R, ?> followerIdGetter, List<? extends Serializable> followerIdList);

    /**
     * 创建或更新n-n关联
     * （在主对象的service中调用，不依赖中间表service实现中间表操作）
     * <p>
     * 可自定附加条件、参数
     *
     * @param driverIdGetter   驱动对象getter
     * @param driverId         驱动对象ID
     * @param followerIdGetter 从动对象getter
     * @param followerIdList   从动对象id集合
     * @param queryConsumer    附加查询条件
     * @param setConsumer      附加插入参数
     * @param <R>              关联对象类型
     * @return
     */
    <R> boolean createOrUpdateN2NRelations(SFunction<R, ?> driverIdGetter, Object driverId,
                                           SFunction<R, ?> followerIdGetter, List<? extends Serializable> followerIdList,
                                           Consumer<QueryWrapper<R>> queryConsumer, Consumer<R> setConsumer);

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

    /**
     * 批量更新entity
     * @param entityList
     * @return
     */
    boolean updateEntities(Collection<T> entityList);

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
     * 添加entity 及 其关联子项entities
     * @param entity 主表entity
     * @param relatedEntities 关联表entities
     * @param relatedEntitySetter 关联Entity类的setter
     * @return
     */
    <RE,R> boolean updateEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter);

    /**
     * 删除entity 及 其关联子项entities
     * @param id 待删除entity的主键
     * @param relatedEntityClass 待删除关联Entity类
     * @param  relatedEntitySetter 待删除类的setter方法
     * @return
     */
    <RE,R> boolean deleteEntityAndRelatedEntities(Serializable id, Class<RE> relatedEntityClass, ISetter<RE, R> relatedEntitySetter);

    /**
     * 根据主键删除实体
     * @param id 主键
     * @return true:成功, false:失败
     */
    boolean deleteEntity(Serializable id);

    /**
     * 根据主键撤销删除
     * @param id
     * @return
     */
    boolean cancelDeletedById(Serializable id);

    /**
     * 按条件删除实体
     * @param queryWrapper
     * @return
     * @throws Exception
     */
    boolean deleteEntities(Wrapper queryWrapper);

    /**
     * 批量删除指定id的实体
     * @param entityIds
     * @return
     * @throws Exception
     */
    boolean deleteEntities(Collection<? extends Serializable> entityIds);

    /**
     * 获取符合条件的entity记录总数
     * @return
     */
    long getEntityListCount(Wrapper queryWrapper);

    /**
     * 获取指定条件的Entity集合
     * @param queryWrapper
     * @return
     * @throws Exception
     */
    List<T> getEntityList(Wrapper queryWrapper);

    /**
     * 获取指定条件的Entity集合
     * @param queryWrapper
     * @param pagination
     * @return
     * @throws Exception
     */
    List<T> getEntityList(Wrapper queryWrapper, Pagination pagination);

    /**
     * 获取指定条件的Entity ID集合
     * @param queryWrapper
     * @param getterFn
     * @return
     * @throws Exception
     */
    <FT> List<FT> getValuesOfField(Wrapper queryWrapper, SFunction<T, FT> getterFn);

    /**
     * 获取指定条件的Entity集合
     * @param ids
     * @return
     */
    List<T> getEntityListByIds(List ids);

    /**
     * 获取指定数量的entity记录
     * @param queryWrapper
     * @param limitCount
     * @return
     * @throws Exception
     */
    List<T> getEntityListLimit(Wrapper queryWrapper, int limitCount);

    /**
     * 获取符合条件的一个Entity实体
     * @param queryWrapper
     * @return entity
     */
    T getSingleEntity(Wrapper queryWrapper);

    /**
     * 是否存在符合条件的记录
     * @param getterFn entity的getter方法
     * @param value 需要检查的值
     * @return
     */
    boolean exists(IGetter<T> getterFn, Object value);

    /**
     * 是否存在符合条件的记录
     * @param queryWrapper
     * @return
     */
    boolean exists(Wrapper queryWrapper);

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
    List<LabelValue> getLabelValueList(Wrapper queryWrapper);

    /**
     * 获取id-指定name的映射map
     * @param entityIds
     * @param getterFn
     * @param <ID>
     * @return
     */
    <ID> Map<ID, String> getId2NameMap(List<ID> entityIds, IGetter<T> getterFn);

    /**
     * 获取指定条件的id-Entity 映射map
     * @param getterFn
     * @return
     */
    Map<String, T> getId2EntityMap(List entityIds, IGetter<T>... getterFn);

    /**
     * 获取Map
     * @param queryWrapper
     * @return
     */
    Map<String, Object> getMap(Wrapper<T> queryWrapper);

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

}
