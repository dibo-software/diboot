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
import com.diboot.core.binding.parser.PropInfo;
import com.diboot.core.config.Cons;
import com.diboot.core.dto.AttachMoreDTO;
import com.diboot.core.dto.CascaderDTO;
import com.diboot.core.entity.ValidList;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.CascaderService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

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

	/**
	 * 字典service
	 */
	@Autowired(required = false)
	protected DictionaryService dictionaryService;

	/**
	 * 级联service实现类
	 */
	@Autowired(required = false)
	private Map<String, CascaderService> cascaderServiceMap;

	/***
	 * 构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @see #buildQueryWrapperByDTO #buildQueryWrapperByQueryParams
	 * @return
	 */
	@Deprecated
    protected <DTO> QueryWrapper<DTO> buildQueryWrapper(DTO entityOrDto) throws Exception{
		return buildQueryWrapperByQueryParams(entityOrDto);
	}

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
	 * 通用的attachMore接口，附加更多下拉选型数据
	 *
	 * @param attachMoreDTOList
	 * @return
	 */
	protected Map<String, List<LabelValue>> attachMoreRelatedData(ValidList<AttachMoreDTO> attachMoreDTOList) {
		if (V.isEmpty(attachMoreDTOList)) {
			return Collections.emptyMap();
		}
		Map<String, List<LabelValue>> result = new HashMap<>(attachMoreDTOList.size());
		for (AttachMoreDTO attachMoreDTO : attachMoreDTOList) {
			// 请求参数安全检查
			V.securityCheck(attachMoreDTO.getTarget(), attachMoreDTO.getValue(), attachMoreDTO.getLabel(), attachMoreDTO.getExt());
			result.computeIfAbsent(S.toLowerCaseCamel(attachMoreDTO.getTarget()) + "Options", key -> V.isEmpty(attachMoreDTO.getLabel()) ?
					dictionaryService.getLabelValueList(attachMoreDTO.getTarget()) : attachMoreRelatedData(attachMoreDTO));
		}
		return result;
	}

	/**
	 * 通用的attachMore过滤接口
	 *
	 * @param attachMoreDTO
	 * @return labelValue列表
	 */
    protected List<LabelValue> attachMoreRelatedData(AttachMoreDTO attachMoreDTO) {
        return attachMoreRelatedData(attachMoreDTO, (queryWrapper, columnByField) -> {
            if (attachMoreDTO.getAdditional() == null) {
                return;
            }
            for (Map.Entry<String, Object> item : attachMoreDTO.getAdditional().entrySet()) {
                String columnName = columnByField.apply(item.getKey(), null);
                if (V.isEmpty(columnName)) {
                    throw new BusinessException("attachMore: " + attachMoreDTO.getTarget() + " 无 " + item.getKey() + " 属性");
                }
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
        });
    }

    /**
     * 通用的attachMore过滤接口
     *
     * @param attachMoreDTO
     * @param custom        自定义条件
     * @return labelValue列表
     */
	protected List<LabelValue> attachMoreRelatedData(AttachMoreDTO attachMoreDTO,
													 BiConsumer<QueryWrapper<?>,BiFunction<String, String, String>> custom) {
		String entityClassName = S.capFirst(S.toLowerCaseCamel(attachMoreDTO.getTarget()));
		Class<?> entityClass = BindingCacheManager.getEntityClassBySimpleName(entityClassName);
		if (V.isEmpty(entityClass)) {
			throw new BusinessException("attachMore: " + attachMoreDTO.getTarget() + " 不存在");
		}
		BaseService<?> baseService = ContextHelper.getBaseServiceByEntity(entityClass);
		if (baseService == null) {
			throw new BusinessException("attachMore: " + attachMoreDTO.getTarget() + " 的Service不存在 ");
		}
		PropInfo propInfoCache = BindingCacheManager.getPropInfoByClass(entityClass);
		BiFunction<String, String, String> columnByField = (field, defValue) -> {
			if (V.notEmpty(field)) {
				String column = propInfoCache.getColumnByField(field);
				return V.notEmpty(column) ? column : defValue;
			}
			return defValue;
		};
		String label = columnByField.apply(attachMoreDTO.getLabel(), null);
		if (V.isEmpty(label)) {
			throw new BusinessException("attachMore: " + attachMoreDTO.getTarget() + " 的label属性 " + attachMoreDTO.getLabel() + " 不存在");
		}
		String value = columnByField.apply(attachMoreDTO.getValue(), propInfoCache.getIdColumn());
		String ext = columnByField.apply(attachMoreDTO.getExt(), null);
		// 构建前端下拉框的初始化数据
		QueryWrapper<?> queryWrapper = Wrappers.query()
				.select(V.isEmpty(ext) ? new String[]{label, value} : new String[]{label, value, ext})
				.like(V.notEmpty(attachMoreDTO.getKeyword()), label, attachMoreDTO.getKeyword());
		if (custom != null) {
			custom.accept(queryWrapper, columnByField);
		}
		return baseService.getLabelValueList(queryWrapper);
	}

	/**
	 * 级联目标数据获取
	 * @param cascaderDTO
	 * @return
	 * @throws Exception
	 */
	public Map<String, List<LabelValue>> cascaderTargetList(CascaderDTO cascaderDTO) throws Exception {
		Map<String, List<LabelValue>> result = new HashMap<>(16);
		if (V.isEmpty(cascaderServiceMap)) {
			log.error("未实现CascaderService定义，无法获取级联数据！");
			return Collections.emptyMap();
		}
		for (String entityName : cascaderDTO.getTargetList()) {
			String entityClassName = S.uncapFirst(entityName);
			CascaderService cascaderService = cascaderServiceMap.get(entityClassName + "ServiceImpl");
			if(cascaderService == null){
				log.error("请检查实体类型{} 是否实现CascaderService定义", entityName);
				continue;
			}
			// 构建前端下拉框的初始化数据
			result.put(entityClassName + "Options", cascaderService.getCascaderLabelValue(cascaderDTO));
		}
		return result;
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
