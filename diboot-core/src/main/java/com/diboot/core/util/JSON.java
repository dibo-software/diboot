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

import com.diboot.core.exception.InvalidUsageException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/***
 * JSON操作辅助类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
@SuppressWarnings({"unchecked", "JavaDoc", "UnnecessaryLocalVariable"})
public class JSON {
    private static final Logger log = LoggerFactory.getLogger(JSON.class);

    private static ObjectMapper objectMapper;

    /**
     * 初始化ObjectMapper
     * @return
     */
    private static ObjectMapper getObjectMapper(){
        if(objectMapper != null){
            return objectMapper;
        }
        objectMapper = ContextHelper.getBean(ObjectMapper.class);
        if(objectMapper == null){
            throw new InvalidUsageException("未找到 ObjectMapper实例，请检查配置类！");
        }
        return objectMapper;
    }

    /**
     * 将Java对象转换为Json String
     *
     * @param object
     * @return
     */
    public static String stringify(Object object) {
        return toJSONString(object);
    }

    /**
     * 转换对象为JSON字符串
     *
     * @param model
     * @return
     */
    public static String toJSONString(Object model) {
        try {
            String json = getObjectMapper().writeValueAsString(model);
            return json;
        } catch (Exception e) {
            log.error("Java转Json异常", e);
            return null;
        }
    }

    /***
     * 将JSON字符串转换为java对象
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T toJavaObject(String jsonStr, Class<T> clazz) {
        try {
            T model = getObjectMapper().readValue(jsonStr, clazz);
            return model;
        } catch (Exception e) {
            log.error("Json转Java异常", e);
            return null;
        }
    }

    /***
     * 将JSON字符串转换为Map<String, Object></>对象
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> parseObject(String jsonStr) {
        try {
            JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            return getObjectMapper().readValue(jsonStr, javaType);
        } catch (Exception e) {
            log.error("Json转Java异常", e);
            return null;
        }
    }

    /***
     * 将JSON字符串转换为java对象
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        return toJavaObject(jsonStr, clazz);
    }

    /***
     * 将JSON字符串转换为复杂类型的Java对象
     * @param jsonStr
     * @param typeReference
     * @return
     */
    public static <T> T parseObject(String jsonStr, TypeReference<T> typeReference) {
        try {
            T model = getObjectMapper().readValue(jsonStr, typeReference);
            return model;
        } catch (Exception e) {
            log.error("Json转Java异常", e);
            return null;
        }
    }


    /***
     * 将JSON字符串转换为list对象
     * @param jsonStr
     * @return
     */
    public static <T> List<T> parseArray(String jsonStr, Class<T> clazz) {
        try {
            JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(List.class, clazz);
            return getObjectMapper().readValue(jsonStr, javaType);
        } catch (Exception e) {
            log.error("Json转Java异常", e);
            return null;
        }
    }

    /***
     * 将JSON字符串转换为java对象
     * @param jsonStr
     * @return
     */

    public static <K, T> Map<K, T> toMap(String jsonStr) {
        return (Map<K, T>) toJavaObject(jsonStr, Map.class);
    }

    /***
     * 将JSON字符串转换为Map对象
     * @param jsonStr
     * @return
     */
    public static<K, T> LinkedHashMap<K, T> toLinkedHashMap(String jsonStr) {
        if (V.isEmpty(jsonStr)) {
            return null;
        }
        return (LinkedHashMap<K, T>)toJavaObject(jsonStr, LinkedHashMap.class);
    }
}