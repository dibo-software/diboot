package com.diboot.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diboot.core.binding.manager.AnnotationBindingManager;
import com.diboot.core.binding.EntityBinder;
import com.diboot.core.binding.EntityListBinder;
import com.diboot.core.binding.FieldBinder;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

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

	/***
	 * 获取当前的Mapper对象
	 * @return
	 */
	protected M getMapper(){
		return baseMapper;
	}

	@Override
	public T getEntity(Serializable id){
		return super.getById(id);
	}

	@Override
	public boolean createEntity(T entity) {
		if(entity == null){
			warning("createModel", "参数entity为null");
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
	public boolean createOrUpdateEntities(Collection entityList) {
		if(V.isEmpty(entityList)){
			warning("createEntities", "参数entityList为空!");
			return false;
		}
		// 批量插入
		return super.saveOrUpdateBatch(entityList, BaseConfig.getBatchSize());
	}

	@Override
	public boolean deleteEntity(Serializable id) {
		boolean success = super.removeById(id);
		return success;
	}

	@Override
	public boolean deleteEntities(Wrapper queryWrapper) throws Exception{
		// 执行
		boolean success = super.remove(queryWrapper);
		return success;
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
			IPage<T> page = convertToIPage(pagination);
			page = super.page(page, queryWrapper);
			// 如果重新执行了count进行查询，则更新pagination中的总数
			if(page.isSearchCount()){
				pagination.set_totalCount(page.getTotal());
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
	public List<Map<String, Object>> getMapList(Wrapper queryWrapper) {
		return getMapList(queryWrapper, null);
	}

	@Override
	public List<Map<String, Object>> getMapList(Wrapper queryWrapper, Pagination pagination) {
		if(pagination != null){
			IPage<T> page = convertToIPage(pagination);
			IPage<Map<String, Object>> resultPage = super.pageMaps(page, queryWrapper);
			// 如果重新执行了count进行查询，则更新pagination中的总数
			if(page.isSearchCount()){
				pagination.set_totalCount(page.getTotal());
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
			log.error("调用错误: getKeyValueList必须用select依次指定返回的Key,Value, ext键值字段，如: new QueryWrapper<Metadata>().lambda().select(Metadata::getItemName, Metadata::getItemValue)");
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
				KeyValue kv = new KeyValue((String)map.get(keyValueArray[0]), map.get(keyValueArray[1]));
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
		List<VO> voList = AnnotationBindingManager.autoConvertAndBind(enityList, voClass);
		return voList.get(0);
	}

	@Override
	public <VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> voClass) {
		List<T> entityList = getEntityList(queryWrapper, pagination);
		// 自动转换为VO并绑定关联对象
		List<VO> voList = AnnotationBindingManager.autoConvertAndBind(entityList, voClass);
		return voList;
	}

	/***
	 * 转换为IPage
	 * @param pagination
	 * @return
	 */
	protected IPage<T> convertToIPage(Pagination pagination){
		if(pagination == null){
			return null;
		}
		IPage<T> page = new Page<T>()
			.setCurrent(pagination.getPageIndex())
			.setSize(pagination.getPageSize())
			// 如果前端传递过来了缓存的总数，则本次不再count统计
			.setTotal(pagination.getTotalCount() > 0? -1 : pagination.getTotalCount())
			.setAscs(S.toSnakeCase(pagination.getAscList()))
			.setDescs(S.toSnakeCase(pagination.getDescList()));
		return page;
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