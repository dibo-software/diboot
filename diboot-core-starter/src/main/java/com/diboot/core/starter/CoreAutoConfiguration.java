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
package com.diboot.core.starter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.diboot.core.config.Cons;
import com.diboot.core.util.DateConverter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * DibootCore自动配置类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/08/01
 */
@Configuration
@EnableConfigurationProperties(CoreProperties.class)
@ComponentScan(basePackages={"com.diboot.core"})
@MapperScan(basePackages = {"com.diboot.core.mapper"})
@Order(1)
public class CoreAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    Environment environment;

    @Autowired
    CoreProperties coreProperties;

    @Bean
    @ConditionalOnMissingBean(CorePluginManager.class)
    public CorePluginManager corePluginManager(){
        // 初始化SCHEMA
        SqlHandler.init(environment);
        CorePluginManager pluginManager = new CorePluginManager() {};
        // 检查数据库字典是否已存在
        if(coreProperties.isInitSql() && SqlHandler.checkIsDictionaryTableExists() == false){
            SqlHandler.initBootstrapSql(pluginManager.getClass(), environment, "core");
        }
        return pluginManager;
    }

    @Bean
    @ConditionalOnMissingBean(HttpMessageConverters.class)
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setDefaultCharset(Charset.forName(Cons.CHARSET_UTF8));
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(fastMediaTypes);
        // 配置转换格式
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 设置fastjson的序列化参数：禁用循环依赖检测，数据兼容浏览器端（避免JS端Long精度丢失问题）
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.BrowserCompatible);
        converter.setFastJsonConfig(fastJsonConfig);

        HttpMessageConverter<?> httpMsgConverter = converter;
        return new HttpMessageConverters(httpMsgConverter);
    }

    /**
     * 默认支持String-Date类型转换
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new DateConverter());
    }
}