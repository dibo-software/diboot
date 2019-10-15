package com.diboot.commons.utils;

import com.diboot.commons.config.BaseConfig;
import com.diboot.commons.config.BaseCons;
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
		return stringList.toArray(new String[stringList.size()]);
	}

    /**
     * 获得随机串
     * @return
     */
    public static String newUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
