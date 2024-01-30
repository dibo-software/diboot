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

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * 配置文件工具类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
@Slf4j
public class PropertiesUtils {

    private static Environment environment;

    public static void setEnvironment(Environment env) {
        if(environment == null) {
            environment = env;
        }
    }

    /***
     *  读取配置项的值
     * @param key
     * @return
     */
    public static String get(String key, String defaultValue){
        String value = get(key);
        if(value != null) {
            return value;
        }
        return defaultValue;
    }

    /***
     *  读取配置项的值
     * @param key
     * @return
     */
    public static String get(String key){
        if(environment == null){
            try{
                environment = ContextHolder.getApplicationContext().getEnvironment();
                log.trace("通过Context获取Environment");
            }
            catch (Exception e){
                log.warn("无法获取Environment，参数配置可能不生效");
            }
        }
        // 获取配置值
        if(environment == null){
            log.warn("无法获取上下文Environment，请在Spring初始化之后调用!");
            return null;
        }
        String value = environment.getProperty(key);
        // 任何password相关的参数需解密
        boolean isSensitiveConfig = key.contains(".password") || key.contains(".secret");
        if(value != null && isSensitiveConfig){
            value = Encryptor.decrypt(value);
        }
        return value;
    }

    /***
     *  读取int型的配置项
     * @param key
     * @return
     */
    public static Integer getInteger(String key){
        return getInteger(key, null);
    }

    /***
     *  读取int型的配置项，未配置返回默认值
     * @param key
     * @param defaultValue
     * @return
     */
    public static Integer getInteger(String key, Integer defaultValue){
        // 获取配置值
        String value = get(key);
        if(V.notEmpty(value)){
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    /***
     *  读取Long型的配置项
     * @param key
     * @return
     */
    public static Long getLong(String key){
        return getLong(key, null);
    }

    /***
     *  读取Long型的配置项，未配置返回默认值
     * @param key
     * @param defaultValue
     * @return
     */
    public static Long getLong(String key, Long defaultValue){
        // 获取配置值
        String value = get(key);
        if(V.notEmpty(value)){
            return Long.parseLong(value);
        }
        return defaultValue;
    }

    /***
     * 读取boolean值的配置项
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 读取boolean值的配置项，未配置返回默认值
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        // 获取配置值
        String value = get(key);
        if(V.notEmpty(value)){
            return V.isTrue(value);
        }
        return defaultValue;
    }

}
