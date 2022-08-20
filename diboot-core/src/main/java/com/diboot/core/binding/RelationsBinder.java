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
package com.diboot.core.binding;

import com.diboot.core.binding.binder.parallel.ParallelBindingManager;
import com.diboot.core.binding.helper.DeepRelationsBinder;
import com.diboot.core.binding.parser.BindAnnotationGroup;
import com.diboot.core.binding.parser.FieldAnnotation;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 关联关系绑定管理器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/7/18
 */
@SuppressWarnings("JavaDoc")
public class RelationsBinder {
    private static final Logger log = LoggerFactory.getLogger(RelationsBinder.class);

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
        Class<?> voClass = voList.get(0).getClass();
        BindAnnotationGroup bindAnnotationGroup = ParserCache.getBindAnnotationGroup(voClass);
        if(bindAnnotationGroup.isEmpty()){
            return;
        }
        RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);
        ParallelBindingManager parallelBindingManager = ContextHelper.getBean(ParallelBindingManager.class);
        // 不可能出现的错误，但是编译器需要
        assert parallelBindingManager != null;
        List<CompletableFuture<Boolean>> binderFutures = new ArrayList<>();
        // 绑定Field字段名
        Map<String, List<FieldAnnotation>> bindFieldGroupMap = bindAnnotationGroup.getBindFieldGroupMap();
        if(bindFieldGroupMap != null){
            for(Map.Entry<String, List<FieldAnnotation>> entry : bindFieldGroupMap.entrySet()){
                CompletableFuture<Boolean> bindFieldFuture = parallelBindingManager.doBindingField(voList, entry.getValue());
                binderFutures.add(bindFieldFuture);
            }
        }
        // 绑定数据字典
        List<FieldAnnotation> dictAnnoList = bindAnnotationGroup.getBindDictAnnotations();
        if(dictAnnoList != null){
            if(bindAnnotationGroup.isRequireSequential()){
                CompletableFuture.allOf(binderFutures.toArray(new CompletableFuture[0])).join();
            }
            for(FieldAnnotation annotation : dictAnnoList){
                CompletableFuture<Boolean> bindDictFuture = parallelBindingManager.doBindingDict(voList, annotation);
                binderFutures.add(bindDictFuture);
            }
        }
        // 绑定Entity实体
        List<FieldAnnotation> entityAnnoList = bindAnnotationGroup.getBindEntityAnnotations();

        if(entityAnnoList != null){
            for(FieldAnnotation anno : entityAnnoList){
                // 绑定关联对象entity
                CompletableFuture<Boolean> bindEntFuture = parallelBindingManager.doBindingEntity(voList, anno);
                binderFutures.add(bindEntFuture);
            }
        }
        // 绑定Entity实体List
        List<FieldAnnotation> entitiesAnnoList = bindAnnotationGroup.getBindEntityListAnnotations();
        if(entitiesAnnoList != null){
            for(FieldAnnotation anno : entitiesAnnoList){
                // 绑定关联对象entity
                CompletableFuture<Boolean> bindEntFuture = parallelBindingManager.doBindingEntityList(voList, anno);
                binderFutures.add(bindEntFuture);
            }
        }
        // 绑定Entity field List
        Map<String, List<FieldAnnotation>> bindFieldListGroupMap = bindAnnotationGroup.getBindFieldListGroupMap();
        if(bindFieldListGroupMap != null){
            // 解析条件并且执行绑定
            for(Map.Entry<String, List<FieldAnnotation>> entry : bindFieldListGroupMap.entrySet()){
                CompletableFuture<Boolean> bindFieldFuture = parallelBindingManager.doBindingFieldList(voList, entry.getValue());
                binderFutures.add(bindFieldFuture);
            }
        }
        // 绑定count子项计数
        List<FieldAnnotation> countAnnoList = bindAnnotationGroup.getBindCountAnnotations();
        if(countAnnoList != null){
            for(FieldAnnotation anno : countAnnoList){
                // 绑定关联对象count计数
                CompletableFuture<Boolean> bindCountFuture = parallelBindingManager.doBindingCount(voList, anno);
                binderFutures.add(bindCountFuture);
            }
        }
        // 执行绑定
        CompletableFuture.allOf(binderFutures.toArray(new CompletableFuture[0])).join();
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

}
