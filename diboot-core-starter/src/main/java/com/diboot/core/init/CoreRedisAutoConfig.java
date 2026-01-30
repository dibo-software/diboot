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
package com.diboot.core.init;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.diboot.core.cache.DictionaryCacheManager;
import com.diboot.core.cache.DynamicRedisCacheManager;
import com.diboot.core.cache.I18nCacheManager;
import com.diboot.core.config.Cons;
import com.diboot.core.extension.sequence.counter.SeqCounter;
import com.diboot.core.extension.sequence.counter.RedisCacheSeqCounter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonObjectReader;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Redis 自动配置
 *
 * @author wind
 * @version v2.3.0
 */
@Slf4j
@Order(901)
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@ConditionalOnResource(resources = "org/springframework/data/redis")
public class CoreRedisAutoConfig {

    public CoreRedisAutoConfig() {
        log.info("初始化 core 内核 Redis 自动配置");
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        //... 初始化RedisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 用StringRedisSerializer 序列化和反序列化key值
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 用GenericJacksonJsonRedisSerializer 序列化和反序列化value值
        GenericJacksonJsonRedisSerializer jacksonJsonRedisSerializer = genericJacksonJsonRedisSerializer();
        redisTemplate.setValueSerializer(jacksonJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jacksonJsonRedisSerializer);
        redisTemplate.setDefaultSerializer(jacksonJsonRedisSerializer);

        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer() {
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("rewrite-bean", SimpleBeanPropertyFilter.serializeAllExcept("realmNames"));
        // 开启多态验证器，设置后允许序列化中加入@class 属性
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).allowIfSubType((ctx, clazz) -> true).build();
        Supplier<JsonMapper.Builder> jsonMapperBuilder = () ->
                JsonMapper.builder()
                        .changeDefaultVisibility(v -> v.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                        .activateDefaultTyping(typeValidator) // 反序列化的时候允许使用@class属性
                        .filterProvider(filterProvider)
                        .changeDefaultPropertyInclusion(inc -> inc.withValueInclusion(JsonInclude.Include.NON_NULL));
        return GenericJacksonJsonRedisSerializer.builder(jsonMapperBuilder)
                .enableDefaultTyping(typeValidator)
                .build();
    }

    /**
     * 字典等基础数据缓存管理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DictionaryCacheManager dictionaryCacheManager(RedisTemplate redisTemplate) {
        log.info("初始化 Dictionary Redis缓存: DynamicRedisCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<>() {{
            put(Cons.CACHE_NAME_DICTIONARY, 24*60);
        }};
        DynamicRedisCacheManager redisCacheManager = new DynamicRedisCacheManager(redisTemplate, cacheName2ExpireMap);
        return new DictionaryCacheManager(redisCacheManager);
    }

    /**
     * 国际化等基础数据缓存管理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public I18nCacheManager i18nCacheManager(RedisTemplate redisTemplate) {
        log.info("初始化 I18n Redis缓存: DynamicRedisCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<>() {{
            put(Cons.CACHE_NAME_I18N, 24*60);
        }};
        DynamicRedisCacheManager memoryCacheManager = new DynamicRedisCacheManager(redisTemplate, cacheName2ExpireMap);
        return new I18nCacheManager(memoryCacheManager);
    }

    /**
     * 计数器
     */
    @Bean
    @ConditionalOnMissingBean(SeqCounter.class)
    public SeqCounter redisCacheSeqCounter(RedisTemplate<String, Object> redisTemplate) {
        log.info("初始化 流水号计数器 Redis缓存: RedisCacheSeqCounter");
        return new RedisCacheSeqCounter(redisTemplate);
    }

}
