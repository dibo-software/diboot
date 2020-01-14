package com.diboot.core.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/***
 * JSON操作辅助类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
public class JSON extends JSONObject{
	private static final Logger log = LoggerFactory.getLogger(JSON.class);

	/**
	 * 序列化配置
	 */
	private static SerializeConfig serializeConfig = new SerializeConfig();
	static {
		serializeConfig.put(Date.class, new SimpleDateFormatSerializer(D.FORMAT_DATETIME_Y4MDHM));
	}

	/**
	 * 将Java对象转换为Json String
	 * @param object
	 * @return
	 */
	public static String stringify(Object object){
		return toJSONString(object, serializeConfig);
	}

	/***
	 * 将JSON字符串转换为java对象
	 * @param jsonStr
	 * @return
	 */
	public static Map toMap(String jsonStr){
		return parseObject(jsonStr);
	}

	/***
	 * 将JSON字符串转换为java对象
	 * @param jsonStr
	 * @return
	 */
	public static LinkedHashMap toLinkedHashMap(String jsonStr){
		if(V.isEmpty(jsonStr)){
			return null;
		}
		return toJavaObject(jsonStr, LinkedHashMap.class);
	}

	/***
	 * 将JSON字符串转换为java对象
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	public static <T> T toJavaObject(String jsonStr, Class<T> clazz){
		return JSONObject.parseObject(jsonStr, clazz);
	}

}