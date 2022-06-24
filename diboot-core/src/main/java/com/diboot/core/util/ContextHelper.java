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

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.binding.parser.PropInfo;
import com.diboot.core.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Spring上下文帮助类
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
@Component
@Lazy(false)
public class ContextHelper implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(ContextHelper.class);

    /***
     * ApplicationContext上下文
     */
    private static ApplicationContext APPLICATION_CONTEXT = null;

    /**
     * 数据库类型
     */
    private static String DATABASE_TYPE = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
        log.debug("ApplicationContext已赋值: {}", APPLICATION_CONTEXT.getDisplayName());
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        APPLICATION_CONTEXT = event.getApplicationContext();
        log.debug("ApplicationContext已注入: {}", APPLICATION_CONTEXT.getDisplayName());
    }

    /***
     * 获取ApplicationContext上下文
     */
    public static ApplicationContext getApplicationContext() {
        if (APPLICATION_CONTEXT == null){
            log.debug("ApplicationContext未初始化，通过ContextLoader获取!");
            APPLICATION_CONTEXT = ContextLoader.getCurrentWebApplicationContext();
        }
        if(APPLICATION_CONTEXT == null){
            log.warn("无法获取ApplicationContext，请确保ComponentScan扫描路径包含com.diboot包路径，并在Spring初始化之后调用接口!");
        }
        return APPLICATION_CONTEXT;
    }

    /***
     * 根据beanId获取Bean实例
     * @param beanId
     * @return
     */
    public static Object getBean(String beanId){
        return getApplicationContext().getBean(beanId);
    }

    /***
     * 获取指定类型的单个Bean实例
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz){
        try{
            return getApplicationContext().getBean(clazz);
        }
        catch (Exception e){
            log.debug("instance not found: {}", clazz.getSimpleName());
            return null;
        }
    }

    /***
     * 获取指定类型的全部实现类
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> getBeans(Class<T> type){
        Map<String, T> map = getApplicationContext().getBeansOfType(type);
        if(V.isEmpty(map)){
            return null;
        }
        List<T> beanList = new ArrayList<>(map.size());
        beanList.addAll(map.values());
        return beanList;
    }

    /***
     * 根据注解获取beans
     * @param annotationType
     * @return
     */
    public static List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationType){
        Map<String, Object> map = getApplicationContext().getBeansWithAnnotation(annotationType);
        if(V.isEmpty(map)){
            return null;
        }
        List<Object> beanList = new ArrayList<>();
        beanList.addAll(map.values());
        return beanList;
    }

    /**
     * 根据Entity获取对应的Service (已废弃，请调用getBaseServiceByEntity)
     * @param entity
     * @return
     */
    @Deprecated
    public static IService getServiceByEntity(Class entity){
        return getIServiceByEntity(entity);
    }

    /**
     * 根据Entity获取对应的IService实现
     * @param entity
     * @return
     */
    public static IService getIServiceByEntity(Class entity){
        EntityInfoCache entityInfoCache = BindingCacheManager.getEntityInfoByClass(entity);
        IService iService = entityInfoCache != null? entityInfoCache.getService() : null;
        if(iService == null){
            log.info("未能识别到Entity: "+entity.getName()+" 的IService实现！");
        }
        return iService;
    }

    /**
     * 根据Entity获取对应的BaseService实现
     * @param entity
     * @return
     */
    public static BaseService getBaseServiceByEntity(Class entity){
        EntityInfoCache entityInfoCache = BindingCacheManager.getEntityInfoByClass(entity);
        IService iService = entityInfoCache != null? entityInfoCache.getService() : null;
        if(iService == null){
            log.info("未能识别到Entity: "+entity.getName()+" 的Service实现！");
        }
        if(iService instanceof BaseService){
            return (BaseService)iService;
        }
        log.warn("Entity的service实现类: {} 非BaseService实现！", entityInfoCache.getService());
        return null;
    }

    /**
     * 根据Entity获取对应的BaseMapper实现
     * @param entityClass
     * @return
     */
    public static BaseMapper getBaseMapperByEntity(Class entityClass){
        return ParserCache.getMapperInstance(entityClass);
    }

    /**
     * 获取Entity主键列名
     * @return
     */
    public static String getIdColumnName(Class entity){
        PropInfo propInfoCache = BindingCacheManager.getPropInfoByClass(entity);
        return propInfoCache != null? propInfoCache.getIdColumn() : null;
    }

    /**
     * 获取Entity主键字段名
     * @return
     */
    public static String getIdFieldName(Class entity){
        PropInfo propInfoCache = BindingCacheManager.getPropInfoByClass(entity);
        return propInfoCache != null ? propInfoCache.getIdFieldName() : null;
    }

    /**
     * 获取Entity主键
     * @return
     */
    @Deprecated
    public static String getPrimaryKey(Class entity){
        return getIdFieldName(entity);
    }

    /***
     * 获取JdbcUrl
     * @return
     */
    public static String getJdbcUrl() {
        ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext == null) {
            return null;
        }
        String jdbcUrl = null;
        try{
            DataSource dataSource = applicationContext.getBean(DataSource.class);
            Connection connection = dataSource.getConnection();
            jdbcUrl = connection.getMetaData().getURL();
            connection.close();
            return jdbcUrl;
        }
        catch (Exception e){
            log.warn("获取JDBC URL异常: {}", e.getMessage());
        }
        // 候补识别方式，暂时保留
        Environment environment = applicationContext.getEnvironment();
        jdbcUrl = environment.getProperty("spring.datasource.url");
        if(jdbcUrl == null){
            jdbcUrl = environment.getProperty("spring.datasource.druid.url");
        }
        if(jdbcUrl == null){
            String master = environment.getProperty("spring.datasource.dynamic.primary");
            if(master != null){
                jdbcUrl = environment.getProperty("spring.datasource.dynamic.datasource."+master+".url");
            }
        }
        if(jdbcUrl == null){
            String names = environment.getProperty("spring.shardingsphere.datasource.names");
            if(names != null){
                jdbcUrl = environment.getProperty("spring.shardingsphere.datasource."+ names.split(",")[0] +".url");
            }
        }
        if(jdbcUrl == null){
            String urlConfigItem = environment.getProperty("diboot.datasource.url.config");
            if(urlConfigItem != null){
                jdbcUrl = environment.getProperty(urlConfigItem);
            }
        }
        return jdbcUrl;
    }

    /**
     * 获取数据库类型
     * @return
     */
    public static String getDatabaseType(){
        if(DATABASE_TYPE != null){
            return DATABASE_TYPE;
        }
        String jdbcUrl = getJdbcUrl();
        if(jdbcUrl != null){
            DbType dbType = JdbcUtils.getDbType(jdbcUrl);
            DATABASE_TYPE = dbType.getDb();
            if(DATABASE_TYPE.startsWith(DbType.SQL_SERVER.getDb())){
                DATABASE_TYPE = DbType.SQL_SERVER.getDb();
            }
        }
        if(DATABASE_TYPE == null){
            log.warn("无法识别数据库类型，请检查数据源配置:spring.datasource.url等");
        }
        return DATABASE_TYPE;
    }

}
