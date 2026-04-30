/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.core.data.protect.DataEncryptHandler;
import com.diboot.core.data.protect.DataMaskHandler;
import com.diboot.core.data.protect.DefaultDataEncryptHandler;
import com.diboot.core.data.protect.DefaultDataMaskHandler;
import com.diboot.core.extension.sequence.counter.SeqCounter;
import com.diboot.core.extension.sequence.counter.MemoryCacheSeqCounter;
import com.diboot.core.serial.deserializer.LocalDateTimeDeserializer;
import com.diboot.core.config.CoreProperties;
import com.diboot.core.config.GlobalProperties;
import com.diboot.core.init.CoreRedisAutoConfig;
import com.diboot.core.serial.serializer.BigDecimal2StringSerializer;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.D;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
@AutoConfigureBefore(JacksonAutoConfiguration.class)
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

    @Bean
    @ConditionalOnMissingBean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.enable(SerializationFeature.INDENT_OUTPUT);
            builder.addModule(new SimpleModule()
                            .addSerializer(Long.class, ToStringSerializer.instance)
                            .addSerializer(Long.TYPE, ToStringSerializer.instance)
                            .addSerializer(BigInteger.class, ToStringSerializer.instance)
                            // BigDecimal转换成String避免JS超长问题，以及格式化数值
                            .addSerializer(BigDecimal.class, new BigDecimal2StringSerializer())
                            // 日期时间格式
                            .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(D.FORMATTER_DATETIME_Y4MDHMS))
                            .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
                            .addSerializer(LocalDate.class, new LocalDateSerializer(D.FORMATTER_DATE_Y4MD))
                    .       addDeserializer(LocalDate.class, new LocalDateDeserializer(D.FORMATTER_DATE_Y4MD))
                            .addSerializer(LocalTime.class, new LocalTimeSerializer(D.FORMATTER_TIME_HM))
                            .addDeserializer(LocalTime.class, new LocalTimeDeserializer(D.FORMATTER_TIME_HM))
             );
            // 设置序列化包含策略
            builder.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(defaultPropertyInclusion));
            builder.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 时间格式化
            builder.defaultTimeZone(TimeZone.getTimeZone(defaultTimeZone));
            SimpleDateFormat dateFormat = new SimpleDateFormat(defaultDatePattern) {
                @Override
                public Date parse(String dateStr) {
                    return D.fuzzyConvert(dateStr);
                }
            };
            builder.defaultDateFormat(dateFormat);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public JacksonJsonHttpMessageConverter jacksonMessageConverter() {
        JsonMapper jsonMapper = JsonMapper.builder()
                .changeDefaultVisibility(v-> v.withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.DEFAULT))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .changeDefaultPropertyInclusion(inc -> inc.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        return new JacksonJsonHttpMessageConverter(jsonMapper);
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
        log.debug("初始化默认的加密实现：DataEncryptHandler");
        return new DefaultDataEncryptHandler();
    }

    /**
     * 数据脱敏处理器
     */
    @Bean
    @ConditionalOnMissingBean(DataMaskHandler.class)
    public DataMaskHandler dataMaskHandler() {
        log.debug("初始化默认的脱敏实现：DataMaskHandler");
        return new DefaultDataMaskHandler();
    }

    /**
     * 默认支持各种类型转换
     *
     * @param registry registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        List<Converter> converterList = ContextHolder.getBeans(Converter.class);
        if (converterList != null && !converterList.isEmpty())
            converterList.forEach(registry::addConverter);
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
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DictionaryCacheManager dictionaryCacheManager() {
        log.info("初始化 Dictionary 内存缓存: DynamicMemoryCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<>() {{
            put(Cons.CACHE_NAME_DICTIONARY, 24 * 60);
        }};
        DynamicMemoryCacheManager memoryCacheManager = new DynamicMemoryCacheManager(cacheName2ExpireMap);
        return new DictionaryCacheManager(memoryCacheManager);
    }

    /**
     * 国际化等基础数据缓存管理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public I18nCacheManager i18nCacheManager() {
        log.info("初始化 I18n 内存缓存: DynamicMemoryCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<>() {{
            put(Cons.CACHE_NAME_I18N, 24 * 60);
        }};
        DynamicMemoryCacheManager memoryCacheManager = new DynamicMemoryCacheManager(cacheName2ExpireMap);
        return new I18nCacheManager(memoryCacheManager);
    }


    /**
     * 国际化默认环境配置
     *
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
     *
     * @return
     */
    @Bean
    public static RequestContextFilter requestContextFilter() {
        OrderedRequestContextFilter orderedRequestContextFilter = new OrderedRequestContextFilter();
        orderedRequestContextFilter.setThreadContextInheritable(true);
        return orderedRequestContextFilter;
    }

    /**
     * 计数器
     */
    @Bean
    @ConditionalOnMissingBean(SeqCounter.class)
    public SeqCounter memoryCacheSeqCounter() {
        log.info("初始化 流水号计数器 内存缓存: MemoryCacheSeqCounter");
        return new MemoryCacheSeqCounter();
    }

}