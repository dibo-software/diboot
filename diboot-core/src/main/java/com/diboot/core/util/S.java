package com.diboot.core.util;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/***
 * String 操作类
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 */
public class S extends StringUtils{
	/***
	 * 默认分隔符 ,
 	 */
	public static final String SEPARATOR = ",";

	/***
	 * 裁剪字符串，显示前部分+...
	 * @param input
	 * @return
	 */
	public static String cut(String input){
		return cut(input, BaseConfig.getCutLength());
	}

	/***
	 * 裁剪字符串，显示前部分+...
	 * @param input
	 * @return
	 */
	public static String cut(String input, int cutLength){
		return substring(input, 0, cutLength);
	}

	/***
	 * 将list拼接成string，默认分隔符:,
	 * @param stringList
	 * @return
	 */
	public static String join(List<String> stringList){
		return StringUtils.join(stringList, SEPARATOR);
	}

	/***
	 * 将list拼接成string，默认分隔符:,
	 * @param stringArray
	 * @return
	 */
	public static String join(String[] stringArray){
		return StringUtils.join(stringArray, SEPARATOR);
	}

	/***
	 * 按,拆分字符串
	 * @param joinedStr
	 * @return
	 */
	public static String[] split(String joinedStr){
		if(joinedStr == null){
			return null;
		}
		return joinedStr.split(SEPARATOR);
	}

	/***
	 * 转换为String数组（避免转型异常）
	 * @param stringList
	 * @return
	 */
	public static String[] toStringArray(List<String> stringList){
		String[] array = new String[stringList.size()];
		for(int i=0; i<stringList.size(); i++){
			array[i] = stringList.get(i);
		}
		return array;
	}

	/***
	 * 转换成蛇形命名（用于Java属性转换为数据库列名）
	 * @param camelCaseStrArray
	 * @return
	 */
	public static String[] toSnakeCase(String[] camelCaseStrArray){
		if(camelCaseStrArray == null){
			return null;
		}
		String[] snakeCaseArray = new String[camelCaseStrArray.length];
		for(int i=0; i<camelCaseStrArray.length; i++){
			snakeCaseArray[i] = toSnakeCase(camelCaseStrArray[i]);
		}
		return snakeCaseArray;
	}

	/***
	 * 转换成蛇形命名（用于Java属性转换为数据库列名）
	 * @param camelCaseStrArray
	 * @return
	 */
	public static List<String> toSnakeCase(List<String> camelCaseStrArray){
		if(camelCaseStrArray == null){
			return null;
		}
		List<String> snakeCaseList = new ArrayList<>(camelCaseStrArray.size());
		for(String camelCaseStr : camelCaseStrArray){
			snakeCaseList.add(toSnakeCase(camelCaseStr));
		}
		return snakeCaseList;
	}

	/***
	 * 转换成蛇形命名（用于Java属性转换为数据库列名）
	 * @param camelCaseStr
	 * @return
	 */
	public static String toSnakeCase(String camelCaseStr){
		if(V.isEmpty(camelCaseStr)){
			return null;
		}
		char[] chars = camelCaseStr.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (char c : chars){
			if(Character.isUpperCase(c)){
				if(sb.length() > 0){
					sb.append(Cons.SEPARATOR_UNDERSCORE);
				}
				sb.append(Character.toLowerCase(c));
			}
			else{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/***
	 * 转换成首字母小写的驼峰命名（用于数据库列名转换为Java属性）
	 * @param snakeCaseStr
	 * @return
	 */
	public static String toLowerCaseCamel(String snakeCaseStr){
		if(V.isEmpty(snakeCaseStr)){
			return null;
		}
		// 不包含_，直接return
		if(!snakeCaseStr.contains(Cons.SEPARATOR_UNDERSCORE)){
			return snakeCaseStr;
		}
		char[] chars = snakeCaseStr.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean upperCase = false;
		for (char c : chars){
			if(Cons.SEPARATOR_UNDERSCORE.equals(Character.toString(c))){
				upperCase = true;
				continue;
			}
			if(upperCase){
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			}
			else{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/***
	 * 转换为Long类型（判空，避免NPE）
	 * @param strValue
	 * @return
	 */
	public static Long toLong(String strValue){
		return toLong(strValue, null);
	}

	/***
	 * 转换为Long类型（判空，避免NPE）
	 * @param strValue 字符类型值
	 * @param defaultLong 默认值
	 * @return
	 */
	public static Long toLong(String strValue, Long defaultLong){
		if(V.isEmpty(strValue)){
			return defaultLong;
		}
		return Long.parseLong(strValue);
	}

	/***
	 * 转换为Integer类型(判空，避免NPE)
	 * @param strValue
	 * @return
	 */
	public static Integer toInt(String strValue){
		return toInt(strValue, null);
	}

	/***
	 * 转换为Integer类型(判空，避免NPE)
	 * @param strValue
	 * @param defaultInt 默认值
	 * @return
	 */
	public static Integer toInt(String strValue, Integer defaultInt){
		if(V.isEmpty(strValue)){
			return defaultInt;
		}
		return Integer.parseInt(strValue);
	}

	/***
	 * 字符串转换为boolean
	 * @param strValue
	 * @return
	 */
	public static boolean toBoolean(String strValue){
		return toBoolean(strValue, false);
	}

	/***
	 * 字符串转换为boolean
	 * @param strValue
	 * @param defaultBoolean
	 * @return
	 */
	public static boolean toBoolean(String strValue, boolean defaultBoolean){
		if(V.notEmpty(strValue)){
			return V.isTrue(strValue);
		}
		return defaultBoolean;
	}

	/***
	 * 将多个空格替换为一个
	 * @param input
	 * @return
	 */
	public static String removeDuplicateBlank(String input){
		if(V.isEmpty(input)){
			return input;
		}
		return input.trim().replaceAll(" +", " ");
	}

    /**
     * 获得随机串
     * @return
     */
    public static String newUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

	/***
	 * 生成指定位数的数字/验证码
	 */
	private static final String NUMBER_SET = "12345678901";
	private static Random random = new Random();
    public static String newRandomNum(int length){
		StringBuilder sb = new StringBuilder();
		sb.append(NUMBER_SET.charAt(random.nextInt(9)));
    	for(int i=1; i<length; i++){
    		sb.append(NUMBER_SET.charAt(random.nextInt(10)));
		}
		return sb.toString();
	}

	/***
	 * 将首字母转为小写
	 * @return
	 */
	public static String uncapFirst(String input){
		if(input != null){
			return String.valueOf(input.charAt(0)).toLowerCase() + input.substring(1);
		}
		return null;
	}

	/***
	 * 将首字母转为大写
	 * @return
	 */
	public static String capFirst(String input){
		if(input != null){
			return String.valueOf(input.charAt(0)).toUpperCase() + input.substring(1);
		}
		return null;
	}

}
