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
package com.diboot.core.binding.binder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.config.Cons;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关联字段绑定
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/19
 */
public class FieldListBinder<T> extends FieldBinder<T> {
    private static final Logger log = LoggerFactory.getLogger(FieldListBinder.class);

    /***
     * 构造方法
     * @param serviceInstance
     * @param voList
     */
    public FieldListBinder(IService<T> serviceInstance, List voList) {
        super(serviceInstance, voList);
    }

    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(V.isEmpty(refObjJoinCols)){
            log.warn("调用错误：无法从condition中解析出字段关联.");
            return;
        }
        if(referencedGetterFieldNameList == null){
            log.error("调用错误：字段绑定必须指定字段field");
            return;
        }
        Map<String, List> valueEntityListMap = new HashMap<>();
        // 直接关联
        if(middleTable == null){
            super.simplifySelectColumns();
            super.buildQueryWrapperJoinOn();
            // 查询entity列表: List<Role>
            List<T> list = getEntityList(queryWrapper);
            if(V.notEmpty(list)){
                valueEntityListMap = this.buildMatchKey2FieldListMap(list);
            }
            // 遍历list并赋值
            bindPropValue(annoObjectList, annoObjJoinCols, valueEntityListMap);
        }
        // 通过中间表关联
        else{
            if(refObjJoinCols.size() > 1){
                throw new BusinessException(NOT_SUPPORT_MSG);
            }
            // 提取注解条件中指定的对应的列表
            Map<String, List> trunkObjCol2ValuesMap = super.buildTrunkObjCol2ValuesMap();
            // 处理中间表, 将结果转换成map
            Map<String, List> middleTableResultMap = middleTable.executeOneToManyQuery(trunkObjCol2ValuesMap);
            if(V.isEmpty(middleTableResultMap)){
                return;
            }
            super.simplifySelectColumns();
            // 收集查询结果values集合
            List entityIdList = extractIdValueFromMap(middleTableResultMap);
            // 构建查询条件
            queryWrapper.in(refObjJoinCols.get(0), entityIdList);
            // 查询entity列表: List<Role>
            List<T> list = getEntityList(queryWrapper);
            if(V.isEmpty(list)){
                return;
            }
            String refObjJoinOnField = toRefObjField(refObjJoinCols.get(0));
            // 转换entity列表为Map<ID, Entity>
            Map<String, T> entityMap = BeanUtils.convertToStringKeyObjectMap(list, refObjJoinOnField);
            for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
                // List<roleId>
                List annoObjFKList = entry.getValue();
                if(V.isEmpty(annoObjFKList)){
                    continue;
                }
                List valueList = new ArrayList();
                for(Object obj : annoObjFKList){
                    T ent = entityMap.get(String.valueOf(obj));
                    if(ent != null){
                        valueList.add(ent);
                    }
                }
                valueEntityListMap.put(entry.getKey(), valueList);
            }
            // 遍历list并赋值
            bindPropValue(annoObjectList, middleTable.getTrunkObjColMapping(), valueEntityListMap);
        }
    }

    /***
     * 从对象集合提取某个属性值到list中
     * @param fromList
     * @param annoObjJoinCols
     * @param valueMatchMap
     * @param <E>
     */
    public <E> void bindPropValue(List<E> fromList, List<String> annoObjJoinCols, Map<String, List> valueMatchMap){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        try{
            for(E object : fromList){
                sb.setLength(0);
                for(int i=0; i<annoObjJoinCols.size(); i++){
                    String col = annoObjJoinCols.get(i);
                    String val = BeanUtils.getStringProperty(object, toAnnoObjField(col));
                    if(i>0){
                        sb.append(Cons.SEPARATOR_COMMA);
                    }
                    sb.append(val);
                }
                // 查找匹配Key
                String matchKey = sb.toString();
                List entityList = valueMatchMap.get(matchKey);
                if(entityList != null){
                    // 赋值
                    for(int i = 0; i< annoObjectSetterPropNameList.size(); i++){
                        List valObjList = BeanUtils.collectToList(entityList, referencedGetterFieldNameList.get(i));
                        BeanUtils.setProperty(object, annoObjectSetterPropNameList.get(i), valObjList);
                    }
                }
            }
            sb.setLength(0);
        }
        catch (Exception e){
            log.warn("设置属性值异常", e);
        }
    }

    /***
     * 从对象集合提取某个属性值到list中
     * @param fromList
     * @param trunkObjColMapping
     * @param valueMatchMap
     * @param <E>
     */
    public <E> void bindPropValue(List<E> fromList, Map<String, String> trunkObjColMapping, Map<String, List> valueMatchMap){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        boolean appendComma = false;
        try{
            for(E object : fromList){
                sb.setLength(0);
                for(Map.Entry<String, String> entry :trunkObjColMapping.entrySet()){
                    String getterField = toAnnoObjField(entry.getKey());
                    String fieldValue = BeanUtils.getStringProperty(object, getterField);
                    if(appendComma){
                        sb.append(Cons.SEPARATOR_COMMA);
                    }
                    if(appendComma == false){
                        appendComma = true;
                    }
                    sb.append(fieldValue);
                }
                // 查找匹配Key
                List entityList = valueMatchMap.get(sb.toString());
                if(entityList != null){
                    // 赋值
                    for(int i = 0; i< annoObjectSetterPropNameList.size(); i++){
                        List valObjList = BeanUtils.collectToList(entityList, referencedGetterFieldNameList.get(i));
                        BeanUtils.setProperty(object, annoObjectSetterPropNameList.get(i), valObjList);
                    }
                }
            }
        }
        catch (Exception e){
            log.warn("设置属性值异常", e);
        }
    }

    /**
     * 构建匹配key-entity目标的map
     * @param list
     * @return
     */
    private Map<String, List> buildMatchKey2FieldListMap(List<T> list){
        Map<String, List> key2TargetListMap = new HashMap<>(list.size());
        StringBuilder sb = new StringBuilder();
        for(T entity : list){
            sb.setLength(0);
            for(int i=0; i<refObjJoinCols.size(); i++){
                String refObjJoinOnCol = refObjJoinCols.get(i);
                String fldValue = BeanUtils.getStringProperty(entity, toRefObjField(refObjJoinOnCol));
                if(i > 0){
                    sb.append(Cons.SEPARATOR_COMMA);
                }
                sb.append(fldValue);
            }
            String matchKey = sb.toString();
            // 获取list
            List entityList = key2TargetListMap.get(matchKey);
            if(entityList == null){
                entityList = new ArrayList<>();
                key2TargetListMap.put(matchKey, entityList);
            }
            entityList.add(entity);
        }
        sb.setLength(0);
        return key2TargetListMap;
    }

}
