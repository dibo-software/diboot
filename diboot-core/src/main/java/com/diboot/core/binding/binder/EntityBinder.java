package com.diboot.core.binding.binder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity实体绑定Binder，用于绑定当前一个entity到目标对象的属性
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/19
 */
public class EntityBinder<T> extends BaseBinder<T> {
    private static final Logger log = LoggerFactory.getLogger(EntityBinder.class);

    /***
     * 给待绑定list中VO对象赋值的setter属性名
     */
    protected String annoObjectField;

    public EntityBinder(){}
    /***
     * 构造方法
     * @param referencedService
     * @param voList
     */
    public EntityBinder(IService<T> referencedService, List voList){
        this.referencedService = referencedService;
        this.annoObjectList = voList;
        this.queryWrapper = new QueryWrapper<T>();
    }

    /***
     * 指定VO绑定属性赋值的setter方法
     * @param voSetter VO中调用赋值的setter方法
     * @param <T1> VO类型
     * @param <R> set方法参数类型
     * @return
     */
    public <T1,R> BaseBinder<T> set(ISetter<T1, R> voSetter){
        return set(BeanUtils.convertToFieldName(voSetter));
    }

    /***
     * 指定VO绑定属性赋值的set属性
     * @param annoObjectField VO中调用赋值的setter属性
     * @return
     */
    public BaseBinder<T> set(String annoObjectField){
        this.annoObjectField = annoObjectField;
        return this;
    }

    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(referencedEntityPrimaryKey == null){
            log.warn("调用错误：无法从condition中解析出字段关联.");
        }
        // 提取注解条件中指定的对应的列表
        String annoObjectForeignKeyField = S.toLowerCaseCamel(annoObjectForeignKey);
        List annoObjectForeignKeyList = BeanUtils.collectToList(annoObjectList, annoObjectForeignKeyField);
        if(V.isEmpty(annoObjectForeignKeyList)){
            return;
        }
        // 结果转换Map
        Map<String, T> valueEntityMap = new HashMap<>();
        // 通过中间表关联Entity
        // @BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id AND department.deleted=0")
        // Organization organization;
        if(middleTable != null){
            Map<String, Object> middleTableResultMap = middleTable.executeOneToOneQuery(annoObjectForeignKeyList);
            if(V.notEmpty(middleTableResultMap)){
                // 提取entity主键值集合
                Collection middleTableColumnValueList = middleTableResultMap.values();
                // 构建查询条件
                queryWrapper.in(S.toSnakeCase(referencedEntityPrimaryKey), middleTableColumnValueList);
                // 查询entity列表
                List<T> list = getEntityList(queryWrapper);
                if(V.notEmpty(list)){
                    // 转换entity列表为Map<ID, Entity>
                    Map<String, T> listMap = BeanUtils.convertToStringKeyObjectMap(list, S.toLowerCaseCamel(referencedEntityPrimaryKey));
                    for(Map.Entry<String, Object> entry : middleTableResultMap.entrySet()){
                        Object fetchValueId = entry.getValue();
                        if(fetchValueId == null){
                            continue;
                        }
                        String key = entry.getKey();
                        T value = listMap.get(String.valueOf(fetchValueId));
                        valueEntityMap.put(key, cloneEntity(value));
                    }
                }
            }
        }
        // 直接关联Entity
        // @BindEntity(entity = Department.class, condition="department_id=id")
        // Department department;
        else{
            // 构建查询条件
            queryWrapper.in(S.toSnakeCase(referencedEntityPrimaryKey), annoObjectForeignKeyList);
            // 查询entity列表
            List<T> list = getEntityList(queryWrapper);
            if(V.notEmpty(list)){
                String refEntityPKFieldName = S.toLowerCaseCamel(referencedEntityPrimaryKey);
                for(T entity : list){
                    String pkValue = BeanUtils.getStringProperty(entity, refEntityPKFieldName);
                    valueEntityMap.put(pkValue, cloneEntity(entity));
                }
            }
        }
        // 绑定结果
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityMap);
    }

    /**
     * 克隆对象
     * @param ent
     * @param <T>
     * @return
     */
    protected <T> T cloneEntity(T ent){
        // 克隆对象
        try{
            T cloneEnt = (T)org.springframework.beans.BeanUtils.instantiateClass(ent.getClass());
            BeanUtils.copyProperties(ent ,cloneEnt);
            return cloneEnt;
        }
        catch (Exception e){
            log.warn("Clone Object "+ent.getClass().getSimpleName()+" error", e);
            return ent;
        }
    }

}
