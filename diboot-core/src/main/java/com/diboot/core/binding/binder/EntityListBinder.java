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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * Entity集合绑定实现
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/19
 */
public class EntityListBinder<T> extends EntityBinder<T> {
    private static final Logger log = LoggerFactory.getLogger(EntityListBinder.class);

    /***
     * 构造方法
     * @param serviceInstance
     * @param voList
     */
    public EntityListBinder(IService<T> serviceInstance, List voList){
        super(serviceInstance, voList);
    }

    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(referencedEntityPrimaryKey == null){
            log.warn("调用错误：无法从condition中解析出字段关联.");
        }
        // 提取主键pk列表
        String annoObjectForeignKeyField = S.toLowerCaseCamel(annoObjectForeignKey);
        List annoObjectForeignKeyList = BeanUtils.collectToList(annoObjectList, annoObjectForeignKeyField);
        if(V.isEmpty(annoObjectForeignKeyList)){
            return;
        }
        Map<String, List> valueEntityListMap = new HashMap<>();
        // 解析中间表查询 1-N关联，如：
        //User.class @BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
        if(middleTable != null){
            Map<String, List> middleTableResultMap = middleTable.executeOneToManyQuery(annoObjectForeignKeyList);
            if(V.notEmpty(middleTableResultMap)){
                // 收集查询结果values集合
                List entityIdList = extractIdValueFromMap(middleTableResultMap);
                // 构建查询条件
                queryWrapper.in(S.toSnakeCase(referencedEntityPrimaryKey), entityIdList);
                // 查询entity列表: List<Role>
                List list = getEntityList(queryWrapper);
                if(V.notEmpty(list)){
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
                                valueList.add(cloneOrConvertBean(ent));
                            }
                        }
                        valueEntityListMap.put(entry.getKey(), valueList);
                    }
                }
            }
        }
        else{
            // 构建查询条件
            queryWrapper.in(S.toSnakeCase(referencedEntityPrimaryKey), annoObjectForeignKeyList);
            // 查询entity列表
            List<T> list = getEntityList(queryWrapper);
            if(V.notEmpty(list)){
                for(T entity : list){
                    String keyValue = BeanUtils.getStringProperty(entity, S.toLowerCaseCamel(referencedEntityPrimaryKey));
                    List entityList = valueEntityListMap.get(keyValue);
                    if(entityList == null){
                        entityList = new ArrayList<>();
                        valueEntityListMap.put(keyValue, entityList);
                    }
                    entityList.add(cloneOrConvertBean(entity));
                }
            }
        }
        // 绑定结果
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityListMap);
    }



}
