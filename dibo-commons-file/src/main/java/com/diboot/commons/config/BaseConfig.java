package com.diboot.commons.config;

import com.diboot.commons.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 系统默认配置
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
public class BaseConfig {
	private static final Logger log = LoggerFactory.getLogger(BaseConfig.class);

	/**
	 * 从默认的/指定的 Properties文件获取配置
	 * @param key
	 * @return
	 */
	public static String getProperty(String key, String... propertiesFileName){
		return PropertiesUtils.get(key, propertiesFileName);
	}

	/***
	 *  从默认的/指定的 Properties文件获取boolean值
	 * @param key
	 * @param propertiesFileName
	 * @return
	 */
	public static boolean isTrue(String key, String... propertiesFileName){
		return PropertiesUtils.getBoolean(key, propertiesFileName);
	}

	/***
	 * 获取int类型
	 * @param key
	 * @param propertiesFileName
	 * @return
	 */
	public static int getInteger(String key, String... propertiesFileName){
		return PropertiesUtils.getInteger(key, propertiesFileName);
	}

	/***
	 * 获取截取长度
	 * @return
	 */
	public static int getCutLength(){
		Integer length = PropertiesUtils.getInteger("system.default.cutLength");
		if(length != null){
			return length;
		}
		return 20;
	}

	/***
	 * 默认页数
	 * @return
	 */
    public static int getPageSize() {
		Integer length = PropertiesUtils.getInteger("pagination.default.pageSize");
		if(length != null){
			return length;
		}
		return 20;
    }

	/***
	 * 获取批量插入的每批次数量
 	 * @return
	 */
	public static int getBatchSize() {
		return 1000;
	}
}