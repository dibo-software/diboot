package com.diboot.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

/***
 * Validator校验类
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 *
 */
public class V {
	private static final Logger log = LoggerFactory.getLogger(V.class);

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
	 * Map为空
	 * @param obj
	 * @return
	 */
	public static boolean notEmpty(Map obj){
		return obj != null && !obj.isEmpty();
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

}