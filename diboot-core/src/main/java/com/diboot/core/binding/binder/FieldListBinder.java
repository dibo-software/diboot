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

import com.diboot.core.binding.annotation.BindFieldList;
import com.diboot.core.binding.binder.remote.RemoteBindingManager;
import com.diboot.core.binding.helper.ResultAssembler;
import com.diboot.core.config.Cons;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;

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
     * @param annotation
     * @param voList
     */
    public FieldListBinder(BindFieldList annotation, List voList) {
        super(annotation.entity(), voList);
        if(V.notEmpty(annotation.splitBy())){
            this.splitBy = annotation.splitBy();
        }
        if(V.notEmpty(annotation.orderBy())){
            this.orderBy = annotation.orderBy();
        }
    }

    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(V.isEmpty(refObjJoinCols)){
            throw new InvalidUsageException("调用错误：无法从condition中解析出字段关联.");
        }
        if(referencedGetterFieldNameList == null){
            throw new InvalidUsageException("调用错误：字段绑定必须指定字段field.");
        }
        Map<String, List> valueEntityListMap = new HashMap<>();
        // 直接关联
        if(middleTable == null){
            super.simplifySelectColumns();
            super.buildQueryWrapperJoinOn();
            // 查询条件为空时不进行查询
            if (queryWrapper.isEmptyOfNormal()) {
                return;
            }
            //处理orderBy，附加排序
            this.appendOrderBy(remoteBindDTO);
            List<T> entityList = null;
            // 查询entity列表: List<Role>
            if(V.isEmpty(this.module)){
                // 本地查询获取匹配结果的entityList
                entityList = getEntityList(queryWrapper);
            }
            else{
                // 远程调用获取
                entityList = RemoteBindingManager.fetchEntityList(module, remoteBindDTO, referencedEntityClass);
            }
            if(V.notEmpty(entityList)){
                valueEntityListMap = this.buildMatchKey2FieldListMap(entityList);
            }
            // 遍历list并赋值
            ResultAssembler.bindFieldListPropValue(super.getMatchedAnnoObjectList(), getAnnoObjJoinFlds(), valueEntityListMap,
                    annoObjectSetterPropNameList, referencedGetterFieldNameList, this.splitBy);
        }
        // 通过中间表关联
        else{
            if(refObjJoinCols.size() > 1){
                throw new InvalidUsageException(NOT_SUPPORT_MSG);
            }
            // 提取注解条件中指定的对应的列表
            Map<String, List> trunkObjCol2ValuesMap = super.buildTrunkObjCol2ValuesMap();
            // 处理中间表, 将结果转换成map
            Map<String, List> middleTableResultMap = middleTable.executeOneToManyQuery(trunkObjCol2ValuesMap);
            if(V.isEmpty(middleTableResultMap)){
                return;
            }
            super.simplifySelectColumns();
            //处理orderBy，附加排序
            this.appendOrderBy(remoteBindDTO);
            // 收集查询结果values集合
            List entityIdList = extractIdValueFromMap(middleTableResultMap);
            if(V.notEmpty(this.splitBy)){
                Class<?> fieldType = refObjPropInfo.getFieldTypeByColumn(refObjJoinCols.get(0));
                entityIdList = ResultAssembler.unpackValueList(entityIdList, this.splitBy, fieldType);
            }
            // 构建查询条件
            String refObjJoinOnCol = refObjJoinCols.get(0);
            List<T> entityList = null;
            // 查询entity列表: List<Role>
            if(V.isEmpty(this.module)){
                // 本地查询获取匹配结果的entityList
                queryWrapper.in(refObjJoinOnCol, entityIdList);
                entityList = getEntityList(queryWrapper);
            }
            else{
                // 远程调用获取
                remoteBindDTO.setRefJoinCol(refObjJoinOnCol).setInConditionValues(entityIdList);
                entityList = RemoteBindingManager.fetchEntityList(module, remoteBindDTO, referencedEntityClass);
            }
            if(V.isEmpty(entityList)){
                return;
            }
            String refObjJoinOnField = toRefObjField(refObjJoinOnCol);
            // 转换entity列表为Map<ID, Entity>
            Map<String, List<T>> entityMap = BeanUtils.convertToStringKeyObjectListMap(entityList, refObjJoinOnField);
            for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
                // List<roleId>
                List annoObjFKList = entry.getValue();
                if(V.isEmpty(annoObjFKList)){
                    continue;
                }
                List valueList = new ArrayList();
                for(Object obj : annoObjFKList){
                    String valStr = String.valueOf(obj);
                    List<T> ent = entityMap.get(valStr);
                    if(ent != null){
                        valueList.addAll(ent);
                    }
                    else if(V.notEmpty(splitBy) && valStr.contains(splitBy)){
                        for(String key : valStr.split(splitBy)){
                            ent = entityMap.get(key);
                            if(ent != null){
                                valueList.addAll(ent);
                            }
                        }
                    }
                }
                valueEntityListMap.put(entry.getKey(), valueList);
            }
            // 遍历list并赋值
            bindPropValue(super.getMatchedAnnoObjectList(), middleTable.getTrunkObjColMapping(), valueEntityListMap);
        }
    }

    /***
     * 从对象集合提取某个属性值到list中
     * @param fromList
     * @param trunkObjColMapping
     * @param valueMatchMap
     * @param <E>
     */
    private <E> void bindPropValue(List<E> fromList, Map<String, String> trunkObjColMapping, Map<String, List> valueMatchMap){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        try{
            for(E object : fromList){
                boolean appendComma = false;
                sb.setLength(0);
                for(Map.Entry<String, String> entry :trunkObjColMapping.entrySet()){
                    String getterField = toAnnoObjField(entry.getKey());
                    String fieldValue = BeanUtils.getStringProperty(object, getterField);
                    if(appendComma){
                        sb.append(Cons.SEPARATOR_COMMA);
                    }
                    sb.append(fieldValue);
                    if(appendComma == false){
                        appendComma = true;
                    }
                }
                // 查找匹配Key
                List entityList = valueMatchMap.get(sb.toString());
                if(entityList != null){
                    // 赋值
                    BeanWrapper beanWrapper = BeanUtils.getBeanWrapper(object);
                    for(int i = 0; i< annoObjectSetterPropNameList.size(); i++){
                        List valObjList = BeanUtils.collectToList(entityList, referencedGetterFieldNameList.get(i));
                        beanWrapper.setPropertyValue(annoObjectSetterPropNameList.get(i), valObjList);
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
