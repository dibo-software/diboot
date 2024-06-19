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
package com.diboot.starter;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.diboot.core.cache.DictionaryCacheManager;
import com.diboot.core.cache.DynamicMemoryCacheManager;
import com.diboot.core.cache.I18nCacheManager;
import com.diboot.core.config.Cons;
import com.diboot.core.config.MessageSourceBeanPostProcessor;
import com.diboot.core.converter.*;
import com.diboot.core.data.protect.DataEncryptHandler;
import com.diboot.core.data.protect.DataMaskHandler;
import com.diboot.core.data.protect.DefaultDataEncryptHandler;
import com.diboot.core.data.protect.DefaultDataMaskHandler;
import com.diboot.core.serial.deserializer.LocalDateTimeDeserializer;
import com.diboot.core.config.CoreProperties;
import com.diboot.core.config.GlobalProperties;
import com.diboot.core.init.CoreRedisAutoConfig;
import com.diboot.core.serial.serializer.BigDecimal2StringSerializer;
import com.diboot.core.util.D;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Diboot Core自动配置类
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/08/01
 */
@Order(902)
@EnableAsync
@Configuration
@AutoConfigureAfter(CoreRedisAutoConfig.class)
@EnableConfigurationProperties({CoreProperties.class, GlobalProperties.class})
@ComponentScan(basePackages = {"com.diboot.core"})
@MapperScan(basePackages = {"com.diboot.core.mapper"})
public class CoreAutoConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(CoreAutoConfig.class);

    @Value("${spring.jackson.date-format:" + D.FORMAT_DATETIME_Y4MDHMS + "}")
    private String defaultDatePattern;

    @Value("${spring.jackson.time-zone:GMT+8}")
    private String defaultTimeZone;

    @Value("${spring.jackson.default-property-inclusion:NON_NULL}")
    private JsonInclude.Include defaultPropertyInclusion;

    public CoreAutoConfig() {
        log.info("初始化 core 内核 自动配置");
    }

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
            // BigDecimal转换成String避免JS超长问题，以及格式化数值
            builder.serializerByType(BigDecimal.class, BigDecimal2StringSerializer.instance);

            // 支持java8时间类型
            // LocalDateTime
            DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern(D.FORMAT_DATETIME_Y4MDHMS);
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(localDateTimeFormatter));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer());
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
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder objectMapperBuilder = new Jackson2ObjectMapperBuilder();
        jsonCustomizer().customize(objectMapperBuilder);
        log.info("启用diboot默认的Jackson自定义配置");
        return objectMapperBuilder;
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingJackson2HttpMessageConverter jacksonMessageConverter() {
        return new MappingJackson2HttpMessageConverter(jackson2ObjectMapperBuilder().build());
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
     * 数据加密解密处理器
     */
    @Bean
    @ConditionalOnMissingBean(DataEncryptHandler.class)
    public DataEncryptHandler dataEncryptHandler() {
        log.debug("初始化默认的DataEncryptHandler");
        return new DefaultDataEncryptHandler();
    }

    /**
     * 数据脱敏处理器
     */
    @Bean
    @ConditionalOnMissingBean(DataMaskHandler.class)
    public DataMaskHandler dataMaskHandler() {
        log.debug("初始化默认的DataMaskHandler");
        return new DefaultDataMaskHandler();
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
        registry.addConverter(new LocalDate2DateConverter());
        registry.addConverter(new LocalDateTime2DateConverter());
        registry.addConverter(new LocalDateTime2StringConverter());
        registry.addConverter(new SqlDate2LocalDateConverter());
        registry.addConverter(new SqlDate2LocalDateTimeConverter());
        registry.addConverter(new String2DateConverter());
        registry.addConverter(new String2LocalDateConverter());
        registry.addConverter(new String2LocalDateTimeConverter());
        registry.addConverter(new String2BooleanConverter());
        registry.addConverter(new String2ListConverter());
        registry.addConverter(new String2MapConverter());
        registry.addConverter(new Timestamp2LocalDateTimeConverter());
    }

    /**
     * 扩展Mybatis 类型转换，支持日期类型转为LocalDate等
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer typeHandlerRegistry() {
        return configuration -> configuration.getTypeHandlerRegistry().register(java.sql.Date.class, JdbcType.DATE, LocalDateTypeHandler.class);
    }

    /**
     * 字典等基础数据缓存管理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DictionaryCacheManager dictionaryCacheManager() {
        log.info("初始化 Dictionary 内存缓存: DynamicMemoryCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<>() {{
            put(Cons.CACHE_NAME_DICTIONARY, 24*60);
        }};
        DynamicMemoryCacheManager memoryCacheManager = new DynamicMemoryCacheManager(cacheName2ExpireMap);
        return new DictionaryCacheManager(memoryCacheManager);
    }

    /**
     * 国际化等基础数据缓存管理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public I18nCacheManager i18nCacheManager() {
        log.info("初始化 I18n 内存缓存: DynamicMemoryCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<>() {{
            put(Cons.CACHE_NAME_I18N, 24*60);
        }};
        DynamicMemoryCacheManager memoryCacheManager = new DynamicMemoryCacheManager(cacheName2ExpireMap);
        return new I18nCacheManager(memoryCacheManager);
    }


    /**
     * 国际化默认环境配置
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return localeResolver;
    }

    /**
     * Request上下文允许子线程使用
     * @return
     */
    @Bean
    public static RequestContextFilter requestContextFilter() {
        OrderedRequestContextFilter orderedRequestContextFilter = new OrderedRequestContextFilter();
        orderedRequestContextFilter.setThreadContextInheritable(true);
        return orderedRequestContextFilter;
    }

    /**
     * 国际化文件配置
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageSourceBeanPostProcessor messageSourceBeanPostProcessor() {
        return new MessageSourceBeanPostProcessor();
    }

}
