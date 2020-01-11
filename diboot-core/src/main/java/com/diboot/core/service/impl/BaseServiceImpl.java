package com.diboot.core.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.binding.binder.EntityBinder;
import com.diboot.core.binding.binder.EntityListBinder;
import com.diboot.core.binding.binder.FieldBinder;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/***
 * CRUD通用接口实现类
 * @author Mazhicheng
 * @param <M> mapper类
 * @param <T> entity类
 * @version 2.0
 * @date 2019/01/01
 */
public class BaseServiceImpl<M extends BaseCrudMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
	private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);
	/**
	 * Entity类与最佳排序字段间的映射缓存
	 */
	private static Map<String, String> ENTITY_ORDER_FIELD_CACHE_MAP = new ConcurrentHashMap<>();

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
	@Transactional(rollbackFor = Exception.class)
	public boolean createEntities(Collection entityList){
		if(V.isEmpty(entityList)){
			warning("createEntities", "参数entityList为空!");
			return false;
		}
		// 批量插入
		return super.saveBatch(entityList, BaseConfig.getBatchSize());
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
			IPage<T> page = convertToIPage(queryWrapper, pagination);
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
			if(map.get(keyValueArray[0]) != null){
				KeyValue kv = new KeyValue(S.valueOf(map.get(keyValueArray[0])), map.get(keyValueArray[1]));
				if(keyValueArray.length > 2){
					kv.setExt(map.get(keyValueArray[2]));
				}
				keyValueList.add(kv);
			}
		}
		return keyValueList;
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
		List<VO> voList = RelationsBinder.convertAndBind(enityList, voClass);
		return voList.get(0);
	}

	@Override
	public <VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> voClass) {
		List<T> entityList = getEntityList(queryWrapper, pagination);
		// 自动转换为VO并绑定关联对象
		List<VO> voList = RelationsBinder.convertAndBind(entityList, voClass);
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
		// 优化排序
		String defaultOrderBy = getDefaultOrderField(queryWrapper);
		//默认id属性存在
		if(!Cons.FieldName.id.name().equals(defaultOrderBy)){
			// 最佳字段不是id（如create_time），但默认查询字段为id，需要清空默认值以免报错
			pagination.clearDefaultOrder();
		}
		return (Page<T>)pagination.toIPage();
	}

	/***
	 * 打印警告信息
	 * @param method
	 * @param message
	 */
	private void warning(String method, String message){
		log.warn(this.getClass().getName() + "."+ method +" 调用错误: "+message+", 请检查！");
	}

	/**
	 * 初始化Entity和VO的class
	 */
	private String getDefaultOrderField(Wrapper queryWrapper){
		Class entityClass = BeanUtils.getGenericityClass(this, 1);
		if(entityClass == null){
			return null;
		}
		if(!ENTITY_ORDER_FIELD_CACHE_MAP.containsKey(entityClass.getName())){
			// 提取字段，如果有升序id首选id
			Field field = BeanUtils.extractField(entityClass, Cons.FieldName.id.name());
			if(field != null){
				TableField tableFieldAnno = field.getAnnotation(TableField.class);
				if(tableFieldAnno == null || tableFieldAnno.exist() == true){
					ENTITY_ORDER_FIELD_CACHE_MAP.put(entityClass.getName(), Cons.FieldName.id.name());
				}
			}
			if(!ENTITY_ORDER_FIELD_CACHE_MAP.containsKey(entityClass.getName())){
				ENTITY_ORDER_FIELD_CACHE_MAP.put(entityClass.getName(), "");
				log.warn("{} 的默认排序字段id不存在，请自行指定！", entityClass.getName());
			}
		}
		return ENTITY_ORDER_FIELD_CACHE_MAP.get(entityClass.getName());
	}

}