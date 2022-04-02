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

import com.diboot.core.config.BaseConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.util.*;

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
        // 获取全局的ObjectMapper, 避免重复配置
        MappingJackson2HttpMessageConverter jacksonMessageConvertor = ContextHelper.getBean(MappingJackson2HttpMessageConverter.class);
        if(jacksonMessageConvertor != null){
            objectMapper = jacksonMessageConvertor.getObjectMapper();
            log.debug("初始化ObjectMapper完成（复用全局定义）");
        }
        else{
            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // 设置时区和日期转换
            String defaultDatePattern = BaseConfig.getProperty("spring.jackson.date-format", D.FORMAT_DATETIME_Y4MDHMS);
            SimpleDateFormat dateFormat = new SimpleDateFormat(defaultDatePattern) {
                @Override
                public Date parse(String dateStr) {
                    return D.fuzzyConvert(dateStr);
                }
            };
            objectMapper.setDateFormat(dateFormat);
            String timeZone = BaseConfig.getProperty("spring.jackson.time-zone", "GMT+8");
            objectMapper.setTimeZone(TimeZone.getTimeZone(timeZone));
            // 不存在的属性，不转化，否则报错：com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            log.debug("初始化ObjectMapper完成（new新实例）");
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