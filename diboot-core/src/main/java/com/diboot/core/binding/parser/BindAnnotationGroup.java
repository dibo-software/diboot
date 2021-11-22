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
package com.diboot.core.binding.parser;

import com.diboot.core.binding.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VO绑定注解的归类分组，用于缓存解析后的结果
 * @author mazc@dibo.ltd<br>
 * @version 2.0<br>
 * @date 2019/04/03 <br>
 */
@Slf4j
public class BindAnnotationGroup {
    /**
     * Dictionary注解
     */
    private List<FieldAnnotation> bindDictAnnotations;
    /**
     * BindField分组Map
     */
    private Map<String, List<FieldAnnotation>> bindFieldGroupMap;
    /**
     * 实体关联注解
     */
    private List<FieldAnnotation> bindEntityAnnotations;
    /**
     * 实体集合关联注解
     */
    private List<FieldAnnotation> bindEntityListAnnotations;
    /**
     * BindFieldList分组Map
     */
    private Map<String, List<FieldAnnotation>> bindFieldListGroupMap;
    /**
     * 深度绑定实体
     */
    private List<FieldAnnotation> deepBindEntityAnnotations;
    /**
     * 深度绑定实体
     */
    private List<FieldAnnotation> deepBindEntityListAnnotations;

    /**
     * 添加注解
     * @param fieldName
     * @param annotation
     */
    public void addBindAnnotation(String fieldName, Class<?> fieldClass, Annotation annotation){
        if(annotation instanceof BindDict){
            if(bindDictAnnotations == null){
                bindDictAnnotations = new ArrayList<>(4);
            }
            bindDictAnnotations.add(new FieldAnnotation(fieldName, fieldClass, annotation));
            return;
        }
        String key = null;
        try {
            if (annotation instanceof BindField) {
                BindField bindField = (BindField) annotation;
                key = bindField.entity().getName() + ":" + bindField.condition();
            } else if (annotation instanceof BindFieldList) {
                BindFieldList bindField = (BindFieldList) annotation;
                key = bindField.entity().getName() + ":" + bindField.condition() + ":" + bindField.orderBy();
            } else if (annotation instanceof BindEntity) {
                BindEntity bindEntity = (BindEntity) annotation;
                key = bindEntity.entity().getName();
            } else if (annotation instanceof BindEntityList) {
                BindEntityList bindEntity = (BindEntityList) annotation;
                key = bindEntity.entity().getName();
            }
        } catch (Exception e) {
            log.warn("获取绑定信息异常", e);
            return;
        }
        if(annotation instanceof BindField){
            if(bindFieldGroupMap == null){
                bindFieldGroupMap = new HashMap<>(4);
            }
            List<FieldAnnotation> list = bindFieldGroupMap.computeIfAbsent(key, k -> new ArrayList<>(4));
            list.add(new FieldAnnotation(fieldName, fieldClass, annotation));
        }
        else if(annotation instanceof BindEntity){
            if(bindEntityAnnotations == null){
                bindEntityAnnotations = new ArrayList<>(4);
            }
            FieldAnnotation fieldAnno = new FieldAnnotation(fieldName, fieldClass, annotation);
            bindEntityAnnotations.add(fieldAnno);
            if(((BindEntity)annotation).deepBind()){
                if(deepBindEntityAnnotations == null){
                    deepBindEntityAnnotations = new ArrayList<>(4);
                }
                deepBindEntityAnnotations.add(fieldAnno);
            }
        }
        else if(annotation instanceof BindEntityList){
            if(bindEntityListAnnotations == null){
                bindEntityListAnnotations = new ArrayList<>(4);
            }
            FieldAnnotation fieldAnno = new FieldAnnotation(fieldName, fieldClass, annotation);
            bindEntityListAnnotations.add(fieldAnno);
            if(((BindEntityList)annotation).deepBind()){
                if(deepBindEntityListAnnotations == null){
                    deepBindEntityListAnnotations = new ArrayList<>(4);
                }
                deepBindEntityListAnnotations.add(fieldAnno);
            }
        }
        else if(annotation instanceof BindFieldList){
            //多个字段，合并查询，以减少SQL数
            if(bindFieldListGroupMap == null){
                bindFieldListGroupMap = new HashMap<>(4);
            }
            List<FieldAnnotation> list = bindFieldListGroupMap.computeIfAbsent(key, k -> new ArrayList<>(4));
            list.add(new FieldAnnotation(fieldName, fieldClass, annotation));
        }
    }

    public List<FieldAnnotation> getBindDictAnnotations() {
        return bindDictAnnotations;
    }

    public List<FieldAnnotation> getBindEntityAnnotations() {
        return bindEntityAnnotations;
    }

    public List<FieldAnnotation> getBindEntityListAnnotations() {
        return bindEntityListAnnotations;
    }

    public Map<String, List<FieldAnnotation>> getBindFieldGroupMap(){
        return bindFieldGroupMap;
    }

    public Map<String, List<FieldAnnotation>> getBindFieldListGroupMap(){
        return bindFieldListGroupMap;
    }

    public List<FieldAnnotation> getDeepBindEntityAnnotations() {
        return deepBindEntityAnnotations;
    }

    public List<FieldAnnotation> getDeepBindEntityListAnnotations() {
        return deepBindEntityListAnnotations;
    }

    public boolean isEmpty() {
        return bindDictAnnotations == null && bindFieldGroupMap == null && bindEntityAnnotations == null
                && bindEntityListAnnotations == null && bindFieldListGroupMap == null;
    }

}
