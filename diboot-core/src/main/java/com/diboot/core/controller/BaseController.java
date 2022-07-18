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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.helper.WrapperHelper;
import com.diboot.core.binding.parser.PropInfo;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.dto.RelatedDataDTO;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;

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
	 * 根据请求参数构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件，url中的请求参数参与构建)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @return
	 */
    protected <DTO> QueryWrapper<DTO> buildQueryWrapperByQueryParams(DTO entityOrDto) throws Exception{
		return QueryBuilder.toQueryWrapper(entityOrDto, extractQueryParams());
	}

	/***
	 * 构建查询LambdaQueryWrapper (根据BindQuery注解构建相应的查询条件)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @see #buildLambdaQueryWrapperByDTO #buildLambdaQueryWrapperByQueryParams
	 * @return
	 */
	@Deprecated
    protected <DTO> LambdaQueryWrapper<DTO> buildLambdaQueryWrapper(DTO entityOrDto) throws Exception{
		return buildLambdaQueryWrapperByQueryParams(entityOrDto);
	}

	/***
	 * 根据DTO构建查询LambdaQueryWrapper (根据BindQuery注解构建相应的查询条件，DTO中的非空属性均参与构建)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @return
	 */
    protected <DTO> LambdaQueryWrapper<DTO> buildLambdaQueryWrapperByDTO(DTO entityOrDto) throws Exception{
		return QueryBuilder.toLambdaQueryWrapper(entityOrDto);
	}

	/***
	 * 根据请求参数构建查询LambdaQueryWrapper (根据BindQuery注解构建相应的查询条件，url中的请求参数参与构建)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @return
	 */
    protected <DTO> LambdaQueryWrapper<DTO> buildLambdaQueryWrapperByQueryParams(DTO entityOrDto) throws Exception{
		return QueryBuilder.toLambdaQueryWrapper(entityOrDto, extractQueryParams());
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
		if (!relatedDataDTO.isTree()) {
			relatedDataDTO.setParent(null);
		}
		return loadRelatedData(relatedDataDTO, null, null, null);
	}

	/**
	 * 获取RelatedData指定层级的数据集合
	 * <p>
	 * 用于异步加树形数据
	 *
	 * @param relatedDataDTO
	 * @param keyword       模糊搜索关键字
	 * @param parentValue   父级值
	 * @param parentType    父级类型
	 * @return labelValue集合
	 */
	protected List<LabelValue> loadRelatedData(RelatedDataDTO relatedDataDTO, String keyword,
											   String parentValue, String parentType) {
		if (V.notEmpty(parentType)) {
			relatedDataDTO = findRelatedDataByType(relatedDataDTO, parentType);
			if (relatedDataDTO != null && !relatedDataDTO.isTree()) {
				relatedDataDTO = relatedDataDTO.getNext().setTree(false);
			}
		}
		if (relatedDataDTO == null) {
			return Collections.emptyList();
		}
		List<LabelValue> labelValueList = loadRelatedData(relatedDataDTO, parentValue, keyword);
		if (relatedDataDTO.isTree() && relatedDataDTO.getNext() != null) {
			labelValueList.addAll(loadRelatedData(relatedDataDTO.getNext().setTree(false), parentValue, keyword));
		}
		return labelValueList;
	}

	/**
	 * 根据type查找 RelatedDataDTO
	 *
	 * @param relatedData
	 * @param type        类型
	 * @return RelatedDataDTO
	 */
	private RelatedDataDTO findRelatedDataByType(RelatedDataDTO relatedData, String type) {
		return relatedData.getType().equals(type) ? relatedData
				: (relatedData.getNext() == null ? null : findRelatedDataByType(relatedData.getNext(), type));
	}

	/**
	 * 通用的RelatedData获取数据
	 *
	 * @param relatedDataDTO  相应的relatedDataDTO
	 * @param parentValue 父级值
	 * @return labelValue集合
	 */
	private List<LabelValue> loadRelatedData(RelatedDataDTO relatedDataDTO, String parentValue, String keyword) {
		V.securityCheck(relatedDataDTO.getType(), relatedDataDTO.getValue(), relatedDataDTO.getLabel(),
				relatedDataDTO.getExt(), parentValue, keyword);
		if (!relatedDataSecurityCheck(relatedDataDTO)) {
			log.warn("relatedData安全检查不通过: {}", JSON.stringify(relatedDataDTO));
			return Collections.emptyList();
		}
		if (V.notEmpty(parentValue) && !relatedDataDTO.isTree() && V.isEmpty(relatedDataDTO.getParent())) {
			throw new BusinessException("relatedData跨表级联中的 " + relatedDataDTO.getType() + " 未指定关联父级属性 parent: ?");
		}
		String entityClassName = relatedDataDTO.getTypeClassName();
		Class<?> entityClass = BindingCacheManager.getEntityClassBySimpleName(entityClassName);
		if (V.isEmpty(entityClass)) {
			throw new BusinessException("relatedData: " + relatedDataDTO.getType() + " 不存在");
		}
		BaseService<?> baseService = ContextHelper.getBaseServiceByEntity(entityClass);
		if (baseService == null) {
			throw new BusinessException("relatedData: " + relatedDataDTO.getType() + " 的Service不存在 ");
		}
		PropInfo propInfoCache = BindingCacheManager.getPropInfoByClass(entityClass);
		Function<String, String> field2column = field -> {
			if (V.notEmpty(field)) {
				String column = propInfoCache.getColumnByField(field);
				if (V.notEmpty(column)) {
					return column;
				} else {
					throw new BusinessException("relatedData: " + relatedDataDTO.getType() + " 无 `" + field + "` 属性");
				}
			}
			return null;
		};
		String label = field2column.apply(S.defaultIfBlank(relatedDataDTO.getLabel(), "label"));
		String value = S.defaultString(field2column.apply(relatedDataDTO.getValue()), propInfoCache.getIdColumn());
		String ext = field2column.apply(relatedDataDTO.getExt());

		// 构建查询条件
		QueryWrapper<?> queryWrapper = Wrappers.query()
				.select(V.isEmpty(ext) ? new String[]{label, value} : new String[]{label, value, ext})
				.like(V.notEmpty(keyword), label, keyword);
		// 构建排序
        WrapperHelper.buildOrderBy(queryWrapper, relatedDataDTO.getOrderBy(), field2column);
		// 父级限制
		String parentColumn = field2column.apply(S.defaultIfBlank(relatedDataDTO.getParent(), relatedDataDTO.isTree() ? "parentId" : null));
		if (V.notEmpty(parentColumn)) {
			if (V.notEmpty(parentValue)) {
				queryWrapper.eq(parentColumn, parentValue);
			} else {
				queryWrapper.and(e -> e.isNull(parentColumn).or().eq(parentColumn, 0));
			}
		}
		// 构建附加条件
		buildRelatedDataCondition(relatedDataDTO, queryWrapper, field2column);
		// 获取数据并做相应填充
		List<LabelValue> labelValueList = baseService.getLabelValueList(queryWrapper);
		if (V.isEmpty(keyword) && labelValueList.size() > BaseConfig.getBatchSize()) {
			log.warn("relatedData: {} 数据量超过 {} 条, 建议采用远程搜索接口过滤数据", entityClassName, BaseConfig.getBatchSize());
		}
		if (V.notEmpty(parentColumn) || relatedDataDTO.getNext() != null) {
			Boolean leaf = !relatedDataDTO.isTree() && relatedDataDTO.getNext() == null ? true : null;
			// 第一层tree与最后一层无需返回type
			String type = relatedDataDTO.isTree() || Boolean.TRUE.equals(leaf) ? null : relatedDataDTO.getType();
			labelValueList.forEach(item -> {
				item.setType(type);
				item.setLeaf(leaf);
				// 非异步加载
				if (!Boolean.TRUE.equals(leaf) && !relatedDataDTO.isLazy()) {
					List<LabelValue> children = loadRelatedData(relatedDataDTO, keyword, S.valueOf(item.getValue()), type);
					item.setChildren(children.isEmpty() ? null : children);
					item.setDisabled(children.isEmpty() && relatedDataDTO.getNext() != null ? true : null);
				}
			});
		}
		return labelValueList;
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
		if (relatedDataDTO.getCondition() == null) {
			return;
		}
		for (Map.Entry<String, Object> item : relatedDataDTO.getCondition().entrySet()) {
			String columnName = field2column.apply(item.getKey());
			Object itemValue = item.getValue();
			if (itemValue == null) {
				queryWrapper.isNull(columnName);
			} else {
				if (itemValue instanceof Collection) {
					queryWrapper.in(columnName, (Collection<?>) itemValue);
				} else {
					queryWrapper.eq(columnName, itemValue);
				}
			}
		}
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
					sb.append(entry.getKey() + "=" + S.join(values)+"; ");
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
