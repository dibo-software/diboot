package com.diboot.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/***
 * Controller的父类
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
public class BaseController {
	private static final Logger log = LoggerFactory.getLogger(BaseController.class);

	/***
	 * 字段
	 */
	protected static final String PARAM_FIELDS = "_fields";

	/**
	 * ID参数名
	 */
	protected static final String PARAM_ID = Cons.FieldName.id.name();

	/**
	 * 解析所有的验证错误信息，转换为JSON
	 * @param result
	 * @return
	 */
	protected String getBindingError(BindingResult result){
		if(result == null || !result.hasErrors()){
			return null;
		}
		List<ObjectError> errors = result.getAllErrors();
		List<String> allErrors = new ArrayList<>(errors.size());
		for(ObjectError error : errors){
			allErrors.add(error.getDefaultMessage().replaceAll("\"", "'"));
		}
		return S.join(allErrors);
	}

	/***
	 * 构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @param <T>
	 * @return
	 */
	public <T,DTO> QueryWrapper<T> buildQueryWrapper(DTO entityOrDto, HttpServletRequest request) throws Exception{
		if(entityOrDto instanceof HttpServletRequest){
			throw new Exception("参数错误：buildQueryWrapper()参数为Entity/DTO对象！");
		}

		return QueryBuilder.toQueryWrapper(entityOrDto, extractParams(request));
	}

	/***
	 * 构建查询LambdaQueryWrapper (根据BindQuery注解构建相应的查询条件)
	 * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
	 * @param <T>
	 * @return
	 */
	public <T,DTO> LambdaQueryWrapper<T> buildLambdaQueryWrapper(DTO entityOrDto, HttpServletRequest request) throws Exception{
		if(entityOrDto instanceof HttpServletRequest){
			throw new Exception("参数错误：buildQueryWrapper()参数为Entity/DTO对象！");
		}
		return QueryBuilder.toLambdaQueryWrapper(entityOrDto, extractParams(request));
	}

	/***
	 * 获取请求参数Map
	 * @param request
	 * @return
	 */
	public Map<String, Object> getParamsMap(HttpServletRequest request) throws Exception{
		return getParamsMap(request, null);
	}

	/***
	 * 获取请求参数Map
	 * @param request
	 * @return
	 */
	private Map<String, Object> getParamsMap(HttpServletRequest request, List<String> paramList) throws Exception{
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
	 * @param request
	 * @return
	 */
	protected static String getRequestMappingURI(HttpServletRequest request){
		String contextPath = request.getContextPath();
		if(V.notEmpty(contextPath)){
			return S.replace(request.getRequestURI(), contextPath, "");
		}
		return request.getRequestURI();
	}

	/**
	 * 提取请求参数名集合
	 * @param request
	 * @return
	 */
	private static Set<String> extractParams(HttpServletRequest request){
		Map<String, Object> paramValueMap = convertParams2Map(request);
		if(V.notEmpty(paramValueMap)){
			return paramValueMap.keySet();
		}
		return Collections.EMPTY_SET;
	}

	/***
	 * 将请求参数值转换为Map
	 * @param request
	 * @return
	 */
	public static Map<String, Object> convertParams2Map(HttpServletRequest request){
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
					result.put(paramName, values[0]);
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
	protected <VO> List<VO> convertToVoAndBindRelations(List entityList, Class<VO> voClass) {
		// 转换为VO
		List<VO> voList = RelationsBinder.convertAndBind(entityList, voClass);
		return voList;
	}

	/***
	 * 打印所有参数信息
	 * @param request
	 */
	protected static void dumpParams(HttpServletRequest request){
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
	 * @param request
	 * @param param
	 * @return
	 */
	public Long getLong(HttpServletRequest request, String param){
		return S.toLong(request.getParameter(param));
	}

	/**
	 * 从request获取Long参数
	 * @param request
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	public long getLong(HttpServletRequest request, String param, Long defaultValue){
		return S.toLong(request.getParameter(param), defaultValue);
	}
	
	/**
	 * 从request获取Int参数
	 * @param request
	 * @param param
	 * @return
	 */
	public Integer getInteger(HttpServletRequest request, String param){
		return S.toInt(request.getParameter(param));
	}

	/**
	 * 从request获取Int参数
	 * @param request
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	public int getInt(HttpServletRequest request, String param, Integer defaultValue){
		return S.toInt(request.getParameter(param), defaultValue);
	}

	/***
	 * 从request中获取boolean值
	 * @param request
	 * @param param
	 * @return
	 */
	public boolean getBoolean(HttpServletRequest request, String param){
		return S.toBoolean(request.getParameter(param));
	}

	/***
	 * 从request中获取boolean值
	 * @param request
	 * @param param
	 * @param defaultBoolean
	 * @return
	 */
	public boolean getBoolean(HttpServletRequest request, String param, boolean defaultBoolean){
		return S.toBoolean(request.getParameter(param), defaultBoolean);
	}

	/**
	 * 从request获取Double参数
	 * @param request
	 * @param param
	 * @return
	 */
	public Double getDouble(HttpServletRequest request, String param){
		if(V.notEmpty(request.getParameter(param))){
			return Double.parseDouble(request.getParameter(param));
		}
		return null;
	}

	/**
	 * 从request获取Double参数
	 * @param request
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	public Double getDouble(HttpServletRequest request, String param, Double defaultValue){
		if(V.notEmpty(request.getParameter(param))){
			return Double.parseDouble(request.getParameter(param));
		}
		return defaultValue;
	}

	/**
	 * 从request获取String参数
	 * @param request
	 * @param param
	 * @return
	 */
	public String getString(HttpServletRequest request, String param){
		if(V.notEmpty(request.getParameter(param))){
			return request.getParameter(param);
		}
		return null;
	}

	/**
	 * 从request获取String参数
	 * @param request
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	public String getString(HttpServletRequest request, String param, String defaultValue){
		if(V.notEmpty(request.getParameter(param))){
			return request.getParameter(param);
		}
		return defaultValue;
	}

	/**
	 * 从request获取String[]参数
	 * @param request
	 * @param param
	 * @return
	 */
	public String[] getStringArray(HttpServletRequest request, String param){
		if(request.getParameterValues(param) != null){
			return request.getParameterValues(param);
		}
		return null;
	}

	/***
	 * 从request里获取String列表
	 * @param request
	 * @param param
	 * @return
	 */
	public List<String> getStringList(HttpServletRequest request, String param){
		String[] strArray = getStringArray(request, param);
		if(V.isEmpty(strArray)){
			return null;
		}
		return Arrays.asList(strArray);
	}

	/***
	 * 从request里获取Long列表
	 * @param request
	 * @param param
	 * @return
	 */
	public List<Long> getLongList(HttpServletRequest request, String param){
		String[] strArray = getStringArray(request, param);
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