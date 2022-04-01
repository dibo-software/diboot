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
package com.diboot.core.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.config.Cons;
import com.diboot.core.entity.AbstractEntity;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * CRUD增删改查通用RestController-父类
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
@SuppressWarnings({"unchecked", "JavaDoc"})
public class BaseCrudRestController<E extends AbstractEntity> extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseCrudRestController.class);
    /**
     * Entity，VO对应的class
     */
    private Class<E> entityClass;

    /**
     * 查询ViewObject，用于子类重写的方法
     *
     * @param id
     * @return
     * @throws Exception
     */
    protected <VO> JsonResult<VO> getViewObject(Serializable id, Class<VO> voClass) throws Exception {
        VO vo = getService().getViewObject(id, voClass);
        return ok(vo);
    }

    /**
     * 查询Entity，用于子类直接调用
     *
     * @return
     * @throws Exception
     */
    protected E getEntity(Serializable id) throws Exception {
        return getService().getEntity(id);
    }

    /***
     * 获取某VO资源的集合，用于子类重写的方法
     * <p>
     * url参数示例: /${bindURL}?pageSize=20&pageIndex=1&orderBy=itemValue&type=GENDAR
     * </p>
     * @return JsonResult
     * @throws Exception
     */
    protected <VO> JsonResult<List<VO>> getViewObjectList(E entity, Pagination pagination, Class<VO> voClass) throws Exception {
        QueryWrapper<E> queryWrapper = super.buildQueryWrapperByQueryParams(entity);
        // 查询当前页的数据
        List<VO> voList = getService().getViewObjectList(queryWrapper, pagination, voClass);
        // 返回结果
        return JsonResult.OK(voList).bindPagination(pagination);
    }

    /***
     * 获取某VO资源的集合，用于子类重写的方法
     * <p>
     * url参数示例: /${bindURL}?pageSize=20&pageIndex=1&orderBy=itemValue&type=GENDAR
     * </p>
     * @return JsonResult
     * @throws Exception
     */
    protected <VO> JsonResult<List<VO>> getViewObjectList(E entity, Pagination pagination, Class<VO> voClass, boolean buildQueryWrapperByDTO) throws Exception {
        //DTO全部属性参与构建时调用
        QueryWrapper<E> queryWrapper = buildQueryWrapperByDTO ? super.buildQueryWrapperByDTO(entity) : super.buildQueryWrapperByQueryParams(entity);
        // 查询当前页的数据
        List<VO> voList = getService().getViewObjectList(queryWrapper, pagination, voClass);
        // 返回结果
        return JsonResult.OK(voList).bindPagination(pagination);
    }

    /**
     * 获取符合查询条件的全部数据（不分页）
     *
     * @param queryWrapper
     * @return
     * @throws Exception
     */
    protected JsonResult<List<E>> getEntityList(Wrapper<?> queryWrapper) throws Exception {
        // 查询当前页的数据
        List<E> entityList = getService().getEntityList(queryWrapper);
        // 返回结果
        return JsonResult.OK(entityList);
    }

    /***
     * 获取符合查询条件的某页数据（有分页）
     * <p>
     * url参数示例: /${bindURL}?pageSize=20&pageIndex=1
     * </p>
     * @return JsonResult
     * @throws Exception
     */
    protected JsonResult<List<E>> getEntityListWithPaging(Wrapper<?> queryWrapper, Pagination pagination) throws Exception {
        // 查询当前页的数据
        List<E> entityList = getService().getEntityList(queryWrapper, pagination);
        // 返回结果
        return JsonResult.OK(entityList).bindPagination(pagination);
    }

    /***
     * 创建资源对象，用于子类重写的方法
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    protected JsonResult<?> createEntity(E entity) throws Exception {
        // 执行创建资源前的操作
        String validateResult = this.beforeCreate(entity);
        if (validateResult != null) {
            return JsonResult.FAIL_VALIDATION(validateResult);
        }
        // 执行保存操作
        boolean success = getService().createEntity(entity);
        if (success) {
            // 执行创建成功后的操作
            this.afterCreated(entity);
            // 组装返回结果
            Map<String, Object> data = buildPKDataMap(entity);
            return JsonResult.OK(data);
        } else {
            log.warn("创建操作未成功，entity=" + entity.getClass().getSimpleName());
            // 组装返回结果
            return failOperation();
        }
    }

    /***
     * 根据ID更新资源对象，用于子类重写的方法
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    protected JsonResult<?> updateEntity(Serializable id, E entity) throws Exception {
        // 如果前端没有指定entity.id，在此设置，以兼容前端不传的情况
        if (entity.getId() == null) {
            String pk = ContextHelper.getIdFieldName(getEntityClass());
            if (Cons.FieldName.id.name().equals(pk)) {
                Long longId = (id instanceof Long) ? (Long) id : Long.parseLong((String) id);
                entity.setId(longId);
            } else if (BeanUtils.getProperty(entity, pk) == null) {
                BeanUtils.setProperty(entity, pk, id);
            }
        }
        // 执行更新资源前的操作
        String validateResult = this.beforeUpdate(entity);
        if (validateResult != null) {
            return JsonResult.FAIL_VALIDATION(validateResult);
        }
        // 执行保存操作
        boolean success = getService().updateEntity(entity);
        if (success) {
            // 执行更新成功后的操作
            this.afterUpdated(entity);
            return JsonResult.OK();
        } else {
            log.warn("更新操作失败，{}:{}", entity.getClass().getSimpleName(), entity.getId());
            // 返回操作结果
            return failOperation();
        }
    }

    /***
     * 根据id删除资源对象，用于子类重写的方法
     * @param id
     * @return
     * @throws Exception
     */
    protected JsonResult<?> deleteEntity(Serializable id) throws Exception {
        if (id == null) {
            return failInvalidParam( "请选择需要删除的条目！");
        }
        // 是否有权限删除
        E entity = getService().getEntity(id);
        String validateResult = beforeDelete(entity);
        if (validateResult != null) {
            // 返回json
            return failOperation(validateResult);
        }
        // 执行删除操作
        boolean success = getService().deleteEntity(id);
        if (success) {
            // 执行更新成功后的操作
            this.afterDeleted(entity);
            log.info("删除操作成功，{}:{}", entity.getClass().getSimpleName(), id);
            return JsonResult.OK();
        } else {
            log.warn("删除操作未成功，{}:{}", entity.getClass().getSimpleName(), id);
            return failOperation();
        }
    }

    /***
     * 根据id撤回删除
     * @param id
     * @return
     * @throws Exception
     */
    public JsonResult<?> cancelDeletedEntity(Serializable id) throws Exception {
        boolean success = getService().cancelDeletedById(id);
        E entity;
        if (success) {
            entity = getService().getEntity(id);
            this.afterDeletedCanceled(entity);
            log.info("撤回删除操作成功，{}:{}", entity.getClass().getSimpleName(), id);
            return JsonResult.OK("撤回成功");
        } else {
            log.warn("撤回删除操作未成功，{}:{}", getEntityClass().getSimpleName(), id);
            return failOperation("撤回失败");
        }
    }

    /***
     * 根据id批量删除资源对象，用于子类重写的方法
     * @param ids
     * @return
     * @throws Exception
     */
    protected JsonResult<?> batchDeleteEntities(Collection<? extends Serializable> ids) throws Exception {
        if (V.isEmpty(ids)) {
            return failInvalidParam("请选择需要删除的条目！");
        }
        // 是否有权限删除
        String validateResult = beforeBatchDelete(ids);
        if (validateResult != null) {
            // 返回json
            return failOperation(validateResult);
        }
        // 执行删除操作
        boolean success = getService().deleteEntities(ids);
        if (success) {
            // 执行更新成功后的操作
            this.afterBatchDeleted(ids);
            log.info("删除操作成功，{}:{}", getEntityClass().getSimpleName(), S.join(ids));
            return ok();
        } else {
            log.warn("删除操作未成功，{}:{}", getEntityClass().getSimpleName(), S.join(ids));
            return failOperation();
        }
    }

    /**
     * 构建主键的返回值Data
     *
     * @param entity
     * @return
     */
    private Map<String, Object> buildPKDataMap(E entity) {
        // 组装返回结果
        Map<String, Object> data = new HashMap<>(2);
        String pk = ContextHelper.getIdFieldName(getEntityClass());
        Object pkValue = (Cons.FieldName.id.name().equals(pk)) ? entity.getId() : BeanUtils.getProperty(entity, pk);
        data.put(pk, pkValue);
        return data;
    }

    //============= 封装部分快速构造返回结果的方法 =================

    protected JsonResult<?> ok() {
        return JsonResult.OK();
    }

    protected <T> JsonResult<T> ok(T data) {
        return JsonResult.OK(data);
    }

    protected JsonResult<?> failOperation() {
        return new JsonResult<>(Status.FAIL_OPERATION);
    }

    protected JsonResult<?> failOperation(String msg) {
        return JsonResult.FAIL_OPERATION(msg);
    }

    protected JsonResult<?> failInvalidParam() {
        return new JsonResult<>(Status.FAIL_INVALID_PARAM);
    }

    protected JsonResult<?> failInvalidParam(String msg) {
        return JsonResult.FAIL_INVALID_PARAM(msg);
    }

    protected JsonResult<?> fail(Status status, String msg) {
        return new JsonResult<>(status).msg(msg);
    }

    //============= 供子类继承重写的方法 =================

    /***
     * 创建前的相关处理
     * @param entityOrDto
     * @return
     */
    protected String beforeCreate(E entityOrDto) throws Exception {
        return null;
    }

    /***
     * 创建成功后的相关处理
     * @param entityOrDto
     * @return
     */
    protected void afterCreated(E entityOrDto) throws Exception {
    }

    /***
     * 更新前的相关处理
     * @param entityOrDto
     * @return
     */
    protected String beforeUpdate(E entityOrDto) throws Exception {
        BeanUtils.clearFieldValue(entityOrDto, BindingCacheManager.getPropInfoByClass(getEntityClass()).getFillUpdateFieldList());
        return null;
    }

    /***
     * 更新成功后的相关处理
     * @param entityOrDto
     * @return
     */
    protected void afterUpdated(E entityOrDto) throws Exception {
    }

    /***
     * 是否有删除权限，如不可删除返回错误提示信息，如 Status.FAIL_NO_PERMISSION.label()
     * @param entityOrDto
     * @return
     */
    protected String beforeDelete(E entityOrDto) throws Exception {
        return null;
    }

    /***
     * 删除成功后的相关处理
     * @param entityOrDto
     * @return
     */
    protected void afterDeleted(E entityOrDto) throws Exception {
    }

    /***
     * 撤销删除成功后的相关处理
     * @param entityOrDto
     * @throws Exception
     */
    protected void afterDeletedCanceled(E entityOrDto) throws Exception {
    }

    /***
     * 是否有批量删除权限，如不可删除返回错误提示信息，如 Status.FAIL_NO_PERMISSION.label()
     * @param ids
     * @return
     */
    protected String beforeBatchDelete(Collection<? extends Serializable> ids) throws Exception {
        return null;
    }

    /***
     * 批量删除成功后的相关处理
     * @param ids
     * @return
     */
    protected void afterBatchDeleted(Collection<? extends Serializable> ids) throws Exception {
    }

    /**
     * 得到service
     *
     * @return
     */
    protected BaseService<E> getService() {
        return ContextHelper.getBaseServiceByEntity(getEntityClass());
    }

    /**
     * 获取Entity的class
     *
     * @return
     */
    protected Class<E> getEntityClass() {
        if (this.entityClass == null) {
            this.entityClass = BeanUtils.getGenericityClass(this, 0);
            if (this.entityClass == null) {
                log.warn("无法从 {} 类定义中获取泛型类entityClass", this.getClass().getName());
            }
        }
        return this.entityClass;
    }

}
