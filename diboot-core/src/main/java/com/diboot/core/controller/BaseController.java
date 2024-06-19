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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.helper.WrapperHelper;
import com.diboot.core.binding.parser.PropInfo;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.data.query.BaseCriteria;
import com.diboot.core.dto.RelatedDataDTO;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.*;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Pagination;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * Controller的父类
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
public class BaseController {
	private static final Logger log = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	protected HttpServletRequest request;

	/***
	 * 根据DTO构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件，DTO中的非空属性均参与构建)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @return
	 */
    protected <DTO> QueryWrapper<DTO> buildQueryWrapperByDTO(DTO entityOrDto) throws Exception{
		return QueryBuilder.toQueryWrapper(entityOrDto);
	}

	/***
	 * 根据DTO构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件，DTO中的非空属性均参与构建)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @param pagination 分页，如按关联表中的字段排序时需传入pagination
	 * @return
	 */
	protected <DTO> QueryWrapper<DTO> buildQueryWrapperByDTO(DTO entityOrDto, Pagination pagination) throws Exception{
		return QueryBuilder.toQueryWrapper(entityOrDto, pagination);
	}

	/***
	 * 根据请求参数构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件，url中的请求参数参与构建)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @return
	 */
	@Deprecated
    protected <DTO> QueryWrapper<DTO> buildQueryWrapperByQueryParams(DTO entityOrDto) throws Exception{
		return QueryBuilder.toQueryWrapper(entityOrDto, extractQueryParams());
	}

	/***
	 * 根据请求参数构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件，url中的请求参数参与构建)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @param pagination 分页，如按关联表中的字段排序时需传入pagination
	 * @return
	 */
	@Deprecated
	protected <DTO> QueryWrapper<DTO> buildQueryWrapperByQueryParams(DTO entityOrDto, Pagination pagination) throws Exception{
		return QueryBuilder.toQueryWrapper(entityOrDto, extractQueryParams(), pagination);
	}

	/***
	 * 获取请求参数Map
	 * @return
	 */
    protected Map<String, Object> getParamsMap() throws Exception{
		return getParamsMap(null);
	}

	/***
	 * 获取请求参数Map
	 * @return
	 */
	private Map<String, Object> getParamsMap(List<String> paramList) throws Exception{
		Map<String, Object> result = new HashMap<>(8);
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()){
			String paramName = (String) paramNames.nextElement();
			// 如果非要找的参数，则跳过
			if(V.notEmpty(paramList) && !paramList.contains(paramName)){
				continue;
			}
			String[] values = request.getParameterValues(paramName);
			if(V.notEmpty(values)){
				if(values.length == 1){
					if(V.notEmpty(values[0])){
						String paramValue = java.net.URLDecoder.decode(values[0], Cons.CHARSET_UTF8);
						result.put(paramName, paramValue);
					}
				}
				else{
					String[] valueArray = new String[values.length];
					for(int i=0; i<values.length; i++){
						valueArray[i] = java.net.URLDecoder.decode(values[i], Cons.CHARSET_UTF8);
					}
					// 多个值需传递到后台SQL的in语句
					result.put(paramName, valueArray);
				}
			}
		}

		return result;
	}

	/***
	 * 获取请求URI (去除contextPath)
	 * @return
	 */
	protected String getRequestMappingURI(){
		String contextPath = request.getContextPath();
		if(V.notEmpty(contextPath)){
			return S.replace(request.getRequestURI(), contextPath, "");
		}
		return request.getRequestURI();
	}

	/**
	 * 提取请求参数名集合
	 * @return
	 */
	protected Set<String> extractQueryParams(){
		Map<String, Object> paramValueMap = convertParams2Map();
		if(V.notEmpty(paramValueMap)){
			return paramValueMap.keySet();
		}
		return Collections.EMPTY_SET;
	}

	/***
	 * 将请求参数值转换为Map
	 * @return
	 */
	protected Map<String, Object> convertParams2Map(){
		Map<String, Object> result = new HashMap<>(8);
		if(request == null){
			return result;
		}
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()){
			String paramName = (String) paramNames.nextElement();
			String[] values = request.getParameterValues(paramName);
			if(V.notEmpty(values)){
				if(values.length == 1){
					if(V.notEmpty(values[0])){
						result.put(paramName, values[0]);
					}
				}
				else{
					// 多个值需传递到后台SQL的in语句
					result.put(paramName, values);
				}
			}
		}
		return result;
	}

	/**
	 * 自动转换为VO并绑定关联关系
	 *
	 * @param entityList
	 * @param voClass
	 * @param <VO>
	 * @return
	 */
	@Deprecated
	protected <VO> List<VO> convertToVoAndBindRelations(List entityList, Class<VO> voClass) {
		// 转换为VO
		List<VO> voList = Binder.convertAndBindRelations(entityList, voClass);
		return voList;
	}

	/**
	 * RelatedData获取相应对象数据
	 *
	 * @param relatedDataDTO
	 * @return labelValue集合
	 */
	protected List<LabelValue> loadRelatedData(RelatedDataDTO relatedDataDTO) {
		return loadRelatedData(relatedDataDTO, null, null);
	}

	/**
	 * 通用的RelatedData获取数据
	 *
	 * @param relatedDataDTO 相应的relatedDataDTO
	 * @param parentId       父级值
	 * @param keyword       搜索关键词
	 * @return labelValue集合
	 */
	protected List<LabelValue> loadRelatedData(RelatedDataDTO relatedDataDTO, @Nullable String parentId,  @Nullable String keyword) {
		V.securityCheck(relatedDataDTO.getType(), relatedDataDTO.getLabel(), relatedDataDTO.getExt(),
				relatedDataDTO.getOrderBy(), relatedDataDTO.getParent(), relatedDataDTO.getParentPath());
		if (!relatedDataSecurityCheck(relatedDataDTO)) {
			log.warn("relatedData安全检查不通过: {}", JSON.stringify(relatedDataDTO));
			return Collections.emptyList();
		}
		String entityClassName = relatedDataDTO.getTypeClassName();
		Class<?> entityClass = BindingCacheManager.getEntityClassBySimpleName(entityClassName);
		if (V.isEmpty(entityClass)) {
			throw new BusinessException("exception.business.baseController.loadRelatedData.nullEntityClass");
		}
		BaseService<?> baseService = ContextHolder.getBaseServiceByEntity(entityClass);
		if (baseService == null) {
			throw new BusinessException("exception.business.baseController.loadRelatedData.nullServiceClass");
		}
		PropInfo propInfoCache = BindingCacheManager.getPropInfoByClass(entityClass);
		Function<String, String> field2column = field -> {
			if (V.notEmpty(field)) {
				String column = propInfoCache.getColumnByField(field);
				if (V.notEmpty(column)) {
					return column;
				} else {
					throw new BusinessException("exception.business.baseController.loadRelatedData.noField");
				}
			}
			return null;
		};
		String label = field2column.apply(S.defaultIfBlank(relatedDataDTO.getLabel(), "label"));
		String idColumn = propInfoCache.getIdColumn();
		List<String> columns = new ArrayList<>();
		columns.add(label);
		columns.add(idColumn);
		if (V.notEmpty(relatedDataDTO.getExt())) {
			columns.add(field2column.apply(relatedDataDTO.getExt()));
		}
		// 构建查询条件
		QueryWrapper<?> queryWrapper = Wrappers.query();
		// 构建排序
        WrapperHelper.buildOrderBy(queryWrapper, relatedDataDTO.getOrderBy(), field2column);
		// tree
		if (V.notEmpty(relatedDataDTO.getParent())) {
			String parentColumn = field2column.apply(relatedDataDTO.getParent());
			Class<?> parentFieldType = propInfoCache.getFieldTypeByColumn(parentColumn);
			Function<String, Serializable> parentIdTypeConversion = pid -> V.isEmpty(pid) ? null : parentFieldType == Long.class ? Long.valueOf(pid) : pid;
			String parentPathColumn = field2column.apply(relatedDataDTO.getParentPath());
			// 动态根点 限制（需同时指定parentPath属性）
			Serializable rootId;
			if(V.notEmpty(relatedDataDTO.getConditions())) {
				Optional<BaseCriteria> parentCriteriaOpt = relatedDataDTO.getConditions().stream().filter(criteria -> criteria.getField().equals(relatedDataDTO.getParent())).findFirst();
				if(parentCriteriaOpt.isPresent()) {
					rootId = parentIdTypeConversion.apply(S.valueOf(parentCriteriaOpt.get().getValue()));
					relatedDataDTO.removeCondition(relatedDataDTO.getParent());
				}
				else {
                    rootId = null;
                }
            }
			else {
                rootId = null;
            }
            boolean isDynamicRoot = !parentIdTypeConversion.apply(Cons.TREE_ROOT_ID).equals(rootId) && V.isNoneEmpty(rootId, parentPathColumn);
			if (isDynamicRoot) {
				Object rootNode = baseService.getEntity(rootId);
				if (rootNode == null) {
					return Collections.emptyList();
				}
				String parentPath = S.valueOf(BeanUtils.getProperty(rootNode, relatedDataDTO.getParentPath()));
				queryWrapper.and(query -> {
					query.likeRight(parentPathColumn, V.isEmpty(parentPath) ? S.valueOf(rootId) : parentPath + Cons.SEPARATOR_COMMA + rootId);
					query.or().eq(idColumn, rootId);
				});
			}

			if (V.notEmpty(parentId)) {
				// 加载其节点相应下一层节点列表
				queryWrapper.eq(parentColumn, parentIdTypeConversion.apply(parentId));
			} else if (V.isNoneEmpty(keyword, relatedDataDTO.getParentPath())) {
				// tree 模糊搜索（未指定或无parentPath属性及不支持tree搜索）
				List<Map<String, Object>> mapList = baseService.getMapList(Wrappers.query().select(idColumn, parentPathColumn).like(label, keyword));
				if (V.isEmpty(mapList)) {
					return Collections.emptyList();
				}
				queryWrapper.in(idColumn, new HashSet<Serializable>() {{
					addAll(mapList.stream().peek(map -> add(parentIdTypeConversion.apply(S.valueOf(map.remove(idColumn)))))
							.map(map -> S.split(S.valueOf(map.get(parentPathColumn)))).flatMap(Stream::of)
							.filter(V::notEmpty).map(parentIdTypeConversion).collect(Collectors.toList()));
				}});
				columns.add(S.joinWith(" as ", parentColumn, Cons.FieldName.parentId.name()));
			} else if (relatedDataDTO.isLazyChild()) {
				if (isDynamicRoot) {
					queryWrapper.eq(idColumn, rootId);
				} else {
					// 懒加载tree的根节点列表
					Serializable treeRootId = getTreeRootId(entityClass, parentFieldType);
					if (treeRootId == null) {
						queryWrapper.isNull(parentColumn);
					} else {
						queryWrapper.eq(parentColumn, treeRootId);
					}
				}
			} else {
				// 加载整个Tree结构数据
				columns.add(S.joinWith(" as ", parentColumn, Cons.FieldName.parentId.name()));
			}
		} else {
			// list 模糊搜索
			queryWrapper.like(V.notEmpty(keyword), label, keyword);
		}
		// 构建附加条件
		buildRelatedDataCondition(relatedDataDTO, queryWrapper, field2column);
		// 获取数据并做相应填充
		List<LabelValue> labelValueList = baseService.getLabelValueList(queryWrapper.select(columns.toArray(new String[]{})));
		if (V.isEmpty(keyword) && labelValueList.size() > BaseConfig.getBatchSize()) {
			log.warn("relatedData: {} 数据量超过 {} 条, 建议采用远程搜索接口过滤数据", entityClassName, BaseConfig.getBatchSize());
		}
		return labelValueList;
	}

	/**
	 * 获取Tree结构数据根节点parentId
	 * （便于子类重写）
	 *
	 * @param entityClass     实体类型
	 * @param parentFieldType parentId属性类型
	 * @return 根节点parentId
	 */
	protected Serializable getTreeRootId(Class<?> entityClass, Class<?> parentFieldType) {
		return parentFieldType == Long.class ? 0L : Cons.TREE_ROOT_ID;
	}

    /**
     * relatedData 安全检查
     *
     * @param relatedDataDTO
     * @return 是否允许访问该类型接口
     */
    protected boolean relatedDataSecurityCheck(RelatedDataDTO relatedDataDTO) {
		return true;
    }

	/**
	 * 构建RelatedData的筛选条件
	 *
	 * @param relatedDataDTO
	 * @param queryWrapper
	 * @param field2column relatedData.type对象的属性名转列名
	 */
	protected void buildRelatedDataCondition(RelatedDataDTO relatedDataDTO, QueryWrapper<?> queryWrapper, Function<String, String> field2column) {
		if (V.isEmpty(relatedDataDTO.getConditions())) {
			return;
		}
		relatedDataDTO.getConditions().forEach(criteria -> {
			String columnName = field2column.apply(criteria.getField());
			// 根据匹配方式构建查询
			WrapperHelper.buildQueryCriteria(queryWrapper, criteria.getComparison(), columnName, criteria.getValue());
		});
	}

	/***
	 * 打印所有参数信息
	 */
	protected void dumpParams(){
		Map<String, String[]> params = request.getParameterMap();
		if(params != null && !params.isEmpty()){
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<String, String[]> entry : params.entrySet()){
				String[] values = entry.getValue();
				if(values != null && values.length > 0){
					sb.append(entry.getKey()).append("=").append(S.join(values)).append("; ");
				}
			}
			log.debug(sb.toString());
		}
	}

	/**
	 * 从request获取Long参数
	 * @param param
	 * @return
	 */
	protected Long getLong(String param){
		return S.toLong(request.getParameter(param));
	}

	/**
	 * 从request获取Long参数
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	protected long getLong(String param, Long defaultValue){
		return S.toLong(request.getParameter(param), defaultValue);
	}

	/**
	 * 从request获取Int参数
	 * @param param
	 * @return
	 */
	protected Integer getInteger(String param){
		return S.toInt(request.getParameter(param));
	}

	/**
	 * 从request获取Int参数
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	protected int getInt(String param, Integer defaultValue){
		return S.toInt(request.getParameter(param), defaultValue);
	}

	/***
	 * 从request中获取boolean值
	 * @param param
	 * @return
	 */
	protected boolean getBoolean(String param){
		return S.toBoolean(request.getParameter(param));
	}

	/***
	 * 从request中获取boolean值
	 * @param param
	 * @param defaultBoolean
	 * @return
	 */
	protected boolean getBoolean(String param, boolean defaultBoolean){
		return S.toBoolean(request.getParameter(param), defaultBoolean);
	}

	/**
	 * 从request获取Double参数
	 * @param param
	 * @return
	 */
	protected Double getDouble(String param){
		if(V.notEmpty(request.getParameter(param))){
			return Double.parseDouble(request.getParameter(param));
		}
		return null;
	}

	/**
	 * 从request获取Double参数
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	protected Double getDouble(String param, Double defaultValue){
		if(V.notEmpty(request.getParameter(param))){
			return Double.parseDouble(request.getParameter(param));
		}
		return defaultValue;
	}

	/**
	 * 从request获取String参数
	 * @param param
	 * @return
	 */
	protected String getString(String param){
		if(V.notEmpty(request.getParameter(param))){
			return request.getParameter(param);
		}
		return null;
	}

	/**
	 * 从request获取String参数
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	protected String getString(String param, String defaultValue){
		if(V.notEmpty(request.getParameter(param))){
			return request.getParameter(param);
		}
		return defaultValue;
	}

	/**
	 * 从request获取String[]参数
	 * @param param
	 * @return
	 */
	protected String[] getStringArray(String param){
		if(request.getParameterValues(param) != null){
			return request.getParameterValues(param);
		}
		return null;
	}

	/***
	 * 从request里获取String列表
	 * @param param
	 * @return
	 */
	protected List<String> getStringList(String param){
		String[] strArray = getStringArray(param);
		if(V.isEmpty(strArray)){
			return null;
		}
		return Arrays.asList(strArray);
	}

	/***
	 * 从request里获取Long列表
	 * @param param
	 * @return
	 */
	protected List<Long> getLongList(String param){
		String[] strArray = getStringArray(param);
		if(V.isEmpty(strArray)){
			return null;
		}
		List<Long> longList = new ArrayList<>();
		for(String str : strArray){
			if(V.notEmpty(str)){
				longList.add(Long.parseLong(str));
			}
		}
		return longList;
	}
}
