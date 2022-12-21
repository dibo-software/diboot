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

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
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
import com.diboot.core.binding.parser.PropInfo;
import com.diboot.core.binding.query.dynamic.DynamicJoinQueryWrapper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
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
	public <FT> FT getValueOfField(SFunction<T, ?> idGetterFn, Serializable idVal, SFunction<T, FT> getterFn) {
		LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<T>()
				.select(idGetterFn, getterFn)
				.eq(idGetterFn, idVal);
		T entity = getSingleEntity(queryWrapper);
		if(entity == null){
			return null;
		}
		return getterFn.apply(entity);
	}

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
		beforeCreateEntity(entity);
		return super.save(entity);
	}

	/**
	 * 用于创建之前的自动填充等场景调用
	 */
	protected void beforeCreateEntity(T entity){
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
		Class relatedEntityClass = relatedEntities.get(0).getClass();
		// 获取主键
		Object pkValue = getPrimaryKeyValue(entity);
		String attributeName = BeanUtils.convertToFieldName(relatedEntitySetter);
		// 填充关联关系
		relatedEntities.forEach(relatedEntity-> BeanUtils.setProperty(relatedEntity, attributeName, pkValue));
		// 获取关联对象对应的Service
		BaseService relatedEntityService = ContextHelper.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService != null){
			return relatedEntityService.createEntities(relatedEntities);
		}
		else{
			// 查找mapper
			BaseMapper mapper = ContextHelper.getBaseMapperByEntity(entity.getClass());
			// 新增关联，无service只能循环插入
			for(RE relation : relatedEntities){
				mapper.insert(relation);
			}
			return true;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean createEntities(Collection<T> entityList){
		if(V.isEmpty(entityList)){
			return false;
		}
		if(DbType.SQL_SERVER.getDb().equalsIgnoreCase(ContextHelper.getDatabaseType())){
			for(T entity : entityList){
				createEntity(entity);
			}
			return true;
		}
		else{
			// 批量插入
			return saveBatch(entityList, BaseConfig.getBatchSize());
		}
	}

	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize){
		// 批量插入
		beforeCreateEntities(entityList);
		return super.saveBatch(entityList, batchSize);
	}

	/**
	 * 用于创建之前的自动填充等场景调用
	 */
	protected void beforeCreateEntities(Collection<T> entityList){
		if(V.isEmpty(entityList)){
			return;
		}
		for(T entity : entityList){
			beforeCreateEntity(entity);
		}
	}

	/**
	 * 用于更新之前的自动填充等场景调用
	 */
	protected void beforeUpdateEntity(T entity){
	}

	@Override
	public boolean updateById(T entity) {
		return updateEntity(entity);
	}

	@Override
	public boolean updateEntity(T entity) {
		beforeUpdateEntity(entity);
		boolean success = super.updateById(entity);
		return success;
	}

	@Override
	public boolean updateEntity(T entity, Wrapper updateWrapper) {
		beforeUpdateEntity(entity);
		boolean success = super.update(entity, updateWrapper);
		return success;
	}

	@Override
	public boolean updateEntity(Wrapper updateWrapper) {
		boolean success = super.update(null, updateWrapper);
		return success;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateEntities(Collection<T> entityList) {
		if(V.isEmpty(entityList)){
			return false;
		}
		for(T entity : entityList){
			beforeUpdateEntity(entity);
		}
		boolean success = super.updateBatchById(entityList);
		return success;
	}

	@Override
	public boolean createOrUpdateEntity(T entity) {
		boolean success = super.saveOrUpdate(entity);
		return success;
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

    @Override
    public <R> boolean createOrUpdateN2NRelations(SFunction<R, ?> driverIdGetter, Object driverId,
                                                  SFunction<R, ?> followerIdGetter, List<? extends Serializable> followerIdList) {
        return createOrUpdateN2NRelations(driverIdGetter, driverId, followerIdGetter, followerIdList, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <R> boolean createOrUpdateN2NRelations(SFunction<R, ?> driverIdGetter, Object driverId,
                                                  SFunction<R, ?> followerIdGetter, List<? extends Serializable> followerIdList,
                                                  Consumer<QueryWrapper<R>> queryConsumer, Consumer<R> setConsumer) {
		if (driverId == null) {
			throw new InvalidUsageException("主动ID值不能为空！");
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
			throw new InvalidUsageException("未找到 " + middleTableClass.getName() + " 的 Service 或 Mapper 定义！");
		}
		boolean isExistPk = entityInfo.getIdColumn() != null;

		// 获取主动从动字段名
		String driverFieldName = PropertyNamer.methodToProperty(lambdaMeta.getImplMethodName());
		String followerFieldName = convertGetterToFieldName(followerIdGetter);
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
		List<Serializable> removeFollowerIds = new ArrayList<>();
		for (R entity : oldEntityList) {
			Serializable followerId = (Serializable)BeanUtils.getProperty(entity, followerFieldName);
			if (V.notEmpty(followerIdList) && followerIdList.contains(followerId)) {
				removeFollowerIds.add(followerId);
				continue;
			}
			Serializable id = isExistPk? (Serializable)BeanUtils.getProperty(entity, entityInfo.getPropInfo().getIdFieldName()) : followerId;
			if(id != null) {
				delIds.add(id);
			}
		}
		if (!delIds.isEmpty()) {
			if (isExistPk) {
				if (iService != null) {
					iService.removeByIds(delIds);
				} else {
					baseMapper.deleteBatchIds(delIds);
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
			List<Serializable> newFollowIds = new ArrayList();
			for(Serializable id : followerIdList) {
				if(!removeFollowerIds.contains(id)) {
					newFollowIds.add(id);
				}
			}
            List<R> n2nRelations = new ArrayList<>(newFollowIds.size());
            try {
                for (Serializable followerId : newFollowIds) {
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
                throw new BusinessException(Status.FAIL_EXCEPTION, e);
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
		BaseService relatedEntityService = ContextHelper.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService == null){
			log.error("未能识别到Entity: {} 的Service实现，请检查！", relatedEntityClass.getName());
			return false;
		}
		// 获取主键
		Object pkValue = getPrimaryKeyValue(entity);
		String attributeName = BeanUtils.convertToFieldName(relatedEntitySetter);
		//获取原 关联entity list
		QueryWrapper<RE> queryWrapper = new QueryWrapper();
		queryWrapper.eq(S.toSnakeCase(attributeName), pkValue);
		List<RE> oldRelatedEntities = relatedEntityService.getEntityList(queryWrapper);

		// 遍历更新关联对象
		Set relatedEntityIds = new HashSet();
		if(V.notEmpty(relatedEntities)){
			// 新建 修改 删除
			List<RE> newRelatedEntities = new ArrayList<>();
			for(RE relatedEntity : relatedEntities){
				BeanUtils.setProperty(relatedEntity, attributeName, pkValue);
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
	@Transactional(rollbackFor = Exception.class)
	public <RE,R> boolean deleteEntityAndRelatedEntities(Serializable id, Class<RE> relatedEntityClass, ISetter<RE,R> relatedEntitySetter) {
		boolean success = deleteEntity(id);
		if(!success){
			log.warn("删除Entity失败: {}",id);
			return false;
		}
		// 获取关联对象对应的Service
		BaseService relatedEntityService = ContextHelper.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService == null){
			log.error("未能识别到Entity: {} 的Service实现，请检查！", relatedEntityClass.getName());
			return false;
		}
		// 获取主键的关联属性
		String attributeName = BeanUtils.convertToFieldName(relatedEntitySetter);
		QueryWrapper<RE> queryWrapper = new QueryWrapper<RE>().eq(S.toSnakeCase(attributeName), id);
		// 删除关联子表数据
		return relatedEntityService.deleteEntities(queryWrapper);
	}

	@Override
	public boolean deleteEntity(Serializable id) {
		return super.removeById(id);
	}

    @Override
    public boolean cancelDeletedById(Serializable id) {
		EntityInfoCache info = BindingCacheManager.getEntityInfoByClass(super.getEntityClass());
		String tableName = info.getTableName();
        return this.getMapper().cancelDeletedById(tableName, id) > 0;
    }

    @Override
	public boolean deleteEntities(Wrapper queryWrapper){
		// 执行
		return super.remove(queryWrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteEntities(Collection<? extends Serializable> entityIds) {
		if(V.isEmpty(entityIds)){
			return false;
		}
		return super.removeByIds(entityIds);
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
			IPage<T> page = convertToIPage(queryWrapper, pagination);
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
		LambdaQueryWrapper query = null;
		List<T> entityList = null;
		// 优化SQL，只查询当前字段
		if(queryWrapper instanceof QueryWrapper){
			query = ((QueryWrapper)queryWrapper).lambda();
		}
		else if(queryWrapper instanceof LambdaQueryWrapper){
			query = ((LambdaQueryWrapper) queryWrapper);
		}
		else {
			throw new InvalidUsageException("不支持的Wrapper类型：" + (queryWrapper == null ? "null" : queryWrapper.getClass()));
		}
		// 如果是动态join，则调用JoinsBinder
		query.select(getterFn);
		if(queryWrapper instanceof DynamicJoinQueryWrapper){
			entityList = Binder.joinQueryList( (DynamicJoinQueryWrapper)queryWrapper, entityClass, null);
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
	public List<T> getEntityListLimit(Wrapper queryWrapper, int limitCount) {
		// 如果是动态join，则调用JoinsBinder
		if(queryWrapper instanceof DynamicJoinQueryWrapper){
			Pagination pagination = new Pagination();
			pagination.setPageIndex(1).setPageSize(limitCount);
			return Binder.joinQueryList((DynamicJoinQueryWrapper)queryWrapper, entityClass, pagination);
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
			return (T)Binder.joinQueryOne((DynamicJoinQueryWrapper)queryWrapper, entityClass);
		}
		List<T> entityList = getEntityListLimit(queryWrapper, 1);
		if(V.notEmpty(entityList)){
			return entityList.get(0);
		}
		return null;
	}

	@Override
	public boolean exists(IGetter<T> getterFn, Object value) {
		QueryWrapper<T> queryWrapper = new QueryWrapper();
		String field = BeanUtils.convertToFieldName(getterFn);
		String column = BindingCacheManager.getEntityInfoByClass(getEntityClass()).getColumnByField(field);
		queryWrapper.select(column).eq(column, value);
		return exists(queryWrapper);
	}

	@Override
	public boolean exists(Wrapper queryWrapper) {
		if((queryWrapper instanceof QueryWrapper) && queryWrapper.getSqlSelect() == null){
			String pk = ContextHelper.getIdColumnName(getEntityClass());
			((QueryWrapper)queryWrapper).select(pk);
		}
		T entity = getSingleEntity(queryWrapper);
		return entity != null;
	}

	@Override
	public List<T> getEntityListByIds(List ids) {
		QueryWrapper<T> queryWrapper = new QueryWrapper();
		String pk = ContextHelper.getIdColumnName(getEntityClass());
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
			IPage page = convertToIPage(queryWrapper, pagination);
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
		// 最多支持3个属性：label, value, ext
		if(V.isEmpty(sqlSelect) || S.countMatches(sqlSelect, Cons.SEPARATOR_COMMA) > 2){
			log.error("调用错误: getLabelValueList必须用select依次指定返回的Label,Value, ext键值字段，如: new QueryWrapper<Dictionary>().lambda().select(Dictionary::getItemName, Dictionary::getItemValue)");
			return Collections.emptyList();
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
		for(T entity : entityList){
			PropInfo propInfo = BindingCacheManager.getPropInfoByClass(entityClass);
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
			labelValueList.add(labelValue);
		}
		return labelValueList;
	}

	@Override
	public <ID> Map<ID, String> getId2NameMap(List<ID> entityIds, IGetter<T> getterFn) {
		if(V.isEmpty(entityIds)){
			return Collections.emptyMap();
		}
		String fieldName = BeanUtils.convertToFieldName(getterFn);
		EntityInfoCache entityInfo = BindingCacheManager.getEntityInfoByClass(this.getEntityClass());
		String columnName = entityInfo.getColumnByField(fieldName);
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
			ID key = (ID)MapUtils.getIgnoreCase(map, entityInfo.getIdColumn());
			String value = S.valueOf(MapUtils.getIgnoreCase(map, columnName));
			idNameMap.put(key, value);
		}
		return idNameMap;
	}

	@Override
	public Map<String, T> getId2EntityMap(List entityIds, IGetter<T>... selectFlds) {
		QueryWrapper<T> queryWrapper = new QueryWrapper();
		String pk = ContextHelper.getIdColumnName(getEntityClass());
		if(V.notEmpty(selectFlds)) {
			queryWrapper.select(pk);
			EntityInfoCache entityInfo = BindingCacheManager.getEntityInfoByClass(this.getEntityClass());
			for(IGetter<T> getter : selectFlds) {
				String fieldName = BeanUtils.convertToFieldName(getter);
				String columnName = entityInfo.getColumnByField(fieldName);
				if(columnName != null) {
					queryWrapper.select(columnName);
				}
			}
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
	public <VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> voClass) {
		WrapperHelper.optimizeSelect(queryWrapper, getEntityClass(), voClass);
		List<T> entityList = getEntityList(queryWrapper, pagination);
		// 自动转换为VO并绑定关联对象
		List<VO> voList = Binder.convertAndBindRelations(entityList, voClass);
		return voList;
	}

	/***
	 * 转换为IPage
	 * @param queryWrapper 查询条件
	 * @param pagination 分页
	 * @return
	 */
	protected Page<T> convertToIPage(Wrapper queryWrapper, Pagination pagination){
		return ServiceAdaptor.convertToIPage(pagination, entityClass);
	}

	/**
	 * 获取主键值
	 * @param entity
	 * @return
	 */
	private Object getPrimaryKeyValue(Object entity){
		String pk = ContextHelper.getIdFieldName(entity.getClass());
		return BeanUtils.getProperty(entity, pk);
	}

	/**
	 * 转换SFunction为属性名
	 * @param getterFn
	 * @param <R>
	 * @return
	 */
	private <R> String convertGetterToFieldName(SFunction<R, ?> getterFn) {
		LambdaMeta lambdaMeta = LambdaUtils.extract(getterFn);
		String fieldName = PropertyNamer.methodToProperty(lambdaMeta.getImplMethodName());
		return fieldName;
	}

	/***
	 * 打印警告信息
	 * @param method
	 * @param message
	 */
	private void warning(String method, String message){
		log.warn(this.getClass().getSimpleName() + ".{} 调用错误: {}, 请检查！", method, message);
	}

}
