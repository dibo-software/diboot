package com.diboot.core.binding;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.SqlExecutor;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Entity集合绑定实现
 * @author Mazhicheng
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
        this.referencedService = serviceInstance;
        this.annoObjectList = voList;
        this.queryWrapper = new QueryWrapper<T>();
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
        Map<String, List<T>> valueEntityListMap = new HashMap<>();
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
                // 转换entity列表为Map<ID, Entity>
                Map<String, T> entityMap = BeanUtils.convertToStringKeyObjectMap(list, S.toLowerCaseCamel(referencedEntityPrimaryKey));
                for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
                    // List<roleId>
                    List annoObjFKList = entry.getValue();
                    if(V.isEmpty(annoObjFKList)){
                        continue;
                    }
                    List<T> valueList = new ArrayList();
                    for(Object obj : annoObjFKList){
                        T ent = entityMap.get(String.valueOf(obj));
                        if(ent != null){
                            valueList.add(cloneEntity(ent));
                        }
                    }
                    valueEntityListMap.put(entry.getKey(), valueList);
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
                    List<T> entityList = valueEntityListMap.get(keyValue);
                    if(entityList == null){
                        entityList = new ArrayList<>();
                        valueEntityListMap.put(keyValue, entityList);
                    }
                    entityList.add(cloneEntity(entity));
                }
            }
        }
        // 绑定结果
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityListMap);
    }

    /**
     * 从Map中提取ID的值
     * @param middleTableResultMap
     * @return
     */
    private List extractIdValueFromMap(Map<String, List> middleTableResultMap) {
        List entityIdList = new ArrayList();
        for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
            if(V.isEmpty(entry.getValue())){
                continue;
            }
            for(Object id : entry.getValue()){
                if(!entityIdList.contains(id)){
                    entityIdList.add(id);
                }
            }
        }
        return entityIdList;
    }

}
