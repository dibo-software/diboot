package com.diboot.core.util;

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
		return obj == null;
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
		return obj != null;
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
    public static boolean isNumeric(String str){
          return S.isNumeric(str);
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
        	valid = str.matches("^0\\d{2,3}-?\\d{7,8}$");
		}
        return valid;
    }  
      
    /** 
     * 判断是否为整型数字
     * @param str 
     * @return 
     */  
    public static boolean isNumber(String str) {
        try{
        	S.isNumeric(str);
            Integer.parseInt(str);
            return true;
        }
        catch(Exception ex){  
            return false;  
        }
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
						if ((value.length() < Integer.parseInt(arr[0]))) {
							errorMsgList.add("长度少于最小限制数: " + arr[0]);
						}
					}
					if (notEmpty(arr[1])) {
						if (value.length() > Integer.parseInt(arr[1])) {
							errorMsgList.add("长度超出最大限制数: " + arr[1]);
						}
					}
				}
				else {
					if (!(value.length() == Integer.parseInt(range))) {
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
				//TODO 无法识别的格式
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
	public static boolean notEqual(Object source, Object target){
		return !equal(source, target);
	}

	/***
	 * 判定两个对象是否类型相同值相等
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean equal(Object source, Object target){
		// 都为空
		if(source == null && target == null){
			return true;
		}// 一个为空一个非空
		else if(source == null || target == null){
			return false;
		}
    	else if((source instanceof String && target instanceof String)
			|| (source instanceof Long && target instanceof Long)
			|| (source instanceof Integer && target instanceof Integer)
			|| (source instanceof Float && target instanceof Float)
			|| (source instanceof Double && target instanceof Double)
		){
    		return (source).equals(target);
		}
		else if(source instanceof List && target instanceof List){
    		List sourceList = (List)source, targetList = (List)target;
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
		// 其他类型不一致
		else if(!source.getClass().getName().equals(target.getClass().getName())){
			return false;
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
		if(equal(source, target)){
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