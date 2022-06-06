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

import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.binder.remote.RemoteBindingManager;
import com.diboot.core.config.Cons;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 关联字段绑定
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/19
 */
public class FieldBinder<T> extends BaseBinder<T> {
    private static final Logger log = LoggerFactory.getLogger(FieldBinder.class);

    /**
     * VO对象绑定赋值的属性名列表
     */
    protected List<String> annoObjectSetterPropNameList;
    /**
     * DO/Entity对象对应的getter取值属性名列表
     */
    protected List<String> referencedGetterFieldNameList;

    /***
     * 构造方法
     * @param entityClass
     * @param voList
     */
    public FieldBinder(Class<T> entityClass, List voList){
        super(entityClass, voList);
    }

    /***
     * 构造方法
     * @param annotation
     * @param voList
     */
    public FieldBinder(BindField annotation, List voList){
        super(annotation.entity(), voList);
    }

    /***
     * 指定VO绑定属性赋值的setter和DO/Entity取值的getter方法
     * @param toVoSetter VO中调用赋值的setter方法
     * @param <T1> VO类型
     * @param <T2> DO类型
     * @param <R> set方法参数类型
     * @return
     */
    public <T1,T2,R> FieldBinder<T> link(IGetter<T2> fromDoGetter, ISetter<T1, R> toVoSetter){
        return link(BeanUtils.convertToFieldName(fromDoGetter), BeanUtils.convertToFieldName(toVoSetter));
    }

    /***
     * 指定VO绑定赋值的setter属性名和DO/Entity取值的getter属性名
     * @param toVoField VO中调用赋值的setter属性名
     * @return
     */
    public FieldBinder<T> link(String fromDoField, String toVoField){
        if(annoObjectSetterPropNameList == null){
            annoObjectSetterPropNameList = new ArrayList<>(4);
        }
        annoObjectSetterPropNameList.add(toVoField);
        if(referencedGetterFieldNameList == null){
            referencedGetterFieldNameList = new ArrayList<>(4);
        }
        referencedGetterFieldNameList.add(fromDoField);
        return this;
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
            throw new InvalidUsageException("调用错误：字段绑定必须指定字段field");
        }
        // 直接关联
        if(middleTable == null){
            this.simplifySelectColumns();
            super.buildQueryWrapperJoinOn();
            // 查询条件为空时不进行查询
            if (queryWrapper.isEmptyOfNormal()) {
                return;
            }
            List<T> entityList = null;
            if(V.isEmpty(this.module)){
                // 本地查询获取匹配结果的entityList
                entityList = getEntityList(queryWrapper);
            }
            else{
                // 远程调用获取
                entityList = RemoteBindingManager.fetchEntityList(module, remoteBindDTO, referencedEntityClass);
            }
            if(V.isEmpty(entityList)){
                return;
            }
            // 将结果list转换成entityMap
            Map<String, T> key2EntityMap = this.buildMatchKey2EntityMap(entityList);
            // 遍历list并赋值
            for(Object annoObject : super.getMatchedAnnoObjectList()){
                String matchKey = buildMatchKey(annoObject);
                setFieldValueToTrunkObj(key2EntityMap, annoObject, matchKey);
            }
        }
        else{
            if(refObjJoinCols.size() > 1){
                throw new InvalidUsageException(NOT_SUPPORT_MSG);
            }
            // 提取注解条件中指定的对应的列表
            Map<String, List> trunkObjCol2ValuesMap = super.buildTrunkObjCol2ValuesMap();
            // 中间表查询结果map
            Map<String, Object> middleTableResultMap = middleTable.executeOneToOneQuery(trunkObjCol2ValuesMap);
            if(V.isEmpty(middleTableResultMap)){
                return;
            }
            // 收集查询结果values集合
            Collection refObjValues = middleTableResultMap.values().stream().distinct().collect(Collectors.toList());
            this.simplifySelectColumns();
            // 构建查询条件
            String refObjJoinOnCol = refObjJoinCols.get(0);
            // 获取匹配结果的mapList
            List<T> entityList = null;
            if(V.isEmpty(this.module)){
                queryWrapper.in(refObjJoinOnCol, refObjValues);
                // 本地查询获取匹配结果的entityList
                entityList = getEntityList(queryWrapper);
            }
            else{
                // 远程调用获取
                remoteBindDTO.setRefJoinCol(refObjJoinOnCol).setInConditionValues(refObjValues);
                entityList = RemoteBindingManager.fetchEntityList(module, remoteBindDTO, referencedEntityClass);
            }
            if(V.isEmpty(entityList)){
                return;
            }
            // 将结果list转换成entityMap
            Map<String, T> key2EntityMap = this.buildMatchKey2EntityMap(entityList);
            // 遍历list并赋值
            for(Object annoObject : super.getMatchedAnnoObjectList()){
                String matchKey = buildMatchKey(annoObject, middleTableResultMap);
                setFieldValueToTrunkObj(key2EntityMap, annoObject, matchKey);
            }
        }

    }

    /**
     * 设置字段值
     * @param key2EntityMap
     * @param annoObject
     * @param matchKey
     */
    private void setFieldValueToTrunkObj(Map<String, T> key2EntityMap, Object annoObject, String matchKey) {
        T relationEntity = key2EntityMap.get(matchKey);
        if (relationEntity != null) {
            BeanWrapper beanWrapper = BeanUtils.getBeanWrapper(annoObject);
            for (int i = 0; i < annoObjectSetterPropNameList.size(); i++) {
                Object valObj = BeanUtils.getProperty(relationEntity, referencedGetterFieldNameList.get(i));
                beanWrapper.setPropertyValue(annoObjectSetterPropNameList.get(i), valObj);
            }
        }
    }

    /**
     * 构建匹配key-entity目标的map
     * @param entityList
     * @return
     */
    protected Map<String, T> buildMatchKey2EntityMap(List<T> entityList){
        Map<String, T> key2TargetMap = new HashMap<>(entityList.size());
        for(T entity : entityList){
            List<String> joinOnValues = new ArrayList<>(refObjJoinCols.size());
            for(String refObjJoinOnCol : refObjJoinCols){
                Object valObj = BeanUtils.getProperty(entity, toRefObjField(refObjJoinOnCol));
                joinOnValues.add(S.valueOf(valObj));
            }
            String matchKey = S.join(joinOnValues);
            if(matchKey != null){
                key2TargetMap.put(matchKey, entity);
            }
        }
        return key2TargetMap;
    }

    /**
     * 构建匹配Key
     * @param annoObject
     * @return
     */
    private String buildMatchKey(Object annoObject){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<annoObjJoinCols.size(); i++){
            String col = annoObjJoinCols.get(i);
            // 将数子类型转换成字符串，以便解决类型不一致的问题
            String val = BeanUtils.getStringProperty(annoObject, toAnnoObjField(col));
            if(i > 0){
                sb.append(Cons.SEPARATOR_COMMA);
            }
            sb.append(val);
        }
        return sb.toString();
    }

    /**
     * 构建匹配Key
     * @param annoObject
     * @param middleTableResultMap
     * @return
     */
    private String buildMatchKey(Object annoObject, Map<String, Object> middleTableResultMap){
        StringBuilder sb = new StringBuilder();
        boolean appendComma = false;
        for(Map.Entry<String, String> entry : middleTable.getTrunkObjColMapping().entrySet()){
            String getterField = toAnnoObjField(entry.getKey());
            String fieldValue = BeanUtils.getStringProperty(annoObject, getterField);
            // 通过中间结果Map转换得到OrgId
            if(V.notEmpty(middleTableResultMap)){
                Object value = getValueIgnoreKeyCase(middleTableResultMap, fieldValue);
                fieldValue = String.valueOf(value);
            }
            if(appendComma){
                sb.append(Cons.SEPARATOR_COMMA);
            }
            sb.append(fieldValue);
            if(appendComma == false){
                appendComma = true;
            }
        }
        // 查找匹配Key
        return sb.toString();
    }

    @Override
    protected void simplifySelectColumns() {
        List<String> selectColumns = new ArrayList<>(8);
        selectColumns.addAll(refObjJoinCols);
        if(V.notEmpty(referencedGetterFieldNameList)){
            for(String referencedGetterField : referencedGetterFieldNameList){
                String refObjCol = toRefObjColumn(referencedGetterField);
                if(!selectColumns.contains(refObjCol)){
                    selectColumns.add(refObjCol);
                }
            }
        }
        // 添加orderBy排序
        if(V.notEmpty(this.orderBy)){
            // 解析排序
            String[] orderByFields = S.split(this.orderBy);
            for(String field : orderByFields){
                String colName = field.toLowerCase();
                if(colName.contains(":")){
                    colName = S.split(colName, ":")[0];
                }
                colName = toRefObjColumn(colName);
                if(!selectColumns.contains(colName)){
                    selectColumns.add(colName);
                }
            }
        }
        String[] selectColsArray = S.toStringArray(selectColumns);
        if(remoteBindDTO != null){
            remoteBindDTO.setSelectColumns(selectColsArray);
        }
        this.queryWrapper.select(selectColsArray);
    }

}
