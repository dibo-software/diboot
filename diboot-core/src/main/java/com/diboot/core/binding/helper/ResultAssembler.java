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

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;

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
    public static <E> void bindPropValue(String setterFieldName, List<E> fromList, List<String> getterFields, Map valueMatchMap){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        List<String> fieldValues = new ArrayList<>(getterFields.size());
        try{
            for(E object : fromList){
                fieldValues.clear();
                for(String getterField : getterFields){
                    String fieldValue = BeanUtils.getStringProperty(object, getterField);
                    fieldValues.add(fieldValue);
                }
                // 查找匹配Key
                String matchKey = S.join(fieldValues);
                if(valueMatchMap.containsKey(matchKey)){
                    // 赋值
                    BeanUtils.setProperty(object, setterFieldName, valueMatchMap.get(matchKey));
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
    public static <E> void bindPropValue(String setterFieldName, List<E> fromList, Map<String, String> trunkObjColMapping, Map valueMatchMap, Map<String, String> col2FieldMapping){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        List<String> fieldValues = new ArrayList<>(trunkObjColMapping.size());
        try{
            for(E object : fromList){
                fieldValues.clear();
                for(Map.Entry<String, String> entry :trunkObjColMapping.entrySet()){
                    //转换为字段名
                    String getterField = col2FieldMapping.get(entry.getKey());
                    if(getterField == null){
                        getterField = S.toLowerCaseCamel(entry.getKey());
                    }
                    String fieldValue = BeanUtils.getStringProperty(object, getterField);
                    fieldValues.add(fieldValue);
                }
                // 查找匹配Key
                String matchKey = S.join(fieldValues);
                if(valueMatchMap.containsKey(matchKey)){
                    // 赋值
                    BeanUtils.setProperty(object, setterFieldName, valueMatchMap.get(matchKey));
                }
            }
            fieldValues.clear();
        }
        catch (Exception e){
            log.warn("设置属性值异常, setterFieldName="+setterFieldName, e);
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
        Map<String, Object> resultMap = new HashMap<>();
        List<String> fieldValues = new ArrayList<>(trunkObjColMapping.size());
        for(Map<String, E> row : resultSetMapList){
            fieldValues.clear();
            for(Map.Entry<String, String> entry : trunkObjColMapping.entrySet()){
                Object keyObj = row.containsKey(entry.getValue())? row.get(entry.getValue()) : row.get(entry.getValue().toUpperCase());
                fieldValues.add(S.valueOf(keyObj));
            }
            String matchKeys = S.join(fieldValues);
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
        // 合并list为map
        Map<String, List> resultMap = new HashMap<>();
        List<String> fieldValues = new ArrayList<>(trunkObjColMapping.size());
        for(Map<String, E> row : resultSetMapList){
            fieldValues.clear();
            for(Map.Entry<String, String> entry : trunkObjColMapping.entrySet()){
                Object keyObj = row.containsKey(entry.getValue())? row.get(entry.getValue()) : row.get(entry.getValue().toUpperCase());
                fieldValues.add(S.valueOf(keyObj));
            }
            String matchKeys = S.join(fieldValues);
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
        return resultMap;
    }

}
