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
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
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
        if(referencedGetterColumnNameList == null){
            log.error("调用错误：字段绑定必须调用link()指定字段赋值和取值的setter/getter方法！");
            return;
        }
        // 解析默认主键字段名
        String referencedEntityPkName = S.toSnakeCase(referencedEntityPrimaryKey);
        String annoObjectFkFieldName = S.toLowerCaseCamel(annoObjectForeignKey);
        // 提取主键pk列表
        List annoObjectForeignKeyList = BeanUtils.collectToList(annoObjectList, annoObjectFkFieldName);
        if(V.isEmpty(annoObjectForeignKeyList)){
            return;
        }
        Map<String, List> valueEntityListMap = new HashMap<>();
        //@BindFieldList(entity = Role.class, field="name", condition="this.id=user_role.user_id AND user_role.role_id=id")
        //List<String> roleNameList;
        // 构建查询条件
        List<String> selectColumns = new ArrayList<>(referencedGetterColumnNameList.size()+1);
        selectColumns.add(referencedEntityPkName);
        selectColumns.addAll(referencedGetterColumnNameList);
        queryWrapper.select(S.toStringArray(selectColumns));
        // 处理中间表
        if(middleTable != null){
            // 将结果转换成map
            Map<String, List> middleTableResultMap = middleTable.executeOneToManyQuery(annoObjectForeignKeyList);
            if(V.isEmpty(middleTableResultMap)){
                return;
            }
            // 收集查询结果values集合
            List entityIdList = extractIdValueFromMap(middleTableResultMap);
            // 构建查询条件
            queryWrapper.in(referencedEntityPkName, entityIdList);
            // 查询entity列表: List<Role>
            List<T> list = getEntityList(queryWrapper);
            if(V.isEmpty(list)){
                return;
            }
            // 转换entity列表为Map<ID, Entity>
            Map<String, T> entityMap = BeanUtils.convertToStringKeyObjectMap(list, S.toLowerCaseCamel(referencedEntityPrimaryKey));
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
        }
        else{
            queryWrapper.in(referencedEntityPkName, annoObjectForeignKeyList);
            // 查询entity列表: List<Role>
            List<T> list = getEntityList(queryWrapper);
            if(V.isEmpty(list)){
                return;
            }
            for(T entity : list){
                String keyValue = BeanUtils.getStringProperty(entity, S.toLowerCaseCamel(referencedEntityPrimaryKey));
                List entityList = valueEntityListMap.get(keyValue);
                if(entityList == null){
                    entityList = new ArrayList<>();
                    valueEntityListMap.put(keyValue, entityList);
                }
                entityList.add(entity);
            }
        }
        // 遍历list并赋值
        for(Object annoObject : annoObjectList){
            // 将数子类型转换成字符串，以解决类型不一致的问题
            String annoObjectId = BeanUtils.getStringProperty(annoObject, annoObjectFkFieldName);
            List entityList = valueEntityListMap.get(annoObjectId);
            if(entityList != null){
                for(int i = 0; i< annoObjectSetterPropNameList.size(); i++){
                    List valObjList = BeanUtils.collectToList(entityList, referencedGetterColumnNameList.get(i));
                    BeanUtils.setProperty(annoObject, annoObjectSetterPropNameList.get(i), valObjList);
                }
            }
        }
    }

}
