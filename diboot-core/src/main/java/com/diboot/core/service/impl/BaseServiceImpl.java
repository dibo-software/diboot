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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diboot.core.binding.Binder;
import com.diboot.core.binding.binder.EntityBinder;
import com.diboot.core.binding.binder.EntityListBinder;
import com.diboot.core.binding.binder.FieldBinder;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.*;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/***
 * CRUD通用接口实现类
 * @author mazc@dibo.ltd
 * @param <M> mapper类
 * @param <T> entity类
 * @version 2.0
 * @date 2019/01/01
 */
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
	public T getEntity(Serializable id){
		return super.getById(id);
	}

	@Override
	public boolean createEntity(T entity) {
		if(entity == null){
			warning("createEntity", "参数entity为null");
			return false;
		}
		return super.save(entity);
	}

	@Override
	public <RE, R> boolean createEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter) {
		boolean success = createEntity(entity);
		if(!success){
			log.warn("新建Entity失败: {}", entity.toString());
			return false;
		}
		if(V.isEmpty(relatedEntities)){
			return success;
		}
		Class relatedEntityClass = BeanUtils.getTargetClass(relatedEntities.get(0));
		// 获取关联对象对应的Service
		BaseService relatedEntityService = ContextHelper.getBaseServiceByEntity(relatedEntityClass);
		if(relatedEntityService == null){
			log.error("未能识别到Entity: {} 的Service实现，请检查！", relatedEntityClass.getName());
			return false;
		}
		// 获取主键
		Object pkValue = getPrimaryKeyValue(entity);
		String attributeName = BeanUtils.convertToFieldName(relatedEntitySetter);
		// 填充关联关系
		relatedEntities.stream().forEach(relatedEntity->{
			BeanUtils.setProperty(relatedEntity, attributeName, pkValue);
		});
		return relatedEntityService.createEntities(relatedEntities);
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
			return super.saveBatch(entityList, BaseConfig.getBatchSize());
		}
	}

	@Override
	public boolean updateEntity(T entity) {
		boolean success = super.updateById(entity);
		return success;
	}

	@Override
	public boolean updateEntity(T entity, Wrapper updateWrapper) {
		boolean success = super.update(entity, updateWrapper);
		return success;
	}

	@Override
	public boolean updateEntity(Wrapper updateWrapper) {
		boolean success = super.update(null, updateWrapper);
		return success;
	}

	@Override
	public boolean updateEntities(Collection<T> entityList) {
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
	@Transactional(rollbackFor = Exception.class)
	public <RE,R> boolean updateEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE,R> relatedEntitySetter) {
		boolean success = updateEntity(entity);
		if(!success){
			log.warn("更新Entity失败: {}", entity.toString());
			return false;
		}
		// 获取关联entity的类
		Class relatedEntityClass = null;
		if(V.notEmpty(relatedEntities)){
			relatedEntityClass = BeanUtils.getTargetClass(relatedEntities.get(0));
		}
		else{
			try{
				relatedEntityClass = Class.forName(BeanUtils.getSerializedLambda(relatedEntitySetter).getImplClass().replaceAll("/", "."));
			}
			catch (Exception e){
				log.warn("无法识别关联Entity的Class", e.getMessage());
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
			log.error("未能识别到Entity: {} 的Service实现，请检查！", relatedEntityClass.getClass().getName());
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
	public boolean deleteEntities(Wrapper queryWrapper){
		// 执行
		return super.remove(queryWrapper);
	}

	@Override
	public boolean deleteEntities(Collection<? extends Serializable> entityIds) {
		return super.removeByIds(entityIds);
	}

	@Override
	public int getEntityListCount(Wrapper queryWrapper) {
		return super.count(queryWrapper);
	}

	@Override
	public List<T> getEntityList(Wrapper queryWrapper) {
		return getEntityList(queryWrapper, null);
	}

	@Override
	public List<T> getEntityList(Wrapper queryWrapper, Pagination pagination) {
		if(pagination != null){
			IPage<T> page = convertToIPage(queryWrapper, pagination);
			page = super.page(page, queryWrapper);
			// 如果重新执行了count进行查询，则更新pagination中的总数
			if(page.isSearchCount()){
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
				log.warn("单次查询记录数量过大，返回结果数={}", list.size());
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
	public <FT> List<FT> getValuesOfField(Wrapper queryWrapper, SFunction<T, ?> getterFn){
		SerializedLambda lambda = LambdaUtils.resolve(getterFn);
		String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
		String columnName = S.toSnakeCase(fieldName);
		// 优化SQL，只查询当前字段
		if(queryWrapper instanceof QueryWrapper){
			if(V.isEmpty(queryWrapper.getSqlSelect())){
				((QueryWrapper)queryWrapper).select(columnName);
			}
		}
		else if(queryWrapper instanceof LambdaQueryWrapper){
			if(V.isEmpty(queryWrapper.getSqlSelect())){
				((LambdaQueryWrapper) queryWrapper).select(getterFn);
			}
		}
		List<Map<String, Object>> mapList = getMapList(queryWrapper);
		if(V.isEmpty(mapList)){
			return Collections.emptyList();
		}
		String columnNameUC = V.notEmpty(columnName)? columnName.toUpperCase() : null;
		List<FT> fldValues = new ArrayList<>(mapList.size());
		for(Map<String, Object> map : mapList){
			if(V.isEmpty(map)){
				continue;
			}
			if(map.containsKey(columnName)){
				FT value = (FT) map.get(columnName);
				if(!fldValues.contains(value)){
					fldValues.add(value);
				}
			}
			else if(columnNameUC != null && map.containsKey(columnNameUC)){
				FT value = (FT) map.get(columnNameUC);
				if(!fldValues.contains(value)){
					fldValues.add(value);
				}
			}
		}
		return fldValues;
	}

	@Override
	public List<T> getEntityListLimit(Wrapper queryWrapper, int limitCount) {
		IPage<T> page = new Page<>(1, limitCount);
		page = super.page(page, queryWrapper);
		return page.getRecords();
	}

	@Override
	public T getSingleEntity(Wrapper queryWrapper) {
		List<T> entityList = getEntityListLimit(queryWrapper, 1);
		if(V.notEmpty(entityList)){
			return entityList.get(0);
		}
		return null;
	}

	@Override
	public boolean exists(IGetter<T> getterFn, Object value) {
		QueryWrapper<T> queryWrapper = new QueryWrapper();
		queryWrapper.eq(BeanUtils.convertToFieldName(getterFn), value);
		return exists(queryWrapper);
	}

	@Override
	public boolean exists(Wrapper queryWrapper) {
		List<T> entityList = getEntityListLimit(queryWrapper, 1);
		boolean isExists = V.notEmpty(entityList) && entityList.size() > 0;
		entityList = null;
		return isExists;
	}

	@Override
	public List<T> getEntityListByIds(List ids) {
		QueryWrapper<T> queryWrapper = new QueryWrapper();
		queryWrapper.in(Cons.FieldName.id.name(), ids);
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
			if(page.isSearchCount()){
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
				log.warn("单次查询记录数量过大，返回结果数={}", list.size());
			}
			return list;
		}
	}

	@Override
	public List<KeyValue> getKeyValueList(Wrapper queryWrapper) {
		String sqlSelect = queryWrapper.getSqlSelect();
		// 最多支持3个属性：k, v, ext
		if(V.isEmpty(sqlSelect) || S.countMatches(sqlSelect, Cons.SEPARATOR_COMMA) > 2){
			log.error("调用错误: getKeyValueList必须用select依次指定返回的Key,Value, ext键值字段，如: new QueryWrapper<Dictionary>().lambda().select(Dictionary::getItemName, Dictionary::getItemValue)");
			return Collections.emptyList();
		}
		// 获取mapList
		List<Map<String, Object>> mapList = super.listMaps(queryWrapper);
		if(mapList == null){
			return Collections.emptyList();
		}
		// 转换为Key-Value键值对
		String[] keyValueArray = sqlSelect.split(Cons.SEPARATOR_COMMA);
		List<KeyValue> keyValueList = new ArrayList<>(mapList.size());
		for(Map<String, Object> map : mapList){
			String key = keyValueArray[0], value = keyValueArray[1], ext = null;
			// 兼容oracle大写
			if(map.containsKey(key) == false && map.containsKey(key.toUpperCase())){
				key = key.toUpperCase();
			}
			if(map.containsKey(value) == false && map.containsKey(value.toUpperCase())){
				value = value.toUpperCase();
			}
			if(map.containsKey(key)){
				KeyValue kv = new KeyValue(S.valueOf(map.get(key)), map.get(value));
				if(keyValueArray.length > 2){
					ext = keyValueArray[2];
					if(map.containsKey(ext) == false && map.containsKey(ext.toUpperCase())){
						ext = ext.toUpperCase();
					}
					kv.setExt(map.get(ext));
				}
				keyValueList.add(kv);
			}
		}
		return keyValueList;
	}

	@Override
	public Map<String, Object> getKeyValueMap(Wrapper queryWrapper) {
		List<KeyValue> keyValueList = getKeyValueList(queryWrapper);
		return BeanUtils.convertKeyValueList2Map(keyValueList);
	}

	@Override
	public FieldBinder<T> bindingFieldTo(List voList){
		return new FieldBinder<>(this, voList);
	}

	@Override
    public EntityBinder<T> bindingEntityTo(List voList){
	    return new EntityBinder<>(this, voList);
    }

	@Override
	public EntityListBinder<T> bindingEntityListTo(List voList){
		return new EntityListBinder<>(this, voList);
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
		List<T> enityList = new ArrayList<>();
		enityList.add(entity);
		// 绑定
		List<VO> voList = Binder.convertAndBindRelations(enityList, voClass);
		return voList.get(0);
	}

	@Override
	public <VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> voClass) {
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
		if(pagination == null){
			return null;
		}
		// 如果是默认id排序
		if(pagination.isDefaultOrderBy()){
			// 优化排序
			String pk = getPrimaryKeyField();
			// 主键非有序id字段，需要清空默认排序以免报错
			if(!Cons.FieldName.id.name().equals(pk)){
				log.warn("{} 的主键非有序id，无法自动设置排序字段，请自行指定！", entityClass.getName());
				pagination.clearDefaultOrder();
			}
		}
		return (Page<T>)pagination.toPage();
	}

	/**
	 * 获取当前主键字段名
	 * @return
	 */
	private String getPrimaryKeyField(){
		Class entityClass = BeanUtils.getGenericityClass(this, 1);
		if(entityClass != null){
			return ContextHelper.getPrimaryKey(entityClass);
		}
		return null;
	}

	/**
	 * 获取主键值
	 * @param entity
	 * @return
	 */
	private Object getPrimaryKeyValue(Object entity){
		String pk = ContextHelper.getPrimaryKey(entity.getClass());
		return BeanUtils.getProperty(entity, pk);
	}

	/***
	 * 打印警告信息
	 * @param method
	 * @param message
	 */
	private void warning(String method, String message){
		log.warn(this.getClass().getName() + "."+ method +" 调用错误: "+message+", 请检查！");
	}

}