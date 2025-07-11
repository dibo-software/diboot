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
package com.diboot.core.binding.helper;

import com.diboot.core.binding.annotation.*;
import com.diboot.core.binding.binder.*;
import com.diboot.core.binding.parser.ConditionManager;
import com.diboot.core.binding.parser.FieldAnnotation;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.core.service.I18nConfigService;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关联关系绑定Manager
 * @author JerryMa
 * @version v3.6.0
 * @date 2024/1/8
 * Copyright © diboot.com
 */
@Slf4j
@Component
public class RelationsBindingManager {

    @Autowired(required = false)
    private DictionaryServiceExtProvider dictionaryServiceExtProvider;

    @Autowired(required = false)
    private I18nConfigService i18nConfigService;

    /**
     * 绑定字典
     * @param voList
     * @param fieldAnno
     * @return
     */
    public void doBindingDict(List voList, FieldAnnotation fieldAnno){
        if(dictionaryServiceExtProvider != null){
            BindDict annotation = (BindDict) fieldAnno.getAnnotation();
            String dictValueField = annotation.field();
            if(V.isEmpty(dictValueField)){
                dictValueField = S.replace(fieldAnno.getFieldName(), "Label", "");
                log.debug("BindDict未指定field，将默认取值为: {}", dictValueField);
            }
            // 字典绑定接口化
            dictionaryServiceExtProvider.bindItemLabel(voList, fieldAnno.getFieldName(), dictValueField, annotation.type());
        }
        else{
            log.warn("BindDictService未实现，无法使用BindDict注解！");
        }
    }

    /**
     * 绑定Field
     * @param voList
     * @param fieldAnnotations
     */
    public void doBindingField(List voList, List<FieldAnnotation> fieldAnnotations){
        BindField bindAnnotation = (BindField) fieldAnnotations.get(0).getAnnotation();
        if(voList.size() <= BaseConfig.getBatchSize()) {
            doBindingFieldPartition(voList, fieldAnnotations, bindAnnotation);
        }
        else {// 需要分批
            List<List> allBatchList = ListUtils.partition(voList, BaseConfig.getBatchSize());
            log.debug("@BindField 待绑定数据过多:{}，分 {} 批次执行", voList.size(), allBatchList.size());
            for(List batchList : allBatchList) {
                doBindingFieldPartition(batchList, fieldAnnotations, bindAnnotation);
            }
        }
    }

    /**
     * 单批次执行字段绑定
     * @param voList
     * @param fieldAnnotations
     * @param bindAnnotation
     */
    private static void doBindingFieldPartition(List voList, List<FieldAnnotation> fieldAnnotations, BindField bindAnnotation) {
        FieldBinder binder = new FieldBinder(bindAnnotation, voList);
        for(FieldAnnotation anno : fieldAnnotations){
            BindField bindField = (BindField) anno.getAnnotation();
            binder.link(bindField.field(), anno.getFieldName());
        }
        // 解析条件并且执行绑定
        ConditionManager.parseConditions(bindAnnotation.condition(), binder);
        binder.bind();
    }

    /**
     * 绑定FieldList
     * @param voList
     * @param fieldAnnotations
     */
    public void doBindingFieldList(List voList, List<FieldAnnotation> fieldAnnotations){
        BindFieldList bindAnnotation = (BindFieldList) fieldAnnotations.get(0).getAnnotation();
        if(voList.size() <= BaseConfig.getBatchSize()) {
            doBindingFieldListPartition(voList, fieldAnnotations, bindAnnotation);
        }
        else {// 需要分批
            List<List> allBatchList = ListUtils.partition(voList, BaseConfig.getBatchSize());
            log.debug("@BindFieldList 待绑定数据过多:{}，分 {} 批次执行", voList.size(), allBatchList.size());
            for(List batchList : allBatchList) {
                doBindingFieldListPartition(batchList, fieldAnnotations, bindAnnotation);
            }
        }
    }

    /**
     * 单次执行字段list绑定
     * @param voList
     * @param fieldAnnotations
     * @param bindAnnotation
     */
    private static void doBindingFieldListPartition(List voList, List<FieldAnnotation> fieldAnnotations, BindFieldList bindAnnotation) {
        FieldListBinder binder = new FieldListBinder(bindAnnotation, voList);
        for(FieldAnnotation anno : fieldAnnotations){
            BindFieldList bindField = (BindFieldList) anno.getAnnotation();
            binder.link(bindField.field(), anno.getFieldName());
        }
        // 解析条件并且执行绑定
        ConditionManager.parseConditions(bindAnnotation.condition(), binder);
        binder.bind();
    }

    /**
     * 绑定Entity
     * @param voList
     * @param fieldAnnotation
     */
    public void doBindingEntity(List voList, FieldAnnotation fieldAnnotation) {
        BindEntity annotation = (BindEntity) fieldAnnotation.getAnnotation();
        if(voList.size() <= BaseConfig.getBatchSize()) {
            doBindingEntityPartition(voList, fieldAnnotation, annotation);
        }
        else {// 需要分批
            List<List> allBatchList = ListUtils.partition(voList, BaseConfig.getBatchSize());
            log.debug("@BindEntity 待绑定数据过多:{}，分 {} 批次执行", voList.size(), allBatchList.size());
            for(List batchList : allBatchList) {
                doBindingEntityPartition(batchList, fieldAnnotation, annotation);
            }
        }
    }

    /**
     * 分批绑定entity
     * @param voList
     * @param fieldAnnotation
     * @param annotation
     */
    private static void doBindingEntityPartition(List voList, FieldAnnotation fieldAnnotation, BindEntity annotation) {
        // 绑定关联对象entity
        EntityBinder binder = new EntityBinder(annotation, voList);
        // 构建binder
        binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
        // 解析条件并且执行绑定
        ConditionManager.parseConditions(annotation.condition(), binder);
        binder.bind();
    }

    /**
     * 绑定EntityList
     * @param voList
     * @param fieldAnnotation
     */
    public void doBindingEntityList(List voList, FieldAnnotation fieldAnnotation) {
        BindEntityList annotation = (BindEntityList) fieldAnnotation.getAnnotation();
        if(voList.size() <= BaseConfig.getBatchSize()) {
            doBindingEntityListPartition(voList, fieldAnnotation, annotation);
        }
        else {// 需要分批
            List<List> allBatchList = ListUtils.partition(voList, BaseConfig.getBatchSize());
            log.debug("@BindEntityList 待绑定数据过多:{}，分 {} 批次执行", voList.size(), allBatchList.size());
            for(List batchList : allBatchList) {
                doBindingEntityListPartition(batchList, fieldAnnotation, annotation);
            }
        }
    }

    /**
     * 分批次绑定EntityList
     * @param voList
     * @param fieldAnnotation
     * @param annotation
     */
    private static void doBindingEntityListPartition(List voList, FieldAnnotation fieldAnnotation, BindEntityList annotation) {
        // 构建binder
        EntityListBinder binder = new EntityListBinder(annotation, voList);
        binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
        // 解析条件并且执行绑定
        ConditionManager.parseConditions(annotation.condition(), binder);
        binder.bind();
    }

    /**
     * 绑定count计数
     * @param voList
     * @param fieldAnnotation
     */
    public void doBindingCount(List voList, FieldAnnotation fieldAnnotation) {
        BindCount annotation = (BindCount) fieldAnnotation.getAnnotation();
        if(voList.size() <= BaseConfig.getBatchSize()) {
            doBindingCountPartition(voList, fieldAnnotation, annotation);
        }
        else {// 需要分批
            List<List> allBatchList = ListUtils.partition(voList, BaseConfig.getBatchSize());
            log.debug("@BindEntityList 待绑定数据过多:{}，分 {} 批次执行", voList.size(), allBatchList.size());
            for(List batchList : allBatchList) {
                doBindingCountPartition(batchList, fieldAnnotation, annotation);
            }
        }
    }

    /**
     * 分批次绑定count计数
     * @param voList
     * @param fieldAnnotation
     * @param annotation
     */
    private static void doBindingCountPartition(List voList, FieldAnnotation fieldAnnotation, BindCount annotation) {
        // 绑定关联对象entity
        CountBinder binder = new CountBinder(annotation, voList);
        // 构建binder
        binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
        // 解析条件并且执行绑定
        ConditionManager.parseConditions(annotation.condition(), binder);
        binder.bind();
    }

    /**
     * 绑定国际化
     *
     * @param voList
     * @param fieldAnnotation
     */
    public void doBindingI18n(List voList, FieldAnnotation fieldAnnotation) {
        BindI18n annotation = (BindI18n) fieldAnnotation.getAnnotation();
        String i18nCodeField = annotation.value();
        if (i18nConfigService != null) {
            // 国际化绑定接口化
            i18nConfigService.bindI18nContent(voList, i18nCodeField, fieldAnnotation.getFieldName());
        } else {
            log.warn("I18nConfigService未初始化，无法翻译I18n注解: {}", i18nCodeField);
        }
    }

}