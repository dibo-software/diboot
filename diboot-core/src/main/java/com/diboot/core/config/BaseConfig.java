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
package com.diboot.core.config;

import com.diboot.core.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 系统默认配置
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
public class BaseConfig {
	private static final Logger log = LoggerFactory.getLogger(BaseConfig.class);

	/**
	 * 从当前配置文件获取配置参数值
	 * @param key
	 * @return
	 */
	public static String getProperty(String key){
		return PropertiesUtils.get(key);
	}

	/**
	 * 从当前配置文件获取配置参数值
	 * @param key
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getProperty(String key, String defaultValue){
		String value = PropertiesUtils.get(key);
		return value != null? value : defaultValue;
	}

	/***
	 *  从默认的/指定的 Properties文件获取boolean值
	 * @param key
	 * @return
	 */
	public static boolean isTrue(String key){
		return PropertiesUtils.getBoolean(key);
	}

	/***
	 * 获取int类型
	 * @param key
	 * @return
	 */
	public static Integer getInteger(String key){
		return PropertiesUtils.getInteger(key);
	}

	/***
	 * 获取int类型
	 * @param key
	 * @return
	 */
	public static Integer getInteger(String key, int defaultValue){
		Integer value = PropertiesUtils.getInteger(key);
		return value != null? value : defaultValue;
	}

	private static Integer cutLength = null;
	/***
	 * 获取截取长度
	 * @return
	 */
	public static int getCutLength(){
		if(cutLength == null){
			cutLength = PropertiesUtils.getInteger("diboot.core.cut-length");
			if(cutLength == null){
				cutLength = 20;
			}
		}
		return cutLength;
	}

	private static Integer pageSize = null;
	/***
	 * 默认页数
	 * @return
	 */
    public static int getPageSize() {
		if(pageSize == null){
			pageSize = PropertiesUtils.getInteger("diboot.core.page-size");
			if(pageSize == null){
				pageSize = 20;
			}
		}
		return pageSize;
    }

	private static Integer batchSize = null;
	/***
	 * 获取批量插入的每批次数量
 	 * @return
	 */
	public static int getBatchSize() {
		if(batchSize == null){
			batchSize = PropertiesUtils.getInteger("diboot.core.batch-size");
			if(batchSize == null){
				batchSize = 1000;
			}
		}
		return batchSize;
	}

	private static Object ACTIVE_FLAG_VALUE = null;
	/**
	 * 获取有效记录的标记值，如 0
	 * @return
	 */
	public static Object getActiveFlagValue(){
		if(ACTIVE_FLAG_VALUE == null){
			String activeFlagValue = getProperty("mybatis-plus.global-config.db-config.logic-not-delete-value", "0");
			switch (activeFlagValue) {
				case "false":
					ACTIVE_FLAG_VALUE = false;
					break;
				case "0":
					ACTIVE_FLAG_VALUE = 0;
					break;
				default:
					ACTIVE_FLAG_VALUE = activeFlagValue;
			}
		}
		return ACTIVE_FLAG_VALUE;
	}

	public static void setActiveFlagValue(String value) {
		if(getActiveFlagValue() == null) {
			ACTIVE_FLAG_VALUE = value;
		}
	}
}