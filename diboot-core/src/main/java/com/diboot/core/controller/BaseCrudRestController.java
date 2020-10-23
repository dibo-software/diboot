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
import com.diboot.core.config.Cons;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
public class BaseCrudRestController<E extends BaseEntity> extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseCrudRestController.class);
    /**
     * Entity，VO对应的class
     */
    private Class<E> entityClass;
    /**
     * Service实现类
     */
    private BaseService baseService;

    @Autowired
    protected DictionaryService dictionaryService;

    /**
     * 查询ViewObject，用于子类重写的方法
     * @param id
     * @return
     * @throws Exception
     */
    protected <VO> JsonResult getViewObject(Serializable id, Class<VO> voClass) throws Exception{
        // 检查String类型id
        if(id instanceof String && !S.isNumeric((String)id)){
            String pk = ContextHelper.getPrimaryKey(getEntityClass());
            if(Cons.FieldName.id.name().equals(pk)){
                throw new BusinessException(Status.FAIL_INVALID_PARAM, "参数id类型不匹配！");
            }
        }
        VO vo = (VO)getService().getViewObject(id, voClass);
        return new JsonResult(vo);
    }

    /**
     * 查询Entity，用于子类直接调用
    * @return
     * @throws Exception
     */
    protected <E> E getEntity(Serializable id) throws Exception{
        // 检查String类型id
        if(id instanceof String && !S.isNumeric((String)id)){
            String pk = ContextHelper.getPrimaryKey(getEntityClass());
            if(Cons.FieldName.id.name().equals(pk)){
                throw new BusinessException(Status.FAIL_INVALID_PARAM, "参数id类型不匹配！");
            }
        }
        return (E)getService().getEntity(id);
    }

    /***
     * 获取某VO资源的集合，用于子类重写的方法
     * <p>
     * url参数示例: /${bindURL}?pageSize=20&pageIndex=1&orderBy=itemValue&type=GENDAR
     * </p>
     * @return JsonResult
     * @throws Exception
     */
    protected <VO> JsonResult getViewObjectList(E entity, Pagination pagination, Class<VO> voClass) throws Exception {
        QueryWrapper<E> queryWrapper = super.buildQueryWrapper(entity);
        // 查询当前页的数据
        List<VO> voList = getService().getViewObjectList(queryWrapper, pagination, voClass);
        // 返回结果
        return JsonResult.OK(voList).bindPagination(pagination);
    }

    /**
     * 获取符合查询条件的全部数据（不分页）
     * @param queryWrapper
     * @return
     * @throws Exception
     */
    protected JsonResult getEntityList(Wrapper queryWrapper) throws Exception {
        // 查询当前页的数据
        List entityList = getService().getEntityList(queryWrapper);
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
    protected JsonResult getEntityListWithPaging(Wrapper queryWrapper, Pagination pagination) throws Exception {
        // 查询当前页的数据
        List entityList = getService().getEntityList(queryWrapper, pagination);
        // 返回结果
        return JsonResult.OK(entityList).bindPagination(pagination);
    }

    /***
     * 创建资源对象，用于子类重写的方法
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    protected JsonResult createEntity(E entity) throws Exception {
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
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }

    /***
     * 根据ID更新资源对象，用于子类重写的方法
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    protected JsonResult updateEntity(Serializable id, E entity) throws Exception {
        // 如果前端没有指定entity.id，在此设置，以兼容前端不传的情况
        if(entity.getId() == null){
            String pk = ContextHelper.getPrimaryKey(getEntityClass());
            if(Cons.FieldName.id.name().equals(pk)){
                Long longId = (id instanceof Long)? (Long)id : Long.parseLong((String)id);
                entity.setId(longId);
            }
            else if(BeanUtils.getProperty(entity, pk) == null){
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
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }

    /***
     * 根据id删除资源对象，用于子类重写的方法
     * @param id
     * @return
     * @throws Exception
     */
    protected JsonResult deleteEntity(Serializable id) throws Exception {
        if (id == null) {
            return new JsonResult(Status.FAIL_INVALID_PARAM, "请选择需要删除的条目！");
        }
        // 是否有权限删除
        E entity = (E) getService().getEntity(id);
        String validateResult = beforeDelete(entity);
        if (validateResult != null) {
            // 返回json
            return new JsonResult(Status.FAIL_OPERATION, validateResult);
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
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }

    /***
     * 根据id批量删除资源对象，用于子类重写的方法
     * @param ids
     * @return
     * @throws Exception
     */
    protected JsonResult batchDeleteEntities(Collection<? extends Serializable> ids) throws Exception {
        if (V.isEmpty(ids)) {
            return new JsonResult(Status.FAIL_INVALID_PARAM, "请选择需要删除的条目！");
        }
        // 是否有权限删除
        String validateResult = beforeBatchDelete(ids);
        if (validateResult != null) {
            // 返回json
            return new JsonResult(Status.FAIL_OPERATION, validateResult);
        }
        // 执行删除操作
        boolean success = getService().deleteEntities(ids);
        if (success) {
            // 执行更新成功后的操作
            this.afterBatchDeleted(ids);
            log.info("删除操作成功，{}:{}", getEntityClass().getSimpleName(), S.join(ids));
            return new JsonResult();
        } else {
            log.warn("删除操作未成功，{}:{}",getEntityClass().getSimpleName(), S.join(ids));
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }

    /**
     * 构建主键的返回值Data
     * @param entity
     * @return
     */
    private Map<String, Object> buildPKDataMap(E entity){
        // 组装返回结果
        Map<String, Object> data = new HashMap<>(2);
        String pk = ContextHelper.getPrimaryKey(getEntityClass());
        Object pkValue = (Cons.FieldName.id.name().equals(pk))? entity.getId() : BeanUtils.getProperty(entity, pk);
        data.put(pk, pkValue);
        return data;
    }

    //============= 供子类继承重写的方法 =================
    /***
     * 创建前的相关处理
     * @param entityOrDto
     * @return
     */
    protected String beforeCreate(Object entityOrDto) throws Exception {
        return null;
    }

    /***
     * 创建成功后的相关处理
     * @param entityOrDto
     * @return
     */
    protected void afterCreated(Object entityOrDto) throws Exception {
    }

    /***
     * 更新前的相关处理
     * @param entityOrDto
     * @return
     */
    protected String beforeUpdate(Object entityOrDto) throws Exception {
        return null;
    }

    /***
     * 更新成功后的相关处理
     * @param entityOrDto
     * @return
     */
    protected void afterUpdated(Object entityOrDto) throws Exception {
    }

    /***
     * 是否有删除权限，如不可删除返回错误提示信息，如 Status.FAIL_NO_PERMISSION.label()
     * @param entityOrDto
     * @return
     */
    protected String beforeDelete(Object entityOrDto) throws Exception{
        return null;
    }

    /***
     * 删除成功后的相关处理
     * @param entityOrDto
     * @return
     */
    protected void afterDeleted(Object entityOrDto) throws Exception {
    }

    /***
     * 是否有批量删除权限，如不可删除返回错误提示信息，如 Status.FAIL_NO_PERMISSION.label()
     * @param ids
     * @return
     */
    protected String beforeBatchDelete(Collection<? extends Serializable> ids) throws Exception{
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
     * @return
     */
    protected BaseService getService() {
        if(this.baseService == null){
            Class<E> clazz = getEntityClass();
            if(clazz != null){
                this.baseService = ContextHelper.getBaseServiceByEntity(clazz);
            }
            if(this.baseService == null){
                log.warn("Entity: {} 无对应的Service定义，请检查！", clazz.getName());
            }
        }
        return this.baseService;
    }

    /**
     * 获取Entity的class
     * @return
     */
    protected Class<E> getEntityClass(){
        if(this.entityClass == null){
             this.entityClass = BeanUtils.getGenericityClass(this, 0);
             if(this.entityClass == null) {
                log.warn("无法从 {} 类定义中获取泛型类entityClass", this.getClass().getName());
             }
        }
        return this.entityClass;
    }

}