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

import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.annotation.*;
import com.diboot.core.binding.binder.*;
import com.diboot.core.binding.helper.DeepRelationsBinder;
import com.diboot.core.binding.parser.BindAnnotationGroup;
import com.diboot.core.binding.parser.ConditionManager;
import com.diboot.core.binding.parser.FieldAnnotation;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关联关系绑定管理器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/7/18
 */
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
        List<VO> voList = new ArrayList<>(1);
        voList.add(vo);
        bind(voList);
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
        Class voClass = voList.get(0).getClass();
        BindAnnotationGroup bindAnnotationGroup = ParserCache.getBindAnnotationGroup(voClass);
        if(bindAnnotationGroup.isNotEmpty()){
            // 绑定数据字典
            List<FieldAnnotation> dictAnnoList = bindAnnotationGroup.getBindDictAnnotations();
            if(dictAnnoList != null){
                for(FieldAnnotation annotation : dictAnnoList){
                    doBindingDict(voList, annotation);
                }
            }
            // 绑定Field字段名
            List<FieldAnnotation> fieldAnnoList = bindAnnotationGroup.getBindFieldAnnotations();
            if(fieldAnnoList != null){
                doBindingField(voList, fieldAnnoList);
            }
            // 绑定Entity实体
            List<FieldAnnotation> entityAnnoList = bindAnnotationGroup.getBindEntityAnnotations();
            List<FieldAnnotation> deepBindEntityAnnoList = null;
            if(entityAnnoList != null){
                for(FieldAnnotation anno : entityAnnoList){
                    doBindingEntity(voList, anno);
                    if(enableDeepBind && ((BindEntity)anno.getAnnotation()).deepBind()){
                        if(deepBindEntityAnnoList == null){
                            deepBindEntityAnnoList = new ArrayList<>();
                        }
                        deepBindEntityAnnoList.add(anno);
                    }
                }
            }
            // 绑定Entity实体List
            List<FieldAnnotation> entitiesAnnoList = bindAnnotationGroup.getBindEntityListAnnotations();
            List<FieldAnnotation> deepBindEntitiesAnnoList = null;
            if(entitiesAnnoList != null){
                for(FieldAnnotation anno : entitiesAnnoList){
                    doBindingEntityList(voList, anno);
                    if(enableDeepBind && ((BindEntityList)anno.getAnnotation()).deepBind()){
                        if(deepBindEntitiesAnnoList == null){
                            deepBindEntitiesAnnoList = new ArrayList<>();
                        }
                        deepBindEntitiesAnnoList.add(anno);
                    }
                }
            }
            // 绑定Entity field List
            List<FieldAnnotation> fieldListAnnoList = bindAnnotationGroup.getBindFieldListAnnotations();
            if(fieldListAnnoList != null){
                doBindingFieldList(voList, fieldListAnnoList);
            }
            // 深度绑定
            if(enableDeepBind && (deepBindEntityAnnoList != null || deepBindEntitiesAnnoList != null)){
                DeepRelationsBinder.deepBind(voList, deepBindEntityAnnoList, deepBindEntitiesAnnoList);
            }
        }
    }

    /***
     * 绑定数据字典
     * @param voList
     * @param fieldAnno
     * @param <VO>
     */
    private static <VO> void doBindingDict(List<VO> voList, FieldAnnotation fieldAnno) {
        DictionaryServiceExtProvider bindDictService = ContextHelper.getBean(DictionaryServiceExtProvider.class);
        if(bindDictService != null){
            BindDict annotation = (BindDict) fieldAnno.getAnnotation();
            String dictValueField = annotation.field();
            if(V.isEmpty(dictValueField)){
                dictValueField = S.replace(fieldAnno.getFieldName(), "Label", "");
            }
            // 字典绑定接口化
            bindDictService.bindItemLabel(voList, fieldAnno.getFieldName(), dictValueField, annotation.type());
        }
        else{
            throw new BusinessException(Status.FAIL_SERVICE_UNAVAILABLE, "BindDictService未实现，无法使用BindDict注解！");
        }
    }

    /***
     * 绑定字段
     * @param voList
     * @param fieldAnnoList
     * @param <VO>
     */
    private static <VO> void doBindingField(List<VO> voList, List<FieldAnnotation> fieldAnnoList) {
        //多个字段，合并查询，以减少SQL数
        Map<String, List<FieldAnnotation>> clazzToListMap = new HashMap<>();
        for(FieldAnnotation anno : fieldAnnoList){
            BindField bindField = (BindField) anno.getAnnotation();
            String key = bindField.entity().getName() + ":" + bindField.condition();
            List<FieldAnnotation> list = clazzToListMap.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(anno);
        }
        // 解析条件并且执行绑定
        for(Map.Entry<String, List<FieldAnnotation>> entry : clazzToListMap.entrySet()){
            List<FieldAnnotation> list = entry.getValue();
            BindField bindAnnotation = (BindField) list.get(0).getAnnotation();
            FieldBinder binder = buildFieldBinder(bindAnnotation, voList);
            for(FieldAnnotation anno : list){
                BindField bindField = (BindField) anno.getAnnotation();
                binder.link(bindField.field(), anno.getFieldName());
            }
            parseConditionsAndBinding(binder, bindAnnotation.condition());
        }
    }

    /***
     * 绑定Entity
     * @param voList
     * @param fieldAnnotation
     * @param <VO>
     */
    private static <VO> void doBindingEntity(List<VO> voList, FieldAnnotation fieldAnnotation) {
        BindEntity annotation = (BindEntity) fieldAnnotation.getAnnotation();
        // 绑定关联对象entity
        EntityBinder binder = buildEntityBinder(annotation, voList);
        if(binder != null){
            // 构建binder
            binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
            // 解析条件并且执行绑定
            parseConditionsAndBinding(binder, annotation.condition());
        }
    }

    /***
     * 绑定EntityList
     * @param voList
     * @param fieldAnnotation
     * @param <VO>
     */
    private static <VO> void doBindingEntityList(List<VO> voList, FieldAnnotation fieldAnnotation) {
        BindEntityList bindAnnotation = (BindEntityList) fieldAnnotation.getAnnotation();
        // 构建binder
        EntityListBinder binder = buildEntityListBinder(bindAnnotation, voList);
        if(binder != null){
            binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
            // 解析条件并且执行绑定
            parseConditionsAndBinding(binder, bindAnnotation.condition(), bindAnnotation.orderBy());
        }
    }

    /***
     * 绑定FieldList
     * @param voList
     * @param fieldListAnnoList
     * @param <VO>
     */
    private static <VO> void doBindingFieldList(List<VO> voList, List<FieldAnnotation> fieldListAnnoList) {
        //多个字段，合并查询，以减少SQL数
        Map<String, List<FieldAnnotation>> clazzToListMap = new HashMap<>();
        for(FieldAnnotation anno : fieldListAnnoList){
            BindFieldList bindField = (BindFieldList) anno.getAnnotation();
            String key = bindField.entity().getName() + ":" + bindField.condition();
            List<FieldAnnotation> list = clazzToListMap.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(anno);
        }
        // 解析条件并且执行绑定
        for(Map.Entry<String, List<FieldAnnotation>> entry : clazzToListMap.entrySet()){
            List<FieldAnnotation> list = entry.getValue();
            BindFieldList bindAnnotation = (BindFieldList) list.get(0).getAnnotation();
            FieldListBinder binder = buildFieldListBinder(bindAnnotation, voList);
            for(FieldAnnotation anno : list){
                BindFieldList bindField = (BindFieldList) anno.getAnnotation();
                binder.link(bindField.field(), anno.getFieldName());
            }
            parseConditionsAndBinding(binder, bindAnnotation.condition());
        }
    }

    /***
     * 解析条件并且执行绑定
     * @param condition
     * @param binder
     */
    private static void parseConditionsAndBinding(BaseBinder binder, String condition){
        try{
            ConditionManager.parseConditions(condition, binder);
            binder.bind();
        }
        catch (Exception e){
            log.error("解析注解条件与绑定执行异常", e);
        }
    }

    /***
     * 解析条件并且执行绑定
     * @param condition
     * @param binder
     * @param orderBy
     */
    private static void parseConditionsAndBinding(BaseBinder binder, String condition, String orderBy){
        try{
            ConditionManager.parseConditions(condition, binder);
            ((EntityListBinder)binder).setOrderBy(orderBy);
            binder.bind();
        }
        catch (Exception e){
            log.error("解析注解条件与绑定执行异常", e);
        }
    }

    /**
     * 构建FieldBinder
     * @param annotation
     * @param voList
     * @return
     */
    private static FieldBinder buildFieldBinder(Annotation annotation, List voList){
        IService service = getService(annotation);
        if(service != null){
            return new FieldBinder<>(service, voList);
        }
        return null;
    }

    /**
     * 构建EntityBinder
     * @param annotation
     * @param voList
     * @return
     */
    private static EntityBinder buildEntityBinder(Annotation annotation, List voList){
        IService service = getService(annotation);
        if(service != null){
            return new EntityBinder<>(service, voList);
        }
        return null;
    }

    /**
     * 构建EntityListBinder
     * @param annotation
     * @param voList
     * @return
     */
    private static EntityListBinder buildEntityListBinder(Annotation annotation, List voList){
        IService service = getService(annotation);
        if(service != null){
            return new EntityListBinder<>(service, voList);
        }
        return null;
    }

    /**
     * 构建FieldListBinder
     * @param annotation
     * @param voList
     * @return
     */
    private static FieldListBinder buildFieldListBinder(Annotation annotation, List voList){
        IService service = getService(annotation);
        if(service != null){
            return new FieldListBinder<>(service, voList);
        }
        return null;
    }

    /**
     * 通过Entity获取对应的Service实现类
     * @param annotation
     * @return
     */
    private static IService getService(Annotation annotation){
        Class<?> entityClass = null;
        if(annotation instanceof BindDict){
            entityClass = Dictionary.class;
        }
        else if(annotation instanceof BindField){
            BindField bindAnnotation = (BindField)annotation;
            entityClass = bindAnnotation.entity();
        }
        else if(annotation instanceof BindEntity){
            BindEntity bindAnnotation = (BindEntity)annotation;
            entityClass = bindAnnotation.entity();
        }
        else if(annotation instanceof BindEntityList){
            BindEntityList bindAnnotation = (BindEntityList)annotation;
            entityClass = bindAnnotation.entity();
        }
        else if(annotation instanceof BindFieldList){
            BindFieldList bindAnnotation = (BindFieldList)annotation;
            entityClass = bindAnnotation.entity();
        }
        else{
            log.warn("非预期的注解: "+ annotation.getClass().getSimpleName());
            return null;
        }
        // 根据entity获取Service
        return ContextHelper.getIServiceByEntity(entityClass);
    }

}
