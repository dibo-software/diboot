package com.diboot.core.binding;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.BaseService;
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
    public EntityBinder(BaseService<T> referencedService, List voList){
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
        // 通过中间表关联Entity
        // @BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id AND department.deleted=0")
        // Organization organization;
        if(middleTable != null){
            // 提取中间表查询SQL: SELECT id, org_id FROM department WHERE id IN(?)
            String sql = middleTable.toSQL(annoObjectForeignKeyList);
            // 执行查询并合并结果
            String keyName = middleTable.getEqualsToRefEntityPkColumn(), valueName = middleTable.getEqualsToAnnoObjectFKColumn();
            Map<String, List> middleTableResultMap = SqlExecutor.executeQueryAndMergeResult(sql, annoObjectForeignKeyList, keyName, valueName);
            if(V.notEmpty(middleTableResultMap)){
                // 提取entity主键值集合
                Collection middleTableColumnValueList = middleTableResultMap.keySet();
                // 构建查询条件
                queryWrapper.in(S.toSnakeCase(referencedEntityPrimaryKey), middleTableColumnValueList);
                // 查询entity列表
                List<T> list = referencedService.getEntityList(queryWrapper);
                // 基于中间表查询结果和entity列表绑定结果
                bindingResultWithMiddleTable(S.toLowerCaseCamel(referencedEntityPrimaryKey), list, middleTableResultMap);
            }
        }
        // 直接关联Entity
        // @BindEntity(entity = Department.class, condition="department_id=id")
        // Department department;
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
     * @param doPkPropName
     * @param list
     */
    private <E> void bindingResultWithMiddleTable(String doPkPropName, List<E> list, Map<String, List> middleTableResultMap) {
        // 构建IdString-Entity之间的映射Map
        Map<String, E> valueEntityMap = new HashMap<>(list.size());
        for(E entity : list){
            // 获取主键值
            String pkValue = BeanUtils.getStringProperty(entity, doPkPropName);
            // 得到对应Entity
            List annoObjFKList = middleTableResultMap.get(pkValue);
            if(V.notEmpty(annoObjFKList)){
                valueEntityMap.put(String.valueOf(annoObjFKList.get(0)), entity);
            }
            else{
                log.warn("{}.{}={} 无匹配结果!", entity.getClass().getSimpleName(), doPkPropName, pkValue);
            }
        }
        // 绑定
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityMap);
    }

    /***
     * 绑定结果
     * @param doPkPropName
     * @param list
     */
    private void bindingResult(String doPkPropName, List<T> list) {
        Map<String, T> valueEntityMap = new HashMap<>(list.size());
        for(T entity : list){
            String pkValue = BeanUtils.getStringProperty(entity, doPkPropName);
            valueEntityMap.put(pkValue, entity);
        }
        // 绑定
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityMap);
    }

}
