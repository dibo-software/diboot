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
package com.diboot.core.binding.cache;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.binding.parser.TableLinkage;
import com.diboot.core.cache.StaticMemoryCacheManager;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * CacheManager
 * @author mazc@dibo.ltd
 * @version v2.2.1
 * @date 2021/04/17
 */
@Slf4j
public class BindingCacheManager {
    /**
     * 实体相关定义缓存管理器
     */
    private static StaticMemoryCacheManager cacheManager;
    /**
     * cache key
     */
    private static final String CACHE_NAME_CLASS_ENTITY = "CLASS_ENTITY";
    /**
     * cache key
     */
    private static final String CACHE_NAME_TABLE_ENTITY = "TABLE_ENTITY";
    /**
     * cache key
     */
    private static final String CACHE_NAME_TABLE_MAPPER = "TABLE_MAPPER";

    private static StaticMemoryCacheManager getCacheManager(){
        if(cacheManager == null){
            cacheManager = new StaticMemoryCacheManager(
                    CACHE_NAME_CLASS_ENTITY,
                    CACHE_NAME_TABLE_ENTITY,
                    CACHE_NAME_TABLE_MAPPER);
        }
        return cacheManager;
    }

    /**
     * 根据tableName获取cache
     * @param tableName
     * @return
     */
    public static EntityInfoCache getEntityInfoByTable(String tableName){
        initEntityInfoCache();
        return getCacheManager().getCacheObj(CACHE_NAME_TABLE_ENTITY, tableName, EntityInfoCache.class);
    }

    /**
     * 根据entity类获取cache
     * @param entityClazz
     * @return
     */
    public static EntityInfoCache getEntityInfoByClass(Class<?> entityClazz){
        initEntityInfoCache();
        return getCacheManager().getCacheObj(CACHE_NAME_CLASS_ENTITY, entityClazz.getName(), EntityInfoCache.class);
    }

    /**
     * 通过table获取mapper
     * @param table
     * @return
     */
    public static BaseMapper getMapperByTable(String table){
        EntityInfoCache entityInfoCache = getEntityInfoByTable(table);
        if(entityInfoCache != null){
            return entityInfoCache.getBaseMapper();
        }
        initTable2MapperCache();
        Class<?> mapperClass = getCacheManager().getCacheObj(CACHE_NAME_TABLE_MAPPER, table, Class.class);
        if(mapperClass != null){
            BaseMapper mapper = (BaseMapper) ContextHelper.getBean(mapperClass);
            return mapper;
        }
        return null;
    }

    /**
     * 初始化
     */
    private static void initEntityInfoCache(){
        StaticMemoryCacheManager cacheManager = getCacheManager();
        if(cacheManager.isUninitializedCache(CACHE_NAME_CLASS_ENTITY) == false){
            return;
        }
        Map<String, IService> serviceMap = ContextHelper.getApplicationContext().getBeansOfType(IService.class);
        if(V.isEmpty(serviceMap)){
            log.debug("未获取到任何有效@Service.");
        }
        Set<String> uniqueEntitySet = new HashSet<>();
        for(Map.Entry<String, IService> entry : serviceMap.entrySet()){
            Class entityClass = BeanUtils.getGenericityClass(entry.getValue(), 1);
            if(entityClass != null){
                IService entityIService = entry.getValue();
                if(uniqueEntitySet.contains(entityClass.getName())){
                    if(entityIService.getClass().getAnnotation(Primary.class) != null){
                        EntityInfoCache entityInfoCache = cacheManager.getCacheObj(CACHE_NAME_CLASS_ENTITY, entityClass.getName(), EntityInfoCache.class);
                        if(entityInfoCache != null){
                            entityInfoCache.setService(entityIService);
                        }
                    }
                    else{
                        log.warn("Entity: {} 存在多个service实现类，可能导致调用实例与预期不一致!", entityClass.getName());
                    }
                }
                else{
                    EntityInfoCache entityInfoCache = new EntityInfoCache(entityClass, entityIService);
                    cacheManager.putCacheObj(CACHE_NAME_CLASS_ENTITY, entityClass.getName(), entityInfoCache);
                    cacheManager.putCacheObj(CACHE_NAME_TABLE_ENTITY, entityInfoCache.getTableName(), entityInfoCache);
                    uniqueEntitySet.add(entityClass.getName());
                }
            }
        }
    }

    /**
     * 初始化Table-mapper缓存
     */
    private static void initTable2MapperCache(){
        StaticMemoryCacheManager cacheManager = getCacheManager();
        if(cacheManager.isUninitializedCache(CACHE_NAME_TABLE_MAPPER) == false){
            return;
        }
        SqlSessionFactory sqlSessionFactory = ContextHelper.getBean(SqlSessionFactory.class);
        Collection<Class<?>> mappers = sqlSessionFactory.getConfiguration().getMapperRegistry().getMappers();
        if(V.notEmpty(mappers)){
            mappers.forEach(m->{
                Type[] types = m.getGenericInterfaces();
                try{
                    if(types != null && types.length > 0 && types[0] != null){
                        ParameterizedType genericType = (ParameterizedType) types[0];
                        Type[] superTypes = genericType.getActualTypeArguments();
                        if(superTypes != null && superTypes.length > 0 && superTypes[0] != null){
                            String entityClassName = superTypes[0].getTypeName();
                            if(entityClassName.length() > 1){
                                Class<?> entityClass = Class.forName(entityClassName);
                                String table = ParserCache.getEntityTableName(entityClass);
                                cacheManager.putCacheObj(CACHE_NAME_TABLE_MAPPER, table, m);
                            }
                        }
                    }
                }
                catch (Exception e){
                    log.warn("解析mapper异常", e);
                }
            });
        }
    }

}
