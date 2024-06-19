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
package com.diboot.file.excel.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.file.excel.annotation.ExcelBindDict;
import com.diboot.file.excel.annotation.ExcelBindField;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 绑定注解的辅助类
 * @author jerryma@dibo.ltd
 * @version v2.1.0
 * @date 2020/07/01
 */
@Slf4j
public class ExcelBindAnnoHandler {
    /**
     * 注解缓存
     */
    private static final Map<String, Map<String, Annotation>> MODEL_BINDANNO_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取字段-绑定注解之间的map
     * @param modelClass
     * @return
     */
    public static Map<String, Annotation> getField2BindAnnoMap(Class modelClass){
        String modelClassName = modelClass.getName();
        Map<String, Annotation> field2AnnoMap = MODEL_BINDANNO_CACHE.get(modelClassName);
        if(field2AnnoMap == null){
            List<Field> fieldList = BeanUtils.extractAllFields(modelClass, true);
            for(Field field : fieldList){
                ExcelBindDict excelBindDict = field.getAnnotation(ExcelBindDict.class);
                BindDict bindDict = field.getAnnotation(BindDict.class);
                if(excelBindDict != null){
                    if(field2AnnoMap == null){
                        field2AnnoMap = new LinkedHashMap<>(8);
                    }
                    field2AnnoMap.put(field.getName(), excelBindDict);
                }
                else if(bindDict != null) {
                    if(field2AnnoMap == null){
                        field2AnnoMap = new LinkedHashMap<>(8);
                    }
                    field2AnnoMap.put(field.getName(), bindDict);
                }
                ExcelBindField bindField = field.getAnnotation(ExcelBindField.class);
                if(bindField != null){
                    if(field2AnnoMap == null){
                        field2AnnoMap = new LinkedHashMap<>(8);
                    }
                    field2AnnoMap.put(field.getName(), bindField);
                }
            }
            if(field2AnnoMap == null){
                field2AnnoMap = Collections.emptyMap();
            }
            MODEL_BINDANNO_CACHE.put(modelClassName, field2AnnoMap);
        }
        return field2AnnoMap;
    }

    /**
     * 转换为name-value map
     * @param annotation
     * @param nameList
     * @return
     */
    public static Map<String, List> convertToNameValueMap(Annotation annotation, List<String> nameList){
        // 字典
        if(annotation instanceof ExcelBindDict || annotation instanceof BindDict){
            String dictType;
            if(annotation instanceof ExcelBindDict){
                dictType = ((ExcelBindDict)annotation).type();
            }
            else{
                dictType = ((BindDict)annotation).type();
            }
            DictionaryServiceExtProvider bindDictService = ContextHolder.getBean(DictionaryServiceExtProvider.class);
            if(bindDictService == null){
                throw new InvalidUsageException("exception.invalidUsage.excelBindAnnoHandler.convertToNameValueMap.message");
            }
            List<LabelValue> list = bindDictService.getLabelValueList(dictType);
            return convertLabelValueListToMap(list);
        }
        else if(annotation instanceof ExcelBindField){
            ExcelBindField bindField = (ExcelBindField)annotation;
            return executeBindField(bindField, nameList);
        }
        else{
            return Collections.emptyMap();
        }
    }

    /**
     * 执行绑定
     * @param bindField
     * @param nameList
     * @return
     */
    private static Map<String, List> executeBindField(ExcelBindField bindField, List<String> nameList){
        if(V.isEmpty(nameList)){
            return Collections.emptyMap();
        }
        BaseService service = ContextHolder.getBaseServiceByEntity(bindField.entity());
        String nameColumn = S.toSnakeCase(bindField.field());
        String idColumn = ContextHolder.getIdColumnName(bindField.entity());
        QueryWrapper queryWrapper = Wrappers.query().select(nameColumn, idColumn).in(nameColumn, nameList);
        List<LabelValue> list = service.getLabelValueList(queryWrapper);
        return convertLabelValueListToMap(list);
    }

    /**
     * 转换列表为map
     * @param list
     * @return
     */
    private static Map<String, List> convertLabelValueListToMap(List<LabelValue> list){
        Map<String, List> resultMap = new HashMap<>(list.size());
        if(V.notEmpty(list)){
            for(LabelValue labelValue : list){
                List mapVal = resultMap.get(labelValue.getLabel());
                if(mapVal == null){
                    mapVal = new ArrayList();
                    resultMap.put(labelValue.getLabel(), mapVal);
                }
                if(!mapVal.contains(labelValue.getValue())){
                    mapVal.add(labelValue.getValue());
                }
            }
        }
        return resultMap;
    }
}
