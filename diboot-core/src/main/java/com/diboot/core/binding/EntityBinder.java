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
            log.warn("调用错误：必须调用joinOn()方法连接两个字段.");
        }
        // 提取主键pk列表
        String annoObjectForeignKeyField = S.toLowerCaseCamel(annoObjectForeignKey);
        List annoObjectForeignKeyList = BeanUtils.collectToList(annoObjectList, annoObjectForeignKeyField);
        if(V.isEmpty(annoObjectForeignKeyList)){
            return;
        }
        // 解析中间表查询关联
        if(middleTable != null){
            String sql = middleTable.toSQL(annoObjectForeignKeyList);
            // 执行查询并合并结果
            String keyName = middleTable.getEqualsToRefEntityPkColumn(), valueName = middleTable.getEqualsToAnnoObjectFKColumn();
            Map<Object, Object> middleTableResultMap = SqlExecutor.executeQueryAndMergeResult(sql, annoObjectForeignKeyList, keyName, valueName);
            if(V.notEmpty(middleTableResultMap)){
                Collection middleTableColumnValueList = middleTableResultMap.keySet();
                // 构建查询条件
                queryWrapper.in(S.toSnakeCase(referencedEntityPrimaryKey), middleTableColumnValueList);
                // 查询entity列表
                List<T> list = referencedService.getEntityList(queryWrapper);
                // 绑定结果
                bindingResult(S.toLowerCaseCamel(referencedEntityPrimaryKey), list, middleTableResultMap);
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
     * 绑定结果
     * @param doPkPropName
     * @param list
     */
    protected void bindingResult(String doPkPropName, List<T> list, Map<Object, Object> middleTableResultMap) {
        Map<String, T> valueEntityMap = new HashMap<>(list.size());
        for(T entity : list){
            Object pkValue = BeanUtils.getProperty(entity, doPkPropName);
            Object annoObjFK = middleTableResultMap.get(pkValue);
            if(annoObjFK != null){
                valueEntityMap.put(String.valueOf(annoObjFK), entity);
            }
            else{
                log.warn("转换结果异常，中间关联条件数据不一致");
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
    protected void bindingResult(String doPkPropName, List<T> list) {
        Map<Object, T> valueEntityMap = new HashMap<>(list.size());
        for(T entity : list){
            Object pkValue = BeanUtils.getProperty(entity, doPkPropName);
            valueEntityMap.put(String.valueOf(pkValue), entity);
        }
        // 绑定
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityMap);
    }

}
