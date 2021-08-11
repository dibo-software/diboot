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
package com.diboot.core.util;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.vo.Status;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

/***
 * Validator校验类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 *
 */
public class V {
	private static final Logger log = LoggerFactory.getLogger(V.class);
	/**
	 * hibernate注解验证
	 */
	private static Validator VALIDATOR = null;

	/***
	 * 对象是否为空
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj){
		if(obj instanceof String){
			return isEmpty((String)obj);
		}
		else if(obj instanceof Collection){
			return isEmpty((Collection)obj);
		}
		else if(obj instanceof Map){
			return isEmpty((Map)obj);
		}
		else if(obj instanceof String[]){
			return isEmpty((String[])obj);
		}
		else{
			return obj == null;
		}
	}

	/***
	 * 字符串是否为空
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value){
		return S.isBlank(value);
	}
	
	/***
	 * 字符串数组是否不为空
	 * @param values
	 * @return
	 */
	public static boolean isEmpty(String[] values){
		return values == null || values.length == 0;
	}
	
	/***
	 * 集合为空
	 * @param list
	 * @return
	 */
	public static <T> boolean isEmpty(Collection<T> list) {
		return list == null || list.isEmpty();
	}
	
	/***
	 * Map为空
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Map obj){
		return obj == null || obj.isEmpty();
	}

	/***
	 * 对象是否为空
	 * @param obj
	 * @return
	 */
	public static boolean notEmpty(Object obj){
		if(obj instanceof String){
			return notEmpty((String)obj);
		}
		else if(obj instanceof Collection){
			return notEmpty((Collection)obj);
		}
		else if(obj instanceof Map){
			return notEmpty((Map)obj);
		}
		else if(obj instanceof String[]){
			return notEmpty((String[])obj);
		}
		else{
			return obj != null;
		}
	}

	/***
	 * 字符串是否不为空
	 * @param value
	 * @return
	 */
	public static boolean notEmpty(String value){
		return S.isNotBlank(value);
	}

	/***
	 * 字符串数组是否不为空
	 * @param values
	 * @return
	 */
	public static boolean notEmpty(String[] values){
		return values != null && values.length > 0;
	}

	/***
	 * 集合不为空
	 * @param list
	 * @return
	 */
	public static <T> boolean notEmpty(Collection<T> list) {
		return list != null && !list.isEmpty();
	}

	/***
	 * 对象不为空且不为0
	 * @param longObj
	 * @return
	 */
	public static boolean notEmptyOrZero(Long longObj){
		return longObj != null && longObj.longValue() != 0;
	}

	/***
	 * 对象不为空且不为0
	 * @param intObj
	 * @return
	 */
	public static boolean notEmptyOrZero(Integer intObj){
		return intObj != null && intObj.intValue() != 0;
	}
	
	/***
	 * Map为空
	 * @param obj
	 * @return
	 */
	public static boolean notEmpty(Map obj){
		return obj != null && !obj.isEmpty();
	}
	
	/** 
     * 判断是否为数字（允许小数点）
     * @param str 
     * @return true Or false 
     */  
    public static boolean isNumber(String str){
		String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
		return str.matches(regex);
    }

    /** 
     * 判断是否为正确的邮件格式 
     * @param str 
     * @return boolean 
     */  
    public static boolean isEmail(String str){  
        if(isEmpty(str)) {
			return false;
		}
        return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");  
    }  
      
    /** 
     * 判断字符串是否为电话号码
     * @param str 
     * @return boolean 
     */  
    public static boolean isPhone(String str){  
        if(isEmpty(str)){  
            return false;
        }
        boolean valid = str.matches("^1\\d{10}$");
        if(!valid){
        	valid = str.matches("^[0|4]\\d{2,3}-?\\d{7,8}$");
		}
        return valid;
    }

	/**
	 * 对参数值做安全检查
	 * @param paramValues
	 */
	public static void securityCheck(String... paramValues) {
		if(isEmpty(paramValues)){
			return;
		}
		for(String param : paramValues){
			if(V.isValidSqlParam(param) == false){
				throw new BusinessException(Status.FAIL_VALIDATION, "非法的参数: " + param);
			}
		}
	}

	/**
	 * 是否为合法的数据库列参数（orderBy等参数安全检查）
	 * @param columnStr
	 * @return
	 */
	private static Pattern pattern = Pattern.compile("^[A-Za-z_][\\w:]*$");
	public static boolean isValidSqlParam(String columnStr){
		if(isEmpty(columnStr)){
			return true;
		}
		return pattern.matcher(columnStr).matches();
	}

	/**
	 * 是否boolean值范围
	 */
	private static final Set<String> TRUE_SET = new HashSet(){{
		add("true"); add("是"); add("y"); add("yes"); add("1");
	}};
	private static final Set<String> FALSE_SET = new HashSet(){{
		add("false"); add("否"); add("n"); add("no"); add("0");
	}};

	/***
	 * 是否为boolean类型
	 * @param value
	 * @return
	 */
	public static boolean isValidBoolean(String value){
		if(value == null){
			return false;
		}
		value = S.trim(value).toLowerCase();
		return TRUE_SET.contains(value) || FALSE_SET.contains(value);
	}

	/***
	 * 转换为boolean类型, 并判定是否为true
	 * @param value
	 * @return
	 */
	public static boolean isTrue(String value){
		if(value == null){
			return false;
		}
		value = S.trim(value).toLowerCase();
		return TRUE_SET.contains(value);
	}

	/***
	 * 根据指定规则校验字符串的值是否合法
	 * @param value
	 * @param validation
	 * @return
	 */
	@Deprecated
    public static String validate(String value, String validation){
    	if(isEmpty(validation)){
    		return null;
		}
		List<String> errorMsgList = new ArrayList<>();
		String[] rules = validation.split(",");
    	for(String rule : rules){
			if ("NotNull".equalsIgnoreCase(rule)){
				if (isEmpty(value)) {
					errorMsgList.add("不能为空");
				}
			}
			else if ("Number".equalsIgnoreCase(rule)){
				if (!isNumber(value)) {
					errorMsgList.add("非数字格式");
				}
			}
			else if ("Boolean".equalsIgnoreCase(rule)){
				if (!isValidBoolean(value)) {
					errorMsgList.add("非Boolean格式");
				}
			}
			else if ("Date".equalsIgnoreCase(rule)){
				if (D.fuzzyConvert(value) == null) {
					errorMsgList.add("非日期格式");
				}
			}
			else if (rule.toLowerCase().startsWith("length")) {
				String range = rule.substring(rule.indexOf("(") + 1, rule.lastIndexOf(")"));
				if (range.contains("-")) {
					String[] arr = range.split("-");
					if (notEmpty(arr[0])) {
						if (V.isEmpty(value) || value.length() < Integer.parseInt(arr[0])) {
							errorMsgList.add("长度少于最小限制数: " + arr[0]);
						}
					}
					if (notEmpty(arr[1])) {
						if(V.notEmpty(value)){
							if (value.length() > Integer.parseInt(arr[1])) {
								errorMsgList.add("长度超出最大限制数: " + arr[1]);
							}
						}
					}
				}
				else {
					if (V.isEmpty(value) || !(value.length() == Integer.parseInt(range))) {
						errorMsgList.add("长度限制: " + range + "位");
					}
				}
			}
			else if ("Email".equalsIgnoreCase(rule)) {
				if (!isEmail(value)) {
					errorMsgList.add("非Email格式");
				}
			}
			else if ("Phone".equalsIgnoreCase(rule)) {
				if (!isPhone(value)) {
					errorMsgList.add("非电话号码格式");
				}
			}
			else{
				// 无法识别的格式
			}
		}
		// 返回校验不通过的结果
		if(errorMsgList.isEmpty()){
    		return null;
		}
		else{
			return S.join(errorMsgList);
		}
	}

	/***
	 * 判定两个对象是否不同类型或不同值
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean notEquals(Object source, Object target){
		return !equals(source, target);
	}

	/***
	 * 判定两个对象是否类型相同值相等
	 * @param source
	 * @param target
	 * @return
	 */
	public static <T> boolean equals(T source, T target){
		if(source == null && target == null){
			return true;
		}
		else if(source == null || target == null){
			return false;
		}
		// 不为空，调用equals比较
		else if(source instanceof Comparable){
			return (source).equals(target);
		}
		else if(source instanceof Collection){
    		Collection sourceList = (Collection)source, targetList = (Collection)target;
			// size不等
			if(sourceList.size() != targetList.size()){
    			return false;
			}
			for(Object obj : sourceList){
    			if(!targetList.contains(obj)){
    				return false;
				}
			}
			for(Object obj : targetList){
				if(!sourceList.contains(obj)){
					return false;
				}
			}
			return true;
		}
		else if(source instanceof Map){
			Map sourceMap = (Map)source, targetMap = (Map)target;
			if(V.isEmpty(sourceMap) && V.isEmpty(targetMap)){
				return true;
			}
			if(sourceMap.size() != targetMap.size()){
				return false;
			}
			for(Object key : sourceMap.keySet()){
				Object value = sourceMap.get(key);
				Object targetValue = targetMap.get(key);
				if(notEquals(value, targetValue)){
					return false;
				}
			}
			return true;
		}
		else{
			log.warn("暂未实现类型 "+ source.getClass().getSimpleName() + "-"+ target.getClass().getSimpleName() + " 的比对！");
			return false;
		}
	}

	/***
	 * 模糊对比是否相等（类型不同的转成String对比）
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean fuzzyEqual(Object source, Object target){
		if(equals(source, target)){
			return true;
		}
		// Boolean-String类型
		if(source instanceof Boolean && target instanceof String){
			return (boolean) source == V.isTrue((String)target);
		}
		if(target instanceof Boolean && source instanceof String){
			return (boolean) target == V.isTrue((String)source);
		}
		// Date-String类型
		else if((source instanceof Timestamp || source instanceof Date) && target instanceof String){
			return D.getDateTime((Date)source).equals(target) || D.getDate((Date)source).equals(target);
		}
		else if((target instanceof Timestamp || target instanceof Date) && source instanceof String){
			return D.getDateTime((Date)target).equals(source) || D.getDate((Date)target).equals(source);
		}
		else{
			return String.valueOf(source).equals(String.valueOf(target));
		}
	}

	/**
	 * 解析所有的验证错误信息，转换为JSON
	 * @param result
	 * @return
	 */
	public static String getBindingError(BindingResult result){
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

	/**
	 * 基于Bean中的validator注解校验
	 * @param obj
	 */
	public static <T> String validateBean(T obj) {
		if(VALIDATOR == null){
			VALIDATOR = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();
		}
		// 校验
		Set<ConstraintViolation<T>> errors = VALIDATOR.validate(obj);
		if(errors == null || errors.size() == 0){
			return null;
		}
		List<String> allErrors = new ArrayList<>(errors.size());
		for(ConstraintViolation<T> err : errors){
			allErrors.add(err.getMessage());
		}
		return S.join(allErrors);
	}

}