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
package com.diboot.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.helper.ServiceAdaptor;
import com.diboot.core.binding.helper.WrapperHelper;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.binding.parser.PropInfo;
import com.diboot.core.binding.query.dynamic.DynamicJoinQueryWrapper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.dto.SortParamDTO;
import com.diboot.core.entity.BaseTreeEntity;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.*;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/***
 * CRUD通用接口实现类
 * @author mazc@dibo.ltd
 * @param <M> mapper类
 * @param <T> entity类
 * @version 2.0
 * @date 2019/01/01
 */
@SuppressWarnings({"unchecked", "rawtypes", "JavaDoc"})
public class BaseServiceImpl<M extends BaseCrudMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
	private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);

	/***
	 * 获取当前的Mapper对象
	 * @return
	 */
	@Override
	public M getMapper(){
		return baseMapper;
	}

	@Override
	public QueryChainWrapper<T> query() {
		return ChainWrappers.queryChain(this.getBaseMapper());
	}

	@Override
	public LambdaQueryChainWrapper<T> lambdaQuery() {
		return ChainWrappers.lambdaQueryChain(this.getBaseMapper());
	}

	@Override
	public UpdateChainWrapper<T> update() {
		return ChainWrappers.updateChain(this.getBaseMapper());
	}

	@Override
	public LambdaUpdateChainWrapper<T> lambdaUpdate() {
		return ChainWrappers.lambdaUpdateChain(this.getBaseMapper());
	}

	@Override
	public T getEntity(Serializable id){
		return super.getById(id);
	}

	@Override
	public <FT> FT getValueOfField(Serializable idVal, SFunction<T, FT> getterFn) {
		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(getEntityClass());
		String fetchCol = propInfo.getColumnByField(BeanUtils.convertSFunctionToFieldName(getterFn));
		QueryWrapper<T> queryWrapper = new QueryWrapper<T>()
				.select(propInfo.getIdColumn(), fetchCol)
				.eq(propInfo.getIdColumn(), idVal);
		T entity = getSingleEntity(queryWrapper);
		if(entity == null){
			return null;
		}
		return getterFn.apply(entity);
	}

	@Override
	public <FT> FT getValueOfField(SFunction<T, ?> queryFieldFn, Serializable queryFieldVal, SFunction<T, FT> getterFn) {
		return getValueOfField(Wrappers.<T>lambdaQuery().eq(queryFieldFn, queryFieldVal), getterFn);
	}

	@Override
	public <FT> FT getValueOfField(LambdaQueryWrapper<T> queryWrapper, SFunction<T, FT> getterFn) {
		T entity = getSingleEntity(queryWrapper.select(getterFn));
		if(entity == null){
			return null;
		}
		return getterFn.apply(entity);
	}

	@Override
	public <FT> List<FT> getValuesOfField(String fieldKey, Object fieldVal, SFunction<T, FT> getterFn) {
		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(getEntityClass());
		String fetchCol = propInfo.getColumnByField(BeanUtils.convertSFunctionToFieldName(getterFn));
		String conditionCol = propInfo.getColumnByField(fieldKey);
		QueryWrapper<T> queryWrapper = new QueryWrapper<T>().select(fetchCol).eq(conditionCol, fieldVal);
		return getValuesOfField(queryWrapper, getterFn);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean createEntity(T entity) {
		if(entity == null){
			warning("createEntity", "参数entity为null");
			return false;
		}
		return save(entity);
	}

	@Override
	public boolean save(T entity) {
		this.beforeCreate(entity);
		boolean success = super.save(entity);
		if(success) {
			this.afterCreate(entity);
		}
		return success;
	}

	/**
	 * 用于创建之前的自动填充等场景调用
	 */
	protected void beforeCreate(T entity) {
		if(entity instanceof BaseTreeEntity) {
			fillTreeNodeParentPath(entity);
		}
	}

	/**
	 * 创建数据的后拦截
	 * @param entity
	 */
	protected void afterCreate(T entity) {
	}

	/**
	 * 批量创建数据的前拦截
	 * @param entityList
	 */
	protected void beforeBatchCreate(Collection<T> entityList) {
	}

	/**
	 * 批量创建数据的后拦截
	 * @param entityList
	 */
	protected void afterBatchCreate(Collection<T> entityList) {
	}

	/**
	 * 更新数据的后拦截
	 * @param entity
	 */
	protected void afterUpdate(T entity) {
	}

	/**
	 * 删除数据的前拦截，值可能为单值或集合
	 * @param fieldKey
	 * @param fieldVal
	 */
	protected void beforeDelete(String fieldKey, Object fieldVal) {
	}

	/**
	 * 删除数据的前拦截，值可能为单值或集合
	 * @param entityIds
	 */
	protected void beforeDelete(Object entityIds) {
		String pk = ContextHolder.getIdFieldName(getEntityClass());
		beforeDelete(pk, entityIds);
	}

	/**
	 * 删除数据的后拦截，值可能为单值或集合
	 * @param entityIds
	 */
	protected void afterDelete(Object entityIds) {
		String pk = ContextHolder.getIdFieldName(getEntityClass());
		afterDelete(pk, entityIds);
	}

	/**
	 * 删除数据的后拦截，值可能为单值或集合
	 * @param fieldKey
	 * @param fieldVal
	 */
	protected void afterDelete(String fieldKey, Object fieldVal) {
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public <RE, R> boolean createEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter) {
		boolean success = createEntity(entity);
		if(!success){
			log.warn("新建Entity失败: {}", entity.toString());
			return false;
		}
		if(V.isEmpty(relatedEntities)){
			return true;
		}
		// 获取主键
		Object pkValue = getPrimaryKeyValue(entity);
		return createRelatedEntities((Serializable) pkValue, relatedEntities, relatedEntitySetter);
	}

	@Override
	public <RE, R> boolean createRelatedEntities(Serializable entityId, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter) {
		if(V.isEmpty(relatedEntities)){
			return true;
		}
		Class relatedEntityClass = relatedEntities.get(0).getClass();
		String relatedEntitySetterFld = BeanUtils.convertToFieldName(relatedEntitySetter);
		// 填充关联关系
		relatedEntities.forEach(relatedEntity-> BeanUtils.setProperty(relatedEntity, relatedEntitySetterFld, entityId));
		// 获取关联对象对应的Service
		BaseService relatedEntityService = ContextHolder.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService != null){
			return relatedEntityService.createEntities(relatedEntities);
		}
		else{
			// 查找mapper
			BaseMapper mapper = ContextHolder.getBaseMapperByEntity(relatedEntityClass);
			// 新增关联，无service只能循环插入
			for(RE relation : relatedEntities){
				mapper.insert(relation);
			}
			return true;
		}
	}

	@Override
	public <RE, R> boolean createRelatedEntity(Serializable entityId, RE relatedEntity, ISetter<RE, R> relatedEntitySetter) {
		if(relatedEntity == null){
			return true;
		}
		Class relatedEntityClass = relatedEntity.getClass();
		String relatedEntitySetterFld = BeanUtils.convertToFieldName(relatedEntitySetter);
		// 填充关联关系
		BeanUtils.setProperty(relatedEntity, relatedEntitySetterFld, entityId);
		// 获取关联对象对应的Service
		BaseService relatedEntityService = ContextHolder.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService != null){
			return relatedEntityService.createEntity(relatedEntity);
		}
		else{
			// 查找mapper
			BaseMapper mapper = ContextHolder.getBaseMapperByEntity(relatedEntityClass);
			// 新增关联，无service只能循环插入
			mapper.insert(relatedEntity);
			return true;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean createEntities(Collection entityList){
		if(V.isEmpty(entityList)){
			return false;
		}
		// 批量插入
		return saveBatch(entityList, BaseConfig.getBatchSize());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize){
		// 批量插入
		this.beforeBatchCreate(entityList);
		boolean success = super.saveBatch(entityList, batchSize);
		this.afterBatchCreate(entityList);
		return success;
	}

	/**
	 * 用于更新之前的自动填充等场景调用
	 */
	protected void beforeUpdate(T entity){
		if(entity instanceof BaseTreeEntity) {
			fillTreeNodeParentPath(entity);
		}
		List<String> maskFields = ParserCache.getDataMaskFieldList(getEntityClass());
		if(V.notEmpty(maskFields)) {
			for(String maskField : maskFields) {
				Object value = BeanUtils.getProperty(entity, maskField);
				if(value != null && S.valueOf(value).contains("*")) {
					BeanUtils.setProperty(entity, maskField, null);
					log.debug("更新操作中提交了 {} 的脱敏值 :{}，忽略该字段以避免误更新", maskField, value);
				}
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateById(T entity) {
		return updateEntity(entity);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateEntity(T entity) {
		this.beforeUpdate(entity);
		boolean success = super.updateById(entity);
		if(success) {
			this.afterUpdate(entity);
		}
		return success;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateEntity(T entity, Wrapper updateWrapper) {
		this.beforeUpdate(entity);
		boolean success = super.update(entity, updateWrapper);
		if(success) {
			this.afterUpdate(entity);
		}
		return success;
	}

	@Override
	public boolean updateEntity(Wrapper updateWrapper) {
		return super.update(null, updateWrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateEntities(Collection entityList) {
		if(V.isEmpty(entityList)){
			return false;
		}
		for(Object entity : entityList){
			this.beforeUpdate((T)entity);
		}
		boolean success = super.updateBatchById(entityList);
		if(success) {
			for(Object entity : entityList){
				this.afterUpdate((T)entity);
			}
		}
		return success;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean createOrUpdateEntity(T entity) {
		if(entity instanceof BaseTreeEntity) {
			fillTreeNodeParentPath(entity);
		}
		Object id = getPrimaryKeyValue(entity);
		if(id == null) {
			return createEntity(entity);
		}
		return updateEntity(entity);
	}

	/**
	 * 填充树节点父级path
	 * @param entity
	 */
	private void fillTreeNodeParentPath(T entity) {
		BaseTreeEntity treeEntity = (BaseTreeEntity) entity;
		if(V.isEmpty(treeEntity.getParentId())) {
			return;
		}
		// 上级设置为自身，抛出异常
		if(V.equals(treeEntity.getParentId(), treeEntity.getId())) {
			throw new BusinessException(Status.FAIL_VALIDATION, "exception.business.baseServiceImpl.fillTreeNodeParentPath.message");
		}
		BaseTreeEntity parentNode = (BaseTreeEntity) getEntity(treeEntity.getParentId());
		if(parentNode != null) {
			String parentIdsPath = parentNode.getParentIdsPath();
			if(parentIdsPath == null) {
				parentIdsPath = "";
			}
			else if(!S.endsWith(parentIdsPath, Cons.SEPARATOR_COMMA)) {
				parentIdsPath += Cons.SEPARATOR_COMMA;
			}
			parentIdsPath += treeEntity.getParentId();
			treeEntity.setParentIdsPath(parentIdsPath);
			log.debug("自动填充 parentIdsPath = {}", parentIdsPath);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean createOrUpdateEntities(Collection entityList) {
		if(V.isEmpty(entityList)){
			warning("createOrUpdateEntities", "参数entityList为空!");
			return false;
		}
		// 批量插入
		return super.saveOrUpdateBatch(entityList, BaseConfig.getBatchSize());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean deleteEntity(String fieldKey, Object fieldVal) {
		// 获取主键的关联属性
		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(getEntityClass());
		String column = propInfo.getColumnByField(fieldKey);
		if(column == null) {
			column = fieldKey;
		}
		QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
		if(fieldVal instanceof Collection) {
			queryWrapper.in(column, (Collection)fieldVal);
		}
		else {
			queryWrapper.eq(column, fieldVal);
		}
		this.beforeDelete(fieldKey, fieldVal);
		boolean success = super.remove(queryWrapper);
		if(success) {
			this.afterDelete(fieldKey, fieldVal);
		}
		return success;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
    public <R> boolean createOrUpdateN2NRelations(SFunction<R, ?> driverIdGetter, Object driverId,
                                                  SFunction<R, ?> followerIdGetter, Collection<? extends Serializable> followerIdList) {
        return createOrUpdateN2NRelations(driverIdGetter, driverId, followerIdGetter, followerIdList, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <R> boolean createOrUpdateN2NRelations(SFunction<R, ?> driverIdGetter, Object driverId,
                                                  SFunction<R, ?> followerIdGetter, Collection<? extends Serializable> followerIdList,
                                                  Consumer<QueryWrapper<R>> queryConsumer, Consumer<R> setConsumer) {
		if (driverId == null) {
			throw new InvalidUsageException("exception.invalidUsage.baseService.nullDriverId");
		}
		if (followerIdList == null) {
			log.debug("从动对象ID集合为null，不做关联关系更新处理");
			return false;
		}
		// 从getter中获取class和fieldName
		LambdaMeta lambdaMeta = LambdaUtils.extract(driverIdGetter);
		Class<R> middleTableClass = (Class<R>) lambdaMeta.getInstantiatedClass();
		EntityInfoCache entityInfo = BindingCacheManager.getEntityInfoByClass(middleTableClass);
		if (entityInfo == null) {
			throw new InvalidUsageException("exception.invalidUsage.baseService.nonServiceOrMapper", middleTableClass.getName());
		}
		boolean isExistPk = entityInfo.getIdColumn() != null;

		// 获取主动从动字段名
		String driverFieldName = PropertyNamer.methodToProperty(lambdaMeta.getImplMethodName());
		String followerFieldName = BeanUtils.convertSFunctionToFieldName(followerIdGetter);
		String driverColumnName = entityInfo.getColumnByField(driverFieldName);
		String followerColumnName = entityInfo.getColumnByField(followerFieldName);

		// 查询已有关联
		QueryWrapper<R> selectOld = new QueryWrapper<R>().eq(driverColumnName, driverId);
		if (queryConsumer != null) {
			queryConsumer.accept(selectOld);
		}
		if (isExistPk) {
			selectOld.select(entityInfo.getIdColumn(), followerColumnName);
		} else {
			selectOld.select(followerColumnName);
		}

		IService<R> iService = entityInfo.getService();
		BaseMapper<R> baseMapper = entityInfo.getBaseMapper();
		List<R> oldEntityList = (iService != null)? iService.list(selectOld) : baseMapper.selectList(selectOld);
		// 删除失效关联
		List<Serializable> delIds = new ArrayList<>();
		for (R entity : oldEntityList) {
			Object followerId = BeanUtils.getProperty(entity, followerFieldName);
			if (followerId != null && V.notEmpty(followerIdList) && followerIdList.contains(followerId)) {
				followerIdList.remove(followerId);
				continue;
			}
			Serializable id = (Serializable) BeanUtils.getProperty(entity, isExistPk ? entityInfo.getPropInfo().getIdFieldName() : followerFieldName);
			if(id != null) {
				delIds.add(id);
			}
		}
		if (!delIds.isEmpty()) {
			if (isExistPk) {
				if (iService != null) {
					iService.removeByIds(delIds);
				} else {
					baseMapper.deleteByIds(delIds);
				}
			} else {
				QueryWrapper<R> delOld = new QueryWrapper<R>().eq(driverColumnName, driverId)
						.in(entityInfo.getColumnByField(followerFieldName), delIds);
				if (queryConsumer != null) {
					queryConsumer.accept(selectOld);
				}
				if (iService != null) {
					iService.remove(delOld);
				} else if (!delIds.isEmpty()) {
					baseMapper.delete(delOld);
				}
			}
		}

        // 新增关联
        if (V.notEmpty(followerIdList)) {
            List<R> n2nRelations = new ArrayList<>(followerIdList.size());
            try {
                for (Serializable followerId : followerIdList) {
                    R relation = middleTableClass.newInstance();
					BeanWrapper beanWrapper = BeanUtils.getBeanWrapper(relation);
					beanWrapper.setPropertyValue(driverFieldName, driverId);
					beanWrapper.setPropertyValue(followerFieldName, followerId);
                    if (setConsumer != null) {
                        setConsumer.accept(relation);
                    }
                    n2nRelations.add(relation);
                }
            } catch (Exception e) {
				log.error("新增关联异常：", e);
                throw new BusinessException(Status.FAIL_EXCEPTION);
            }
            if (iService != null) {
                if (iService instanceof BaseService) {
                    ((BaseService<R>) iService).createEntities(n2nRelations);
                } else {
                    iService.saveBatch(n2nRelations);
                }
            } else {
                // 新增关联，无service只能循环插入
                for (R relation : n2nRelations) {
                    baseMapper.insert(relation);
                }
            }
        }
        return true;
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public <RE,R> boolean updateEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE,R> relatedEntitySetter) {
		boolean success = updateEntity(entity);
		if(!success){
			log.warn("更新Entity失败: {}", entity.toString());
			return false;
		}
		// 更新关联entity的数据
		// 获取主键
		Object pkValue = getPrimaryKeyValue(entity);
		return updateRelatedEntities((Serializable) pkValue, relatedEntities, relatedEntitySetter);
	}

	@Override
	public <RE, R> boolean updateRelatedEntities(Serializable entityId, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter) {
		// 获取关联entity的类
		Class relatedEntityClass;
		if(V.notEmpty(relatedEntities)){
			relatedEntityClass = BeanUtils.getTargetClass(relatedEntities.get(0));
		}
		else{
			try{
				relatedEntityClass = Class.forName(BeanUtils.getSerializedLambda(relatedEntitySetter).getImplClass().replaceAll("/", "."));
			}
			catch (Exception e){
				log.warn("无法识别关联Entity的Class: {}", e.getMessage());
				return false;
			}
		}
		// 获取关联对象对应的Service
		BaseService relatedEntityService = ContextHolder.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService == null){
			log.error("未能识别到Entity: {} 的Service实现，请检查！", relatedEntityClass.getName());
			return false;
		}
		// 获取主键
		String attributeName = BeanUtils.convertToFieldName(relatedEntitySetter);
		//获取原 关联entity list
		QueryWrapper<RE> queryWrapper = new QueryWrapper();
		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(relatedEntityClass);
		queryWrapper.eq(propInfo.getColumnByField(attributeName), entityId);
		List<RE> oldRelatedEntities = relatedEntityService.getEntityList(queryWrapper);
		// 遍历更新关联对象
		Set relatedEntityIds = new HashSet();
		if(V.notEmpty(relatedEntities)){
			// 新建 修改 删除
			List<RE> newRelatedEntities = new ArrayList<>();
			for(RE relatedEntity : relatedEntities){
				BeanUtils.setProperty(relatedEntity, attributeName, entityId);
				Object relPkValue = getPrimaryKeyValue(relatedEntity);
				if(V.notEmpty(relPkValue)){
					relatedEntityService.updateEntity(relatedEntity);
				}
				else{
					newRelatedEntities.add(relatedEntity);
				}
				relatedEntityIds.add(relPkValue);
			}
			relatedEntityService.createEntities(newRelatedEntities);
		}
		// 遍历已有关联对象
		if(V.notEmpty(oldRelatedEntities)){
			List deleteRelatedEntityIds = new ArrayList();
			for(RE relatedEntity : oldRelatedEntities){
				Object relPkValue = getPrimaryKeyValue(relatedEntity);
				if(!relatedEntityIds.contains(relPkValue)){
					deleteRelatedEntityIds.add(relPkValue);
				}
			}
			relatedEntityService.deleteEntities(deleteRelatedEntityIds);
		}
		return true;
	}

	@Override
	public <RE, R> boolean updateRelatedEntity(Serializable entityId, RE relatedEntity, ISetter<RE, R> relatedEntitySetter) {
		// 获取关联entity的类
		Class relatedEntityClass;
		if(relatedEntity != null){
			relatedEntityClass = BeanUtils.getTargetClass(relatedEntity);
		}
		else{
			try{
				relatedEntityClass = Class.forName(BeanUtils.getSerializedLambda(relatedEntitySetter).getImplClass().replaceAll("/", "."));
			}
			catch (Exception e){
				log.warn("无法识别关联Entity的Class: {}", e.getMessage());
				return false;
			}
		}
		// 获取关联对象对应的Service
		BaseService relatedEntityService = ContextHolder.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService == null){
			log.error("未能识别到Entity: {} 的Service实现，请检查！", relatedEntityClass.getName());
			return false;
		}
		// 获取主键
		if(relatedEntity != null){
			// 填充关联关系id
			String relatedEntitySetterFld = BeanUtils.convertToFieldName(relatedEntitySetter);
			if(BeanUtils.getProperty(relatedEntity, relatedEntitySetterFld) == null) {
				BeanUtils.setProperty(relatedEntity, relatedEntitySetterFld, entityId);
			}
			relatedEntityService.createOrUpdateEntity(relatedEntity);
		}
		else {
			String attributeName = BeanUtils.convertToFieldName(relatedEntitySetter);
			relatedEntityService.deleteEntity(attributeName, entityId);
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public <RE,R> boolean deleteEntityAndRelatedEntities(Serializable id, Class<RE> relatedEntityClass, ISetter<RE, R> relatedEntitySetter) {
		boolean success = deleteEntity(id);
		if(!success){
			log.warn("删除Entity失败: {}",id);
			return false;
		}
		// 获取主键的关联属性
		return deleteRelatedEntities(id, relatedEntityClass, relatedEntitySetter);
	}

	@Override
	public <RE, R> boolean deleteRelatedEntities(Object entityIds, Class<RE> relatedEntityClass, ISetter<RE, R> relatedEntitySetter) {
		// 获取主键的关联属性
		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(relatedEntityClass);
		String relatedEntitySetterFld = BeanUtils.convertToFieldName(relatedEntitySetter);
		QueryWrapper<RE> queryWrapper = new QueryWrapper<RE>();
		if(entityIds instanceof Collection){
			queryWrapper.in(propInfo.getColumnByField(relatedEntitySetterFld), entityIds);
		}
		else {
			queryWrapper.eq(propInfo.getColumnByField(relatedEntitySetterFld), entityIds);
		}
		// 删除关联子表数据
		BaseService relatedEntityService = ContextHolder.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService != null){
			return relatedEntityService.deleteEntities(queryWrapper);
		}
		BaseMapper relatedMapper = ContextHolder.getBaseMapperByEntity(relatedEntityClass);
		return relatedMapper.delete(queryWrapper) >= 0;
	}

	@Override
	public <RE, R> boolean deleteRelatedEntity(Object entityIds, Class<RE> relatedEntityClass, ISetter<RE, R> relatedEntitySetter) {
		return deleteRelatedEntities(entityIds, relatedEntityClass, relatedEntitySetter);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean deleteEntity(Serializable id) {
		// 树结构，仅允许叶子节点进行删除操作
		if(BaseTreeEntity.class.isAssignableFrom(getEntityClass())) {
			QueryWrapper<T> wrapper = new QueryWrapper<T>().eq(Cons.ColumnName.parent_id.name(), id);
			if(exists(wrapper)) {
				throw new BusinessException(Status.FAIL_VALIDATION, "exception.business.baseServiceImpl.deleteEntity.message");
			}
		}
		this.beforeDelete(id);
		boolean success = super.removeById(id);
		if(success) {
			this.afterDelete(id);
		}
		return success;
	}

	@Transactional(rollbackFor = Exception.class)
    @Override
	public boolean deleteEntities(Wrapper queryWrapper){
		Class<?> entityClass = getEntityClass();
		// 执行查询获取匹配ids
		// 优化SQL，只查询id字段
		if(queryWrapper instanceof QueryWrapper){
			String idCol = ContextHolder.getIdColumnName(entityClass);
			((QueryWrapper)queryWrapper).select(idCol);
		}
		List<T> entityList = getEntityList(queryWrapper);
		if(V.isEmpty(entityList)){
			return false;
		}
		String pk = ContextHolder.getIdFieldName(entityClass);
		List<String> entityIds = BeanUtils.collectToList(entityList, pk);
		this.beforeDelete(entityIds);
		boolean success = super.removeByIds(entityIds);
		if(success) {
			this.afterDelete(entityIds);
		}
		return success;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteEntities(Collection<? extends Serializable> entityIds) {
		if(V.isEmpty(entityIds)){
			return false;
		}
		String pk = ContextHolder.getIdFieldName(getEntityClass());
		this.beforeDelete(pk, entityIds);
		boolean success = super.removeByIds(entityIds);
		if(success) {
			this.afterDelete(pk, entityIds);
		}
		return success;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public <T, ST> boolean deleteEntities(SFunction<T, ST> fldGetterFn, Object fieldValue) {
		if (fieldValue == null) {
			return false;
		}
		String fieldName = BeanUtils.convertSFunctionToFieldName(fldGetterFn);
		return deleteEntity(fieldName, fieldValue);
	}

	@Override
	public long getEntityListCount(Wrapper queryWrapper) {
		return super.count(queryWrapper);
	}

	@Override
	public List<T> getEntityList(Wrapper queryWrapper) {
		return getEntityList(queryWrapper, null);
	}

	@Override
	public List<T> getEntityList(Wrapper queryWrapper, Pagination pagination) {
		// 支持 ChainQuery
		if (queryWrapper instanceof ChainQuery) {
			queryWrapper = ((ChainQuery<?>) queryWrapper).getWrapper();
		}
		Class<?> entityClass = getEntityClass();
		// 如果是动态join，则调用JoinsBinder
		if(queryWrapper instanceof DynamicJoinQueryWrapper){
			return Binder.joinQueryList((DynamicJoinQueryWrapper)queryWrapper, entityClass, pagination);
		}
		else if(queryWrapper instanceof QueryWrapper) {
			QueryWrapper mpQueryWrapper = ((QueryWrapper)queryWrapper);
			if(mpQueryWrapper.getEntityClass() == null) {
				mpQueryWrapper.setEntityClass(entityClass);
			}
		}
		else if(queryWrapper instanceof LambdaQueryWrapper) {
			LambdaQueryWrapper mpQueryWrapper = ((LambdaQueryWrapper)queryWrapper);
			if(mpQueryWrapper.getEntityClass() == null) {
				mpQueryWrapper.setEntityClass(entityClass);
			}
		}
		// 否则，调用MP默认实现
		if(pagination != null){
			IPage<T> page = convertToIPage(pagination);
			page = super.page(page, queryWrapper);
			// 如果重新执行了count进行查询，则更新pagination中的总数
			if(page.searchCount()){
				pagination.setTotalCount(page.getTotal());
			}
			return page.getRecords();
		}
		else{
			List<T> list = super.list(queryWrapper);
			if(list == null){
				list = Collections.emptyList();
			}
			else if(list.size() > BaseConfig.getBatchSize()){
				log.warn("单次查询记录数量过大，请及时检查优化。返回结果数={}", list.size());
			}
			return list;
		}
	}

	/**
	 * 获取指定条件的Entity ID集合
	 * @param queryWrapper
	 * @param getterFn
	 * @return
	 * @throws Exception
	 */
	@Override
	public <FT> List<FT> getValuesOfField(Wrapper queryWrapper, SFunction<T, FT> getterFn){
		LambdaQueryWrapper<T> query;
		List<T> entityList;
		// 支持 ChainQuery
		if (queryWrapper instanceof ChainQuery) {
			queryWrapper = ((ChainQuery<?>) queryWrapper).getWrapper();
		}
		// 优化SQL，只查询当前字段
		if(queryWrapper instanceof QueryWrapper){
			query = ((QueryWrapper)queryWrapper).lambda();
		}
		else if(queryWrapper instanceof LambdaQueryWrapper){
			query = ((LambdaQueryWrapper) queryWrapper);
		}
		else {
			throw new InvalidUsageException("exception.invalidUsage.baseService.notSupportWrapper", (queryWrapper == null ? "null" : queryWrapper.getClass().getSimpleName()));
		}
		// 如果是动态join，则调用JoinsBinder
		query.select(getterFn);
		if(queryWrapper instanceof DynamicJoinQueryWrapper){
			entityList = Binder.joinQueryList( (DynamicJoinQueryWrapper)queryWrapper, getEntityClass(), null);
		}
		else{
			entityList = getEntityList(query);
		}
		if(V.isEmpty(entityList)){
			return Collections.emptyList();
		}
		return entityList.stream().filter(Objects::nonNull).map(getterFn).distinct().collect(Collectors.toList());
	}

	@Override
	public <ST, FT> Map<ST, FT> getValueMapOfField(SFunction<T, ST> idFieldFn, List<ST> idValList, SFunction<T, FT> getterFn) {
		if (idValList != null && idValList.isEmpty()) {
			return Collections.emptyMap();
		}
		LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<T>()
				.select(idFieldFn, getterFn)
				.in(idValList != null, idFieldFn, idValList);
		List<T> entityList = getEntityList(queryWrapper);
		if (entityList == null) {
			return Collections.emptyMap();
		}
		return entityList.stream().collect(Collectors.toMap(idFieldFn, getterFn));
	}

	@Override
	public List<T> getEntityListLimit(Wrapper queryWrapper, int limitCount) {
		// 如果是动态join，则调用JoinsBinder
		if(queryWrapper instanceof DynamicJoinQueryWrapper){
			Pagination pagination = new Pagination();
			pagination.setPageIndex(1).setPageSize(limitCount);
			return Binder.joinQueryList((DynamicJoinQueryWrapper)queryWrapper, getEntityClass(), pagination);
		}
		Page<T> page = new Page<>(1, limitCount);
		page.setSearchCount(false);
		page = super.page(page, queryWrapper);
		return page.getRecords();
	}

	@Override
	public T getSingleEntity(Wrapper queryWrapper) {
		// 如果是动态join，则调用JoinsBinder
		if(queryWrapper instanceof DynamicJoinQueryWrapper){
			return (T)Binder.joinQueryOne((DynamicJoinQueryWrapper)queryWrapper, getEntityClass());
		}
		List<T> entityList = getEntityListLimit(queryWrapper, 1);
		if(V.notEmpty(entityList)){
			return entityList.get(0);
		}
		return null;
	}

	@Override
	public <FT> boolean exists(SFunction<T, FT> getterFn, Object value) {
		QueryWrapper<T> queryWrapper = new QueryWrapper();
		String column = this.getColumnByField(BeanUtils.convertSFunctionToFieldName(getterFn));
		queryWrapper.select(column).eq(column, value);
		return exists(queryWrapper);
	}

	@Override
	public boolean exists(Wrapper queryWrapper) {
		// 支持 ChainQuery
		if (queryWrapper instanceof ChainQuery) {
			queryWrapper = ((ChainQuery<?>) queryWrapper).getWrapper();
		}
		T entity = getSingleEntity(queryWrapper);
		return entity != null;
	}

	@Override
	public <FT> boolean isValueUnique(SFunction<T, FT> getterFn, Object value, Serializable id) {
		String field = BeanUtils.convertSFunctionToFieldName(getterFn);
		return isValueUnique(field, value, id);
	}

	@Override
	public boolean isValueUnique(String field, Object value, Serializable id) {
		if (V.isEmpty(value)) {
			throw new BusinessException(Status.FAIL_VALIDATION, "exception.business.baseServiceImpl.isValueUnique.message");
		}
		String column = getColumnByField(field);
		QueryWrapper<Object> wrapper = Wrappers.query().eq(column, value);
		if (V.notEmpty(id)) {
			String pk = ContextHolder.getIdColumnName(getEntityClass());
			wrapper.ne(pk, id);
		}
		return !exists(wrapper);
	}

	@Override
	public List<T> getEntityListByIds(List ids) {
		QueryWrapper<T> queryWrapper = new QueryWrapper();
		String pk = ContextHolder.getIdColumnName(getEntityClass());
		queryWrapper.in(pk, ids);
		return getEntityList(queryWrapper);
	}

	@Override
	public List<Map<String, Object>> getMapList(Wrapper queryWrapper) {
		return getMapList(queryWrapper, null);
	}

	@Override
	public List<Map<String, Object>> getMapList(Wrapper queryWrapper, Pagination pagination) {
		if(pagination != null){
			IPage page = convertToIPage(pagination);
			IPage<Map<String, Object>> resultPage = super.pageMaps(page, queryWrapper);
			// 如果重新执行了count进行查询，则更新pagination中的总数
			if(page.searchCount()){
				pagination.setTotalCount(page.getTotal());
			}
			return resultPage.getRecords();
		}
		else{
			List<Map<String, Object>> list = super.listMaps(queryWrapper);
			if(list == null){
				list = Collections.emptyList();
			}
			else if(list.size() > BaseConfig.getBatchSize()){
				log.warn("单次查询记录数量过大，请及时检查优化。返回结果数={}", list.size());
			}
			return list;
		}
	}

	@Override
	public List<LabelValue> getLabelValueList(Wrapper queryWrapper) {
		String sqlSelect = queryWrapper.getSqlSelect();
		// 最少2个属性：label, value , (ext , parentId)
		if(V.isEmpty(sqlSelect) || S.countMatches(sqlSelect, Cons.SEPARATOR_COMMA) < 1){
			throw new InvalidUsageException("exception.invalidUsage.baseService.callGetLabelValueListFailed");
		}
		List<T> entityList = getEntityList(queryWrapper);
		if(entityList == null){
			return Collections.emptyList();
		}
		else if(entityList.size() > BaseConfig.getBatchSize()){
			log.warn("单次查询记录数量过大，建议您及时检查优化。返回结果数={}", entityList.size());
		}
		// 转换为LabelValue
		String[] selectArray = sqlSelect.split(Cons.SEPARATOR_COMMA);
		// 是否有ext字段
		boolean hasExt = selectArray.length > 2;
		List<LabelValue> labelValueList = new ArrayList<>(entityList.size());
		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(getEntityClass());
		boolean hasParentNode = propInfo.containsField(Cons.FieldName.parentId.name());
		for(T entity : entityList){
			if (V.isEmpty(entity)) {
				log.warn("getLabelValueList查询指定字段 {} 在数据库当前记录所有字段均为空值，已自动忽略", sqlSelect);
				continue;
			}
			String label = propInfo.getFieldByColumn(selectArray[0]), value = propInfo.getFieldByColumn(selectArray[1]), ext;
			Object labelVal = BeanUtils.getProperty(entity, label);
			Object valueVal = BeanUtils.getProperty(entity, value);
			// 如果key和value的的值都为null的时候map也为空，则不处理此项
			if (V.isEmpty(labelVal) && V.isEmpty(valueVal)) {
				continue;
			}
			// 兼容oracle大写
			LabelValue labelValue = new LabelValue(S.valueOf(labelVal), valueVal);
			// 设置ext
			if (hasExt) {
				ext = propInfo.getFieldByColumn(selectArray[2]);
				labelValue.setExt(BeanUtils.getProperty(entity, ext));
			}
			// 设置 parentId
			if(hasParentNode) {
				Object parentId = BeanUtils.getProperty(entity, Cons.FieldName.parentId.name());
				labelValue.setParentId(parentId);
			}
			labelValueList.add(labelValue);
		}
		if (hasParentNode) {
			return BeanUtils.buildTree(labelValueList, LabelValue::getValue, LabelValue::getParentId, LabelValue::setChildren);
		}
		return labelValueList;
	}

	@Override
	public <ID> Map<ID, String> getId2NameMap(List<ID> entityIds, SFunction<T,?> getterFn) {
		if(V.isEmpty(entityIds)){
			return Collections.emptyMap();
		}
		EntityInfoCache entityInfo = BindingCacheManager.getEntityInfoByClass(getEntityClass());
		String columnName = entityInfo.getColumnByField(BeanUtils.convertSFunctionToFieldName(getterFn));
		QueryWrapper<T> queryWrapper = new QueryWrapper<T>().select(
				entityInfo.getIdColumn(),
				columnName
		).in(entityInfo.getIdColumn(), entityIds);
		// map列表
		List<Map<String, Object>> mapList = getMapList(queryWrapper);
		if(V.isEmpty(mapList)){
			return Collections.emptyMap();
		}
		Map<ID, String> idNameMap = new HashMap<>(mapList.size());
		for(Map<String, Object> map : mapList){
			ID key = (ID)map.get(entityInfo.getIdColumn());
			String value = S.valueOf(map.get(columnName));
			idNameMap.put(key, value);
		}
		return idNameMap;
	}

	@Override
	public Map<String, T> getId2EntityMap(List entityIds, SFunction<T,?>... getterFns) {
		QueryWrapper<T> queryWrapper = new QueryWrapper();
		Class<?> entityClass = getEntityClass();
		String pk = ContextHolder.getIdColumnName(entityClass);
		if(V.notEmpty(getterFns)) {
			List<String> columns = new ArrayList<>(getterFns.length+1);
			columns.add(pk);
			EntityInfoCache entityInfo = BindingCacheManager.getEntityInfoByClass(entityClass);
			for(SFunction<T,?> getter : getterFns) {
				String fieldName = BeanUtils.convertSFunctionToFieldName(getter);
				String columnName = entityInfo.getColumnByField(fieldName);
				if(!columns.contains(columnName)) {
					columns.add(columnName);
				}
			}
			queryWrapper.select(S.toStringArray(columns));
		}
		queryWrapper.in(pk, entityIds);
		List<T> entityList = getEntityList(queryWrapper);
		if(V.isEmpty(entityList)) {
			return Collections.emptyMap();
		}
		return BeanUtils.convertToStringKeyObjectMap(entityList, pk);
	}

	@Override
	public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
		return super.getMap(queryWrapper);
	}

	/**
	 * 获取View Object对象
	 * @param id 主键
	 * @return entity
	 */
	@Override
	public <VO> VO getViewObject(Serializable id, Class<VO> voClass){
		T entity = getEntity(id);
		if(entity == null){
			return null;
		}
		// 绑定
		return Binder.convertAndBindRelations(entity, voClass);
	}

	@Override
	public <VO> VO getViewObject(Wrapper queryWrapper, Class<VO> voClass) {
		T entity = getSingleEntity(queryWrapper);
		if(entity == null){
			return null;
		}
		// 绑定
		return Binder.convertAndBindRelations(entity, voClass);
	}

	@Override
	public <VO> VO getViewObject(String fieldKey, Object fieldValue, Class<VO> voClass) {
		String column = getColumnByField(fieldKey);
		QueryWrapper<T> queryWrapper = new QueryWrapper<T>().eq(column, fieldValue);
		return getViewObject(queryWrapper, voClass);
	}

	@Override
	public <VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> voClass) {
		if(queryWrapper != null && queryWrapper.getSqlSelect() == null) {
			WrapperHelper.optimizeSelect(queryWrapper, getEntityClass(), voClass);
		}
		List<T> entityList = getEntityList(queryWrapper, pagination);
		// 自动转换为VO并绑定关联对象
		return Binder.convertAndBindRelations(entityList, voClass);
	}

	@Override
	public <VO> List<VO> getViewObjectTree(Serializable rootNodeId, Class<VO> voClass, SFunction<T, String> getParentIdsPath,
										   @Nullable SFunction<T, Comparable<?>> getSortId) {
		LambdaQueryWrapper<T> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.select(getEntityClass(), WrapperHelper.buildSelectPredicate(voClass));
		// 排序
		queryWrapper.orderByAsc(getSortId != null, getSortId);

		if (V.isEmpty(rootNodeId)) {
			return BeanUtils.buildTree(getViewObjectList(queryWrapper, null, voClass));
		}
		T entity = getEntity(rootNodeId);
		// 无根节点
		if (entity == null) {
			return Collections.emptyList();
		}
		if (getParentIdsPath != null) {
			String parentPath = getParentIdsPath.apply(entity);
			if (V.isEmpty(parentPath)) {
				// parentPath格式：/([0-9a-f]+,)+/g
				parentPath = rootNodeId + Cons.SEPARATOR_COMMA;
			}
			queryWrapper.likeRight(getParentIdsPath, parentPath);
		}
		List<T> entityList = getEntityList(queryWrapper);
		if (V.notEmpty(rootNodeId)) {
			entityList.add(entity);
		}
		// 自动转换为VO并绑定关联对象
		List<VO> voList = Binder.convertAndBindRelations(entityList, voClass);
		return BeanUtils.buildTree(voList, rootNodeId);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean sort(SortParamDTO<?> sortParam, SFunction<T, Number> sortField) {
		return sort(sortParam, sortField, null, null);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean sort(SortParamDTO<?> sortParam, SFunction<T, Number> sortField, SFunction<T, Serializable> parentIdField, SFunction<T, String> parentIdsField) {
		Serializable id = sortParam.getId();
		Serializable newParentId = sortParam.getNewParentId();
		Long newSortId = sortParam.getNewSortId();
		Long oldSortId = sortParam.getOldSortId();

		boolean isTree = parentIdField != null;
		if (isTree && newParentId == null) {
			throw new BusinessException("exception.business.baseServiceImpl.sort.nullParentId");
		}
		// tree 数据层级变化（层级变化 oldSortId 应为 null）
		boolean levelChange = oldSortId == null;
		if (!isTree && levelChange) {
			// 非 tree 结构数据，无层级变化，必须指定 oldSortId
			throw new BusinessException("exception.business.baseServiceImpl.sort.nullOldSortId");
		}
		// 上移（层级变化同为上移）
		boolean moveUp = levelChange || oldSortId > newSortId;
		// 排序起始值
		AtomicLong start = new AtomicLong(moveUp ? newSortId : oldSortId);
		long end = !moveUp ? newSortId : (levelChange ? Long.MAX_VALUE : oldSortId);

		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(getEntityClass());
		String idColumn = propInfo.getIdColumn();
		String idFieldName = propInfo.getIdFieldName();

		LambdaQueryWrapper<T> query = Wrappers.<T>query().select(idColumn).lambda();
		query.orderByAsc(sortField).eq(isTree, parentIdField, newParentId);
		if (levelChange) {
			query.ge(sortField, start.get());
		} else {
			query.between(sortField, start.get(), end);
		}
		boolean exchange = false;
		List<Object> ids = new ArrayList<>();
		for (Map<String, Object> map : getMapList(query)) {
			Object key = map.get(idColumn);
			if (V.fuzzyEqual(key, id)){
				exchange = true;
			} else {
				ids.add(key);
			}
		}
		// 起始值小于1时重置排序，从1开始
		if (start.get() <= 0) {
			start.set(1);
		}
		// 越界
		boolean crossBorder = end - start.get() + (levelChange ? 0 : 1) < ids.size();

		SerializedLambda sortFieldLambda = BeanUtils.getSerializedLambda(sortField);
		String sortFieldName = PropertyNamer.methodToProperty(sortFieldLambda.getImplMethodName());

		List<T> collect = new ArrayList<>();
		Function<Object,T> addEntity = idValue ->{
			T entity;
			try {
				entity = getEntityClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			BeanUtils.setProperty(entity, idFieldName, idValue);
			BeanUtils.setProperty(entity, sortFieldName, start.getAndIncrement());
			collect.add(entity);
			return entity;
		};
		if (levelChange) {
			SerializedLambda parentIdFieldLambda = BeanUtils.getSerializedLambda(parentIdField);
			String parentIdFieldName = PropertyNamer.methodToProperty(parentIdFieldLambda.getImplMethodName());
			T entity = addEntity.apply(id);
			BeanUtils.setProperty(entity, parentIdFieldName, newParentId);
			if (parentIdsField != null) {
				T parentEntity = getEntity(newParentId);
				SerializedLambda parentIdsFieldLambda = BeanUtils.getSerializedLambda(parentIdsField);
				String parentIdsFieldName = PropertyNamer.methodToProperty(parentIdsFieldLambda.getImplMethodName());
				BiFunction<String, Serializable, String> buildPids = (pids, idValue) -> V.isEmpty(pids) ? S.valueOf(idValue) : pids + "," + idValue;
				String parentIds = buildPids.apply(parentEntity == null ? null : parentIdsField.apply(parentEntity), newParentId);
				BeanUtils.setProperty(entity, parentIdsFieldName, parentIds);
				// 更新子孙节点的 parentPath
				String parentPathColumnName = propInfo.getColumnByField(parentIdsFieldName);
				QueryWrapper<T> queryWrapper = Wrappers.<T>query().select(idColumn, parentPathColumnName)
						.likeRight(parentPathColumnName, buildPids.apply(parentIdsField.apply(getEntity(id)), id));
				List<T> entityList = getEntityList(queryWrapper);
				if (V.notEmpty(entityList)) {
					Map<Serializable, List<T>> pid2entityMap = entityList.stream().collect(Collectors.groupingBy(parentIdField));
					updateParentIds(idFieldName, pid2entityMap, id, buildPids.apply(parentIds, id), parentIdsFieldName);
					collect.addAll(entityList);
				}
			}
		} else if (!exchange) {
			log.warn("无效排序（非层级变化，且无有效位置交换）");
			return false;
		} else if (moveUp) {
			addEntity.apply(id);
		}
		ids.forEach(addEntity::apply);
		if (!moveUp) {
			addEntity.apply(id);
		}
		if (crossBorder) {
			LambdaQueryWrapper<T> queryWrapper = Wrappers.<T>query().select(idColumn).lambda();
			queryWrapper.orderByAsc(sortField).eq(isTree, parentIdField, newParentId).gt(sortField, end);
			getMapList(queryWrapper).stream().map(map -> map.get(idColumn)).forEach(addEntity::apply);
		}
		return updateEntities(collect);
	}

	/**
	 * 递归更新子孙节点的 parentIds
	 *
	 * @param idFieldName
	 * @param pid2entityMap
	 * @param pid
	 * @param parentIds
	 * @param parentIdsFieldName
	 */
	private void updateParentIds(String idFieldName, Map<Serializable, List<T>> pid2entityMap, Serializable pid, String parentIds, String parentIdsFieldName) {
		for (T item : pid2entityMap.get(pid)) {
			BeanUtils.setProperty(item, parentIdsFieldName, parentIds);
			Serializable idValue = (Serializable)BeanUtils.getProperty(item, idFieldName);
			if (pid2entityMap.containsKey(idValue)) {
				updateParentIds(idFieldName, pid2entityMap, idValue, parentIds + "," + idValue, parentIdsFieldName);
			}
		}
	}

	/***
	 * 转换为IPage
	 * @param pagination 分页
	 * @return
	 */
	protected Page<T> convertToIPage(Pagination pagination){
		return ServiceAdaptor.convertToIPage(pagination, getEntityClass());
	}

	/***
	 * 转换为IPage（已废弃）
	 * @see #convertToIPage(Pagination)
	 * @param queryWrapper 查询条件
	 * @param pagination 分页
	 * @return
	 */
	@Deprecated
	protected Page<T> convertToIPage(Wrapper queryWrapper, Pagination pagination){
		return ServiceAdaptor.convertToIPage(pagination, getEntityClass());
	}

	/**
	 * 获取字段对应的列名
	 * @return
	 */
	protected String getColumnByField(String fieldName) {
		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(getEntityClass());
		return propInfo.getColumnByField(fieldName);
	}

	/**
	 * 获取主键值
	 * @param entity
	 * @return
	 */
	protected Object getPrimaryKeyValue(Object entity){
		String pk = ContextHolder.getIdFieldName(entity.getClass());
		return BeanUtils.getProperty(entity, pk);
	}

	/***
	 * 打印警告信息
	 * @param method
	 * @param message
	 */
	private void warning(String method, String message){
		log.warn("{}.{} 调用错误: {}, 请检查！", this.getClass().getSimpleName(), method, message);
	}

}
