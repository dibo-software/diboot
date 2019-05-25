package com.diboot.core.binding;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public EntityListBinder(BaseService<T> serviceInstance, List voList){
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
        // 解析中间表查询 1-N关联，如：
        //User.class @BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
        if(middleTable != null){
            // 构建查询SQL： SELECT user_id, role_id FROM user_role WHERE user_id IN (?)
            String sql = middleTable.toSQL(annoObjectForeignKeyList);
            // 执行查询并合并结果
            String valueName = middleTable.getEqualsToRefEntityPkColumn(), keyName = middleTable.getEqualsToAnnoObjectFKColumn();
            Map<String, List> middleTableResultMap = SqlExecutor.executeQueryAndMergeResult(sql, annoObjectForeignKeyList, keyName, valueName);
            if(V.notEmpty(middleTableResultMap)){
                // 收集查询结果values集合
                List entityIdList = new ArrayList();
                for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
                    if(V.notEmpty(entry.getValue())){
                        for(Object id : entry.getValue()){
                            if(!entityIdList.contains(id)){
                                entityIdList.add(id);
                            }
                        }
                    }
                }
                // 构建查询条件
                queryWrapper.in(S.toSnakeCase(referencedEntityPrimaryKey), entityIdList);
                // 查询entity列表: List<Role>
                List list = referencedService.getEntityList(queryWrapper);
                // 绑定结果
                bindingResultWithMiddleTable(S.toLowerCaseCamel(annoObjectForeignKey), list, middleTableResultMap);
            }
        }
        else{
            // 构建查询条件
            queryWrapper.in(S.toSnakeCase(referencedEntityPrimaryKey), annoObjectForeignKeyList);
            // 查询entity列表
            List<T> list = referencedService.getEntityList(queryWrapper);
            // 绑定结果
            bindingResult(S.toLowerCaseCamel(referencedEntityPrimaryKey), list);
        }
    }

    /***
     * 基于中间表查询结果和entity列表绑定结果
     * @param annoObjectForeignKey
     * @param list
     */
    private <E> void bindingResultWithMiddleTable(String annoObjectForeignKey, List<E> list, Map<String, List> middleTableResultMap) {
        if(V.isEmpty(list)){
            return;
        }
        // 将 List<Role> 转换为 Map<Id,Role>
        Map<String, E> entityMap = BeanUtils.convertToStringKeyObjectMap(list, annoObjectForeignKey);
        // 将Map<String, List<Id>> 转换为 Map<String, List<Role>>
        Map<String, List<E>> valueEntityMap = new HashMap<>(list.size());
        for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
            // List<roleId>
            List annoObjFKList = entry.getValue();
            if(V.notEmpty(annoObjFKList)){
                List<E> valueList = new ArrayList();
                for(Object obj : annoObjFKList){
                    E ent = entityMap.get(String.valueOf(obj));
                    if(ent != null){
                        valueList.add(ent);
                    }
                }
                valueEntityMap.put(entry.getKey(), valueList);
            }
            else{
                log.warn("转换结果异常，中间关联条件数据不一致");
            }
        }
        // 绑定
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityMap);
    }

    private void bindingResult(String fkName, List<T> list) {
        Map<Object, List<T>> valueEntityListMap = new HashMap<>(list.size());
        for(T entity : list){
            Object keyValue = BeanUtils.getProperty(entity, fkName);
            List<T> entityList = valueEntityListMap.get(keyValue);
            if(entityList == null){
                entityList = new ArrayList<>();
                valueEntityListMap.put(keyValue, entityList);
            }
            entityList.add(entity);
        }
        // 绑定
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityListMap);
    }

}
