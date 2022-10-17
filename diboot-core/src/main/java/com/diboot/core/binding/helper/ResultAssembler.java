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
package com.diboot.core.binding.helper;

import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;

import java.util.*;

/**
 * 绑定关联数据组装器
 * @author mazc@dibo.ltd
 * @version v2.1.2
 * @date 2020/08/19
 */
@Slf4j
public class ResultAssembler {

    /***
     * 从对象集合提取某个属性值到list中
     * @param setterFieldName
     * @param fromList
     * @param getterFields
     * @param valueMatchMap
     * @param <E>
     */
    public static <E> void bindPropValue(String setterFieldName, List<E> fromList, String[] getterFields, Map valueMatchMap, String splitBy){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        try{
            for(E object : fromList){
                List matchKeys = null;
                BeanWrapper beanWrapper = BeanUtils.getBeanWrapper(object);
                for(int i=0; i<getterFields.length; i++){
                    Object fieldValueObj = BeanUtils.getProperty(object, getterFields[i]);
                    if(fieldValueObj == null) {
                        continue;
                    }
                    if(fieldValueObj instanceof Collection) {
                        if(matchKeys == null) {
                            matchKeys = new ArrayList();
                        }
                        matchKeys.addAll((Collection) fieldValueObj);
                    }
                    else {
                        if(matchKeys == null) {
                            matchKeys = new ArrayList(getterFields.length);
                        }
                        matchKeys.add(S.clearNonConst(S.valueOf(fieldValueObj)));
                    }
                }
                if(matchKeys == null) {
                    continue;
                }
                // 查找匹配Key
                String matchKey = S.join(matchKeys);
                if(valueMatchMap.containsKey(matchKey)){
                    // 赋值
                    beanWrapper.setPropertyValue(setterFieldName, valueMatchMap.get(matchKey));
                }
                else {
                    if(matchKeys.size() == 1 && V.notEmpty(splitBy) && getterFields.length == 1 && matchKey.contains(splitBy)) {
                        matchKeys = S.splitToList(matchKey, splitBy);
                    }
                    List matchedValues = new ArrayList(matchKeys.size());
                    for(Object key : matchKeys){
                        Object value = valueMatchMap.get(S.valueOf(key));
                        if(value != null){
                            if(value instanceof Collection){
                                Collection valueList = (Collection)value;
                                for(Object obj : valueList){
                                    if(!matchedValues.contains(obj)){
                                        matchedValues.add(obj);
                                    }
                                }
                            }
                            else{
                                if(!matchedValues.contains(value)){
                                    matchedValues.add(value);
                                }
                            }
                        }
                    }
                    // 赋值
                    beanWrapper.setPropertyValue(setterFieldName, matchedValues);
                }
            }
        }
        catch (Exception e){
            log.warn("设置属性值异常, setterFieldName="+setterFieldName, e);
        }
    }

    /***
     * 从对象集合提取某个属性值到list中
     * @param setterFieldName
     * @param fromList
     * @param trunkObjColMapping
     * @param valueMatchMap
     * @param <E>
     */
    public static <E> void bindEntityPropValue(String setterFieldName, List<E> fromList, Map<String, String> trunkObjColMapping, Map valueMatchMap, Map<String, String> col2FieldMapping){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        try{
            for(E object : fromList){
                boolean appendComma = false;
                sb.setLength(0);
                for(Map.Entry<String, String> entry :trunkObjColMapping.entrySet()){
                    //转换为字段名
                    String getterField = col2FieldMapping.get(entry.getKey());
                    if(getterField == null){
                        getterField = S.toLowerCaseCamel(entry.getKey());
                    }
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
                String matchKey = sb.toString();
                if(valueMatchMap.containsKey(matchKey)){
                    // 赋值
                    BeanUtils.setProperty(object, setterFieldName, valueMatchMap.get(matchKey));
                }
            }
            sb.setLength(0);
        }
        catch (Exception e){
            log.warn("设置属性值异常, setterFieldName="+setterFieldName, e);
        }
    }


    /***
     * 从对象集合提取某个属性值到list中
     * @param fromList
     * @param getterFields
     * @param valueMatchMap
     * @param <E>
     */
    public static <E> void bindFieldListPropValue(List<E> fromList, String[] getterFields, Map<String, List> valueMatchMap,
                                                  List<String> annoObjSetterPropNameList, List<String> refGetterFieldNameList, String splitBy){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        try{
            for(E object : fromList){
                List matchKeys = null;
                for(int i=0; i<getterFields.length; i++){
                    Object fieldValueObj = BeanUtils.getProperty(object, getterFields[i]);
                    if(fieldValueObj == null) {
                        continue;
                    }
                    if(fieldValueObj instanceof Collection) {
                        if(matchKeys == null) {
                            matchKeys = new ArrayList();
                        }
                        matchKeys.addAll((Collection) fieldValueObj);
                    }
                    else {
                        if(matchKeys == null) {
                            matchKeys = new ArrayList(getterFields.length);
                        }
                        matchKeys.add(S.clearNonConst(S.valueOf(fieldValueObj)));
                    }
                }
                // 无有效值，跳过
                if(matchKeys == null) {
                    continue;
                }
                // 查找匹配Key
                String matchKey = S.join(matchKeys);
                List entityList = valueMatchMap.get(matchKey);
                if(entityList == null){
                    if (matchKeys.size() == 1 && V.notEmpty(splitBy) && matchKey.contains(splitBy)) {
                        matchKeys = S.splitToList(matchKey, splitBy);
                    }
                    List matchedValues = new ArrayList(matchKeys.size());
                    for(Object key : matchKeys){
                        Object value = valueMatchMap.get(S.valueOf(key));
                        if(value != null){
                            if(value instanceof Collection){
                                Collection valueList = (Collection)value;
                                for(Object obj : valueList){
                                    if(!matchedValues.contains(obj)){
                                        matchedValues.add(obj);
                                    }
                                }
                            }
                            else{
                                if(!matchedValues.contains(value)){
                                    matchedValues.add(value);
                                }
                            }
                        }
                    }
                    if(matchedValues != null){
                        entityList = matchedValues;
                    }
                }
                // 赋值
                BeanWrapper beanWrapper = BeanUtils.getBeanWrapper(object);
                for(int i = 0; i< annoObjSetterPropNameList.size(); i++){
                    List valObjList = BeanUtils.collectToList(entityList, refGetterFieldNameList.get(i));
                    beanWrapper.setPropertyValue(annoObjSetterPropNameList.get(i), valObjList);
                }
            }
        }
        catch (Exception e){
            log.warn("设置属性值异常", e);
        }
    }

    /**
     * 合并为1-1的map结果
     * @param resultSetMapList
     * @param trunkObjColMapping
     * @param branchObjColMapping
     * @param <E>
     * @return
     */
    public static <E> Map<String, Object> convertToOneToOneResult(List<Map<String, E>> resultSetMapList, Map<String, String> trunkObjColMapping, Map<String, String> branchObjColMapping) {
        if(V.isEmpty(resultSetMapList)){
            return Collections.emptyMap();
        }
        // 获取valueName
        String valueName = branchObjColMapping.entrySet().iterator().next().getKey();
        // 合并list为map
        Map<String, Object> resultMap = new HashMap<>(resultSetMapList.size());
        StringBuilder sb = new StringBuilder();
        for(Map<String, E> row : resultSetMapList){
            boolean appendComma = false;
            sb.setLength(0);
            for(Map.Entry<String, String> entry : trunkObjColMapping.entrySet()){
                Object keyObj = getValueIgnoreKeyCase(row, entry.getValue());
                if(appendComma){
                    sb.append(Cons.SEPARATOR_COMMA);
                }
                sb.append(S.valueOf(keyObj));
                if(appendComma == false){
                    appendComma = true;
                }
            }
            String matchKeys = sb.toString();
            Object valueObj = row.containsKey(valueName)? row.get(valueName) : row.get(valueName.toUpperCase());
            resultMap.put(matchKeys, valueObj);
        }
        return resultMap;
    }

    /**
     * 合并为1-n的map结果
     * @param resultSetMapList
     * @param trunkObjColMapping
     * @param branchObjColMapping
     * @param <E>
     * @return
     */
    public static <E> Map<String, List> convertToOneToManyResult(List<Map<String, E>> resultSetMapList, Map<String, String> trunkObjColMapping, Map<String, String> branchObjColMapping){
        if(V.isEmpty(resultSetMapList)){
            return Collections.emptyMap();
        }
        // 获取valueName
        String valueName = branchObjColMapping.entrySet().iterator().next().getKey();
        valueName = S.removeEsc(valueName);
        // 合并list为map
        Map<String, List> resultMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for(Map<String, E> row : resultSetMapList){
            boolean appendComma = false;
            sb.setLength(0);
            for(Map.Entry<String, String> entry : trunkObjColMapping.entrySet()){
                Object keyObj = getValueIgnoreKeyCase(row, entry.getValue());
                if(appendComma){
                    sb.append(Cons.SEPARATOR_COMMA);
                }
                sb.append(S.valueOf(keyObj));
                if(appendComma == false){
                    appendComma = true;
                }
            }
            String matchKeys = sb.toString();
            Object valueObj = row.containsKey(valueName)? row.get(valueName) : row.get(valueName.toUpperCase());
            if(valueObj != null){
                List valueList = resultMap.get(matchKeys);
                if(valueList == null){
                    valueList = new ArrayList();
                    resultMap.put(matchKeys, valueList);
                }
                valueList.add(valueObj);
            }
        }
        sb.setLength(0);
        return resultMap;
    }


    /**
     * 从map中取值，如直接取为null尝试转换大写后再取，以支持ORACLE等大写命名数据库
     * @param map
     * @param key
     * @return
     */
    public static Object getValueIgnoreKeyCase(Map<String, ?> map, String key){
        if(map == null || key == null){
            return null;
        }
        key = S.removeEsc(key);
        if(map.containsKey(key)){
            return map.get(key);
        }
        if(map.containsKey(key.toUpperCase())){
            return map.get(key.toUpperCase());
        }
        return null;
    }

    /**
     * 拆解值列表
     * @param valueList
     * @param splitBy
     * @return
     */
    public static List unpackValueList(List valueList, String splitBy, Class<?> fieldType) {
        if(V.isEmpty(valueList)) {
            return valueList;
        }
        List newValueList = new ArrayList();
        for(Object value : valueList) {
            if(value == null) {
                continue;
            }
            if(value instanceof Collection) {
                for(Object key : (Collection)value){
                    if(!newValueList.contains(key)){
                        newValueList.add(key);
                    }
                }
            }
            else {
                if(V.notEmpty(splitBy)){
                    boolean isSameType = fieldType == null || fieldType.equals(String.class);
                    String valueStr = S.clearNonConst(S.valueOf(value));
                    if(valueStr.contains(splitBy)) {
                        for(String oneVal : valueStr.split(splitBy)){
                            Object oneValueObj = isSameType? oneVal : BeanUtils.convertValueToFieldType(oneVal, fieldType);
                            if(!newValueList.contains(oneValueObj)){
                                newValueList.add(oneValueObj);
                            }
                        }
                    }
                    else {
                        Object oneValueObj = isSameType? value : BeanUtils.convertValueToFieldType(value, fieldType);
                        if(!newValueList.contains(oneValueObj)){
                            newValueList.add(oneValueObj);
                        }
                    }
                }
                else if(!newValueList.contains(value)){
                    newValueList.add(value);
                }
            }
        }
        return newValueList;
    }

}
