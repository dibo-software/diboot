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
package com.diboot.core.binding;

import com.diboot.core.binding.helper.DeepRelationsBinder;
import com.diboot.core.binding.helper.RelationsBindingManager;
import com.diboot.core.binding.helper.VirtualThreadExecutor;
import com.diboot.core.binding.parser.BindAnnotationGroup;
import com.diboot.core.binding.parser.FieldAnnotation;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.service.I18nConfigService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 关联关系绑定管理器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/7/18
 */
@SuppressWarnings("JavaDoc")
public class RelationsBinder {
    private static final Logger log = LoggerFactory.getLogger(RelationsBinder.class);

    private static RelationsBindingManager relationsBindingManager;
    private static RelationsBindingManager getBindingManager() {
        if(relationsBindingManager == null){
            relationsBindingManager = ContextHolder.getBean(RelationsBindingManager.class);
        }
        return relationsBindingManager;
    }

    /**
     * 自动转换和绑定单个VO中的注解关联（禁止循环调用，多个对象请调用convertAndBind(voList, voClass)）
     * @param voClass 需要转换的VO class
     * @param <E>
     * @param <VO>
     * @return
     */
    public static <E, VO> VO convertAndBind(E entity, Class<VO> voClass){
        // 转换为VO列表
        VO vo = BeanUtils.convert(entity, voClass);
        // 自动绑定关联对象
        bind(vo);
        return vo;
    }

    /**
     * 自动转换和绑定多个VO中的注解关联
     * @param entityList 需要转换的VO list
     * @param voClass VO class
     * @param <E>
     * @param <VO>
     * @return
     */
    public static <E, VO> List<VO> convertAndBind(List<E> entityList, Class<VO> voClass){
        // 转换为VO列表
        List<VO> voList = BeanUtils.convertList(entityList, voClass);
        // 自动绑定关联对象
        bind(voList);
        return voList;
    }

    /**
     * 自动绑定单个VO的关联对象（禁止循环调用，多个对象请调用bind(voList)）
     * @param vo 需要注解绑定的对象
     * @return
     * @throws Exception
     */
    public static <VO> void bind(VO vo){
        bind(Collections.singletonList(vo));
    }

    /**
     * 自动绑定多个VO集合的关联对象
     * @param voList 需要注解绑定的对象集合
     * @return
     * @throws Exception
     */
    public static <VO> void bind(List<VO> voList){
        bind(voList, true);
    }

    /**
     * 自动绑定多个VO集合的关联对象
     * @param voList 需要注解绑定的对象集合
     * @param enableDeepBind
     * @return
     * @throws Exception
     */
    public static <VO> void bind(List<VO> voList, boolean enableDeepBind){
        if(V.isEmpty(voList)){
            return;
        }
        // 获取VO类
        List<Class<?>> uniqueClassesList = new ArrayList<>();
        uniqueClassesList.add(voList.get(0).getClass());
        voList.forEach(vo -> {
            if(!uniqueClassesList.contains(vo.getClass())){
                uniqueClassesList.add(vo.getClass());
            }
        });
        if(uniqueClassesList.size() == 1) {
            bind(uniqueClassesList.get(0), voList, enableDeepBind);
        }
        else {
            for(Class<?> voClazz : uniqueClassesList){
                bind(voClazz, voList.stream().filter(vo->vo.getClass().equals(voClazz)).toList(), enableDeepBind);
            }
        }
    }

    /**
     * 自动绑定多个VO集合的关联对象
     * @param voList 需要注解绑定的对象集合
     * @param enableDeepBind
     * @return
     * @throws Exception
     */
    private static <VO> void bind(Class<?> voClass, List<VO> voList, boolean enableDeepBind){
        // 获取VO类
        BindAnnotationGroup bindAnnotationGroup = ParserCache.getBindAnnotationGroup(voClass);
        if(bindAnnotationGroup.isEmpty()){
            return;
        }
        RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);
        LocaleContextHolder.setLocaleContext(LocaleContextHolder.getLocaleContext(),true);
        RelationsBindingManager bindingManager = getBindingManager();
        SimpleAsyncTaskExecutor bindingExecutor = VirtualThreadExecutor.getVirtualThreadExecutor();
        List<Future<?>> binderFutures = bindingExecutor != null? new ArrayList<>() : null;
        // 绑定Field字段名
        Map<String, List<FieldAnnotation>> bindFieldGroupMap = bindAnnotationGroup.getBindFieldGroupMap();
        if(bindFieldGroupMap != null){
            for(Map.Entry<String, List<FieldAnnotation>> entry : bindFieldGroupMap.entrySet()){
                if(bindingExecutor != null) {
                    Future<?> bindFieldFuture = bindingExecutor.submit(() -> {
                        bindingManager.doBindingField(voList, entry.getValue());
                    });
                    binderFutures.add(bindFieldFuture);
                }
                else {
                    bindingManager.doBindingField(voList, entry.getValue());
                }
            }
        }
        // 绑定数据字典
        List<FieldAnnotation> dictAnnoList = bindAnnotationGroup.getBindDictAnnotations();
        if(dictAnnoList != null){
            if(bindAnnotationGroup.isRequireSequential() && bindingExecutor != null && V.notEmpty(binderFutures)){
                try {
                    for (Future<?> future : binderFutures) {
                        future.get();
                    }
                    binderFutures.clear();
                }
                catch (Exception e) {
                    log.error("虚拟线程执行绑定异常: ", e);
                }
            }
            for(FieldAnnotation annotation : dictAnnoList){
                if(bindingExecutor != null) {
                    Future<?> bindDictFuture = bindingExecutor.submit(() -> {
                        bindingManager.doBindingDict(voList, annotation);
                    });
                    binderFutures.add(bindDictFuture);
                }
                else {
                    bindingManager.doBindingDict(voList, annotation);
                }
            }
        }
        // 绑定Entity实体
        List<FieldAnnotation> entityAnnoList = bindAnnotationGroup.getBindEntityAnnotations();
        if(entityAnnoList != null){
            for(FieldAnnotation anno : entityAnnoList){
                if(bindingExecutor != null) {
                    Future<?> bindEntFuture = bindingExecutor.submit(() -> {
                        bindingManager.doBindingEntity(voList, anno);
                    });
                    binderFutures.add(bindEntFuture);
                }
                else {
                    bindingManager.doBindingEntity(voList, anno);
                }
            }
        }
        // 绑定Entity实体List
        List<FieldAnnotation> entitiesAnnoList = bindAnnotationGroup.getBindEntityListAnnotations();
        if(entitiesAnnoList != null){
            for(FieldAnnotation anno : entitiesAnnoList){
                if(bindingExecutor != null) {
                    Future<?> bindEntListFuture = bindingExecutor.submit(() -> {
                        bindingManager.doBindingEntityList(voList, anno);
                    });
                    binderFutures.add(bindEntListFuture);
                }
                else {
                    bindingManager.doBindingEntityList(voList, anno);
                }
            }
        }
        // 绑定Entity field List
        Map<String, List<FieldAnnotation>> bindFieldListGroupMap = bindAnnotationGroup.getBindFieldListGroupMap();
        if(bindFieldListGroupMap != null){
            // 解析条件并且执行绑定
            for(Map.Entry<String, List<FieldAnnotation>> entry : bindFieldListGroupMap.entrySet()){
                if(bindingExecutor != null) {
                    Future<?> bindFldListFuture = bindingExecutor.submit(() -> {
                        bindingManager.doBindingFieldList(voList, entry.getValue());
                    });
                    binderFutures.add(bindFldListFuture);
                }
                else {
                    bindingManager.doBindingFieldList(voList, entry.getValue());
                }
            }
        }
        // 绑定count子项计数
        List<FieldAnnotation> countAnnoList = bindAnnotationGroup.getBindCountAnnotations();
        if(countAnnoList != null){
            for(FieldAnnotation anno : countAnnoList){
                // 绑定关联对象count计数
                if(bindingExecutor != null) {
                    Future<?> bindCountFuture = bindingExecutor.submit(() -> {
                        bindingManager.doBindingCount(voList, anno);
                    });
                    binderFutures.add(bindCountFuture);
                }
                else {
                    bindingManager.doBindingCount(voList, anno);
                }
            }
        }
        // 开启国际化
        if(isEnableI18N()) {
            // 绑定国际化翻译
            List<FieldAnnotation> i18nAnnoList = bindAnnotationGroup.getBindI18nAnnotations();
            if(i18nAnnoList != null){
                for(FieldAnnotation anno : i18nAnnoList){
                    if(bindingExecutor != null) {
                        Future<?> bindI18nFuture = bindingExecutor.submit(() -> {
                            bindingManager.doBindingI18n(voList, anno);
                        });
                        binderFutures.add(bindI18nFuture);
                    }
                    else {
                        bindingManager.doBindingI18n(voList, anno);
                    }
                }
            }
        }
        if(bindingExecutor != null) {
            if(V.notEmpty(binderFutures)) {
                try {
                    for (Future<?> future : binderFutures) {
                        future.get();
                    }
                }
                catch (Exception e) {
                    log.error("虚拟线程汇总执行绑定异常: ", e);
                }
            }
            log.debug("虚拟线程执行关联绑定完成 <-=");
        }
        // 深度绑定
        if(enableDeepBind){
            List<FieldAnnotation> deepBindEntityAnnoList = bindAnnotationGroup.getDeepBindEntityAnnotations();
            List<FieldAnnotation> deepBindEntitiesAnnoList = bindAnnotationGroup.getDeepBindEntityListAnnotations();
            if(deepBindEntityAnnoList != null || deepBindEntitiesAnnoList != null){
                if(V.notEmpty(deepBindEntityAnnoList)){
                    FieldAnnotation firstAnnotation = deepBindEntityAnnoList.get(0);
                    log.debug("执行深度绑定: {}({}) for field {}", firstAnnotation.getAnnotation().annotationType().getSimpleName(),
                            firstAnnotation.getFieldClass().getSimpleName(), firstAnnotation.getFieldName());
                }
                if(deepBindEntitiesAnnoList != null) {
                    FieldAnnotation firstAnnotation = deepBindEntitiesAnnoList.get(0);
                    log.debug("执行深度绑定: {}({}) for field {}", firstAnnotation.getAnnotation().annotationType().getSimpleName(),
                            firstAnnotation.getFieldClass().getSimpleName(), firstAnnotation.getFieldName());
                }
                DeepRelationsBinder.deepBind(voList, deepBindEntityAnnoList, deepBindEntitiesAnnoList);
            }
        }
    }

    /**
     * 是否启用 i18n
     * @return
     */
    private static Boolean ENABLE_I18N = null;
    private static boolean isEnableI18N() {
        if(ENABLE_I18N == null){
            ENABLE_I18N = ContextHolder.getBean(I18nConfigService.class) != null;
            if(ENABLE_I18N){
                log.info("启用 i8n 国际化翻译转换");
            }
        }
        return ENABLE_I18N;
    }

}
