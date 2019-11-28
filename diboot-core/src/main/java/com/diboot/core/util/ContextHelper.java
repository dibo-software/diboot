package com.diboot.core.util;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring上下文帮助类
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
@Component
@Lazy(false)
public class ContextHelper implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ContextHelper.class);

    /***
     * ApplicationContext上下文
     */
    private static ApplicationContext APPLICATION_CONTEXT = null;

    /**
     * Entity-对应的Service缓存
     */
    private static Map<String, IService> ENTITY_SERVICE_CACHE = null;
    /**
     * Entity-对应的BaseService缓存
     */
    private static Map<String, BaseService> ENTITY_BASE_SERVICE_CACHE = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(APPLICATION_CONTEXT == null){
            APPLICATION_CONTEXT = applicationContext;
        }
    }

    /***
     * 获取ApplicationContext上下文
     */
    public static ApplicationContext getApplicationContext() {
        if (APPLICATION_CONTEXT == null){
            return ContextLoader.getCurrentWebApplicationContext();
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
        return getApplicationContext().getBean(clazz);
    }

    /***
     * 获取指定类型的全部实例
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> getBeans(Class<T> type){
        // 获取所有的定时任务实现类
        Map<String, T> map = getApplicationContext().getBeansOfType(type);
        if(V.isEmpty(map)){
            return null;
        }
        List<T> beanList = new ArrayList<>();
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
     * 根据Entity获取对应的Service (已废弃，请调用getIServiceByEntity)
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
        if(ENTITY_SERVICE_CACHE == null){
            ENTITY_SERVICE_CACHE = new ConcurrentHashMap<>();
            Map<String, IService> serviceMap = getApplicationContext().getBeansOfType(IService.class);
            if(V.notEmpty(serviceMap)){
                for(Map.Entry<String, IService> entry : serviceMap.entrySet()){
                    String entityClassName = getEntityClassByServiceImpl(entry.getValue().getClass());
                    if(V.notEmpty(entityClassName)){
                        ENTITY_SERVICE_CACHE.put(entityClassName, entry.getValue());
                    }
                }
            }
        }
        return ENTITY_SERVICE_CACHE.get(entity.getName());
    }

    /**
     * 根据Entity获取对应的BaseService实现
     * @param entity
     * @return
     */
    public static BaseService getBaseServiceByEntity(Class entity){
        if(ENTITY_BASE_SERVICE_CACHE == null){
            ENTITY_BASE_SERVICE_CACHE = new ConcurrentHashMap<>();
            Map<String, BaseService> serviceMap = getApplicationContext().getBeansOfType(BaseService.class);
            if(V.notEmpty(serviceMap)){
                for(Map.Entry<String, BaseService> entry : serviceMap.entrySet()){
                    String entityClassName = getEntityClassByServiceImpl(entry.getValue().getClass());
                    if(V.notEmpty(entityClassName)){
                        ENTITY_BASE_SERVICE_CACHE.put(entityClassName, entry.getValue());
                    }
                }
            }
        }
        return ENTITY_BASE_SERVICE_CACHE.get(entity.getName());
    }

    /**
     * 根据Service实现类的bean解析出Entity类名
     * @param currentClass
     * @return
     */
    private static String getEntityClassByServiceImpl(Class currentClass){
        ResolvableType superType = ResolvableType.forClass(currentClass).getSuperType();
        ResolvableType[] genericsTypes = superType.getSuperType().getGenerics();
        if(V.notEmpty(genericsTypes) && genericsTypes.length >= 2){
            log.debug("Entity-Service: {} -> {}", genericsTypes[1].toString(), currentClass.getName());
            return genericsTypes[1].toString();
        }
        else{
            return null;
        }
    }

}