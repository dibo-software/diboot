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

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.diboot.core.converter.*;
import com.diboot.core.data.ProtectFieldHandler;
import com.diboot.core.data.encrypt.ProtectInterceptor;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.D;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Diboot Core自动配置类
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/08/01
 */
@EnableAsync
@Configuration
@EnableConfigurationProperties({CoreProperties.class, GlobalProperties.class})
@ComponentScan(basePackages = {"com.diboot.core"})
@MapperScan(basePackages = {"com.diboot.core.mapper"})
public class CoreAutoConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(CoreAutoConfig.class);

    @Value("${spring.jackson.date-format:"+D.FORMAT_DATETIME_Y4MDHMS+"}")
    private String defaultDatePattern;

    @Value("${spring.jackson.time-zone:GMT+8}")
    private String defaultTimeZone;

    @Value("${spring.jackson.default-property-inclusion:NON_NULL}")
    private JsonInclude.Include defaultPropertyInclusion;

    /**
     * 默认配置 ObjectMapper, 并允许用户覆盖
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            // Long转换成String避免JS超长问题
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            builder.serializerByType(BigInteger.class, ToStringSerializer.instance);

            // 支持java8时间类型
            // LocalDateTime
            DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern(D.FORMAT_DATETIME_Y4MDHMS);
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(localDateTimeFormatter));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(localDateTimeFormatter));
            // LocalDate
            DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern(D.FORMAT_DATE_Y4MD);
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(localDateFormatter));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(localDateFormatter));
            // LocalTime
            DateTimeFormatter localTimeFormatter = DateTimeFormatter.ofPattern(D.FORMAT_TIME_HHmmss);
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(localTimeFormatter));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(localTimeFormatter));

            // 设置序列化包含策略
            builder.serializationInclusion(defaultPropertyInclusion);
            // 时间格式化
            builder.failOnUnknownProperties(false);
            builder.timeZone(TimeZone.getTimeZone(defaultTimeZone));
            SimpleDateFormat dateFormat = new SimpleDateFormat(defaultDatePattern) {
                @Override
                public Date parse(String dateStr) {
                    return D.fuzzyConvert(dateStr);
                }
            };
            builder.dateFormat(dateFormat);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters jacksonHttpMessageConverters() {
        return new HttpMessageConverters(jacksonMessageConverter());
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingJackson2HttpMessageConverter jacksonMessageConverter(){
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 优先使用全局默认ObjectMapper, 保证ObjectMapper全局配置相同
        ObjectMapper objectMapper = ContextHelper.getBean(ObjectMapper.class);
        if (objectMapper == null) {
            objectMapper = converter.getObjectMapper();
        }
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    /**
     * Mybatis-plus分页插件
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 数据保护拦截器
     * <p>
     * 默认不注入，diboot.core.enable-data-protect=true可开启
     */
    @Bean
    @ConditionalOnBean(ProtectFieldHandler.class)
    public ProtectInterceptor protectInterceptor() {
        return new ProtectInterceptor();
    }

    /**
     * 默认支持String-Date类型转换
     *
     * @param registry registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Date2LocalDateConverter());
        registry.addConverter(new Date2LocalDateTimeConverter());
        registry.addConverter(new String2DateConverter());
        registry.addConverter(new String2BooleanConverter());
        registry.addConverter(new Timestamp2LocalDateTimeConverter());
    }

}
