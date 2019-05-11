package com.diboot.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.config.Cons;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/***
 * Controller的父类
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
@Controller
public class BaseController {
	private static final Logger log = LoggerFactory.getLogger(BaseController.class);

	/***
	 * 分页参数列表
	 */
	protected static final List<String> PARAM_PAGES = Arrays.asList("_pageIndex", "_pageSize", "_totalCount", "_orderBy");
	/***
	 * 字段
	 */
	protected static final String PARAM_FIELDS = "_fields";

	/**
	 * ID参数名
	 */
	protected static final String PARAM_ID = Cons.FieldName.id.name();

	/**
	 * 错误关键字
	 */
	protected static final String ERROR = "error";
	
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
	 * 构建查询wrapper
	 * @param request
	 * @param <T>
	 * @return
	 */
	public <T extends BaseEntity> QueryWrapper<T> buildQuery(HttpServletRequest request) throws Exception{
		if(!RequestMethod.GET.name().equalsIgnoreCase(request.getMethod())){
			log.warn("调用错误: 非GET请求，无需构建查询条件！");
			return null;
		}
		//TODO 是否需要先拿到Entity定义的属性列表，只映射该列表中的属性?
		QueryWrapper query = new QueryWrapper<T>();
		Map<String, Object> requestMap = getParamsMap(request);
		if(V.notEmpty(requestMap)){
			if(requestMap.containsKey(PARAM_FIELDS) && V.notEmpty(requestMap.get(PARAM_FIELDS))){
				if(requestMap.get(PARAM_FIELDS) instanceof String){
					String fields = (String) requestMap.get(PARAM_FIELDS);
					query.select(fields);
				}
			}
			for(Map.Entry<String, Object> entry : requestMap.entrySet()){
				Object value = entry.getValue();
				if(!entry.getKey().startsWith("_") && value != null){
					if(value instanceof Set || value instanceof List || value.getClass().isArray()){
						query.in(S.toSnakeCase(entry.getKey()), value);
					}
					else if(value instanceof String){
						query.eq(S.toSnakeCase(entry.getKey()), value);
					}
				}
			}
		}
		return query;
	}

	/***
	 * 构建分页对象
	 * @param request
	 * @return
	 */
	protected Pagination buildPagination(HttpServletRequest request) throws Exception{
		return buildPagination(request, true);
	}

	/***
	 * 构建分页对象
	 * @param request
	 * @return
	 */
	protected Pagination buildPagination(HttpServletRequest request, boolean newInstanceIfNull) throws Exception{
		Pagination page = newInstanceIfNull? new Pagination() : null;
		Map<String, Object> pageParamMap = getParamsMap(request, PARAM_PAGES);
		if(V.notEmpty(pageParamMap)){
			if(page == null){
				page = new Pagination();
			}
			BeanUtils.bindProperties(page, pageParamMap);
		}
		return page;
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

	/***
	 * 返回json格式错误信息
	 * @param response
	 * @param jsonResult
	 */
	protected static void responseJson(HttpServletResponse response, JsonResult jsonResult){
		// 处理异步请求
		PrintWriter pw = null;
		try {
			response.setStatus(HttpStatus.OK.value());
			response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
			pw = response.getWriter();
			pw.write(JSON.stringify(jsonResult));
			pw.flush();
		}
		catch (IOException e) {
			log.error("处理异步请求异常", e);
		}
		finally {
			if (pw != null) {
				pw.close();
			}
		}
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

	/***
	 * 将请求参数值绑定成Model
	 * @param request
	 */
	public static void buildEntity(BaseEntity entity, HttpServletRequest request){
		Map<String, Object> propMap = convertParams2Map(request);
		BeanUtils.bindProperties(entity, propMap);
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