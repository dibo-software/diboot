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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.parser.MiddleTable;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.IGetter;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 关系绑定Binder父类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/19
 */
public abstract class BaseBinder<T> {
    private static final Logger log = LoggerFactory.getLogger(BaseBinder.class);
    /***
     * 需要绑定到的VO注解对象List
     */
    protected List annoObjectList;
    /***
     * VO注解对象中的join on对象属性集合
     */
    protected List<String> annoObjJoinFlds;
    /***
     * DO对象中的关联join on对象属性集合
     */
    protected List<String> refObjJoinFlds;
    /**
     * 被关联对象的Service实例
     */
    protected IService<T> referencedService;
    /**
     * 初始化QueryWrapper
     */
    protected QueryWrapper<T> queryWrapper;

    /**
     * 多对多关联的桥接表，如 user_role<br>
     * 多对多注解示例: id=user_role.user_id AND user_role.role_id=id
     */
    protected MiddleTable middleTable;

    protected Class<T> referencedEntityClass;

    public static final String NOT_SUPPORT_MSG = "中间表关联暂不支持涉及目标表多列的情况!";

    /***
     * 构造方法
     * @param serviceInstance
     * @param voList
     */
    public BaseBinder(IService<T> serviceInstance, List voList){
        this.referencedService = serviceInstance;
        this.annoObjectList = voList;
        this.queryWrapper = new QueryWrapper<>();
        this.referencedEntityClass = BeanUtils.getGenericityClass(referencedService, 1);
        this.annoObjJoinFlds = new ArrayList<>(8);
        this.refObjJoinFlds = new ArrayList<>(8);
    }

    /**
     * join连接条件，指定当前VO的取值方法和关联entity的取值方法
     * @param annoObjectFkGetter 当前VO的取值方法
     * @param referencedEntityPkGetter 关联entity的取值方法
     * @param <T1> 当前VO的对象类型
     * @param <T2> 关联对象entity类型
     * @return
     */
    public <T1,T2> BaseBinder<T> joinOn(IGetter<T1> annoObjectFkGetter, IGetter<T2> referencedEntityPkGetter){
        return joinOn(BeanUtils.convertToFieldName(annoObjectFkGetter), BeanUtils.convertToFieldName(referencedEntityPkGetter));
    }

    /**
     * join连接条件，指定当前VO的取值方法和关联entity的取值方法
     * @param annoObjectForeignKey 当前VO的取值属性名
     * @param referencedEntityPrimaryKey 关联entity的属性
     * @return
     */
    public BaseBinder<T> joinOn(String annoObjectForeignKey, String referencedEntityPrimaryKey){
        if(annoObjectForeignKey != null && referencedEntityPrimaryKey != null){
            annoObjJoinFlds.add(S.toLowerCaseCamel(annoObjectForeignKey));
            refObjJoinFlds.add(S.toLowerCaseCamel(referencedEntityPrimaryKey));
        }
        return this;
    }

    public BaseBinder<T> andEQ(String fieldName, Object value){
        queryWrapper.eq(S.toSnakeCase(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andNE(String fieldName, Object value){
        queryWrapper.ne(S.toSnakeCase(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andGT(String fieldName, Object value){
        queryWrapper.gt(S.toSnakeCase(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andGE(String fieldName, Object value){
        queryWrapper.ge(S.toSnakeCase(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andLT(String fieldName, Object value){
        queryWrapper.lt(S.toSnakeCase(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andLE(String fieldName, Object value){
        queryWrapper.le(S.toSnakeCase(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andIsNotNull(String fieldName){
        queryWrapper.isNotNull(S.toSnakeCase(fieldName));
        return this;
    }
    public BaseBinder<T> andIsNull(String fieldName){
        queryWrapper.isNull(S.toSnakeCase(fieldName));
        return this;
    }
    public BaseBinder<T> andBetween(String fieldName, Object begin, Object end){
        queryWrapper.between(S.toSnakeCase(fieldName), formatValue(fieldName, begin), formatValue(fieldName, end));
        return this;
    }
    public BaseBinder<T> andLike(String fieldName, String value){
        fieldName = S.toSnakeCase(fieldName);
        value = (String)formatValue(fieldName, value);
        if(S.startsWith(value, "%")){
            value = S.substringAfter(value, "%");
            if(S.endsWith(value, "%")){
                value = S.substringBeforeLast(value, "%");
                queryWrapper.like(fieldName, value);
            }
            else{
                queryWrapper.likeLeft(fieldName, value);
            }
        }
        else if(S.endsWith(value, "%")){
            value = S.substringBeforeLast(value, "%");
            queryWrapper.likeRight(fieldName, value);
        }
        else{
            queryWrapper.like(fieldName, value);
        }
        return this;
    }
    public BaseBinder<T> andIn(String fieldName, Collection valueList){
        queryWrapper.in(S.toSnakeCase(fieldName), valueList);
        return this;
    }
    public BaseBinder<T> andNotIn(String fieldName, Collection valueList){
        queryWrapper.notIn(S.toSnakeCase(fieldName), valueList);
        return this;
    }
    public BaseBinder<T> andNotBetween(String fieldName, Object begin, Object end){
        queryWrapper.notBetween(S.toSnakeCase(fieldName), formatValue(fieldName, begin), formatValue(fieldName, end));
        return this;
    }
    public BaseBinder<T> andNotLike(String fieldName, String value){
        queryWrapper.notLike(S.toSnakeCase(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andApply(String applySql){
        queryWrapper.apply(applySql);
        return this;
    }
    public BaseBinder<T> withMiddleTable(MiddleTable middleTable){
        this.middleTable = middleTable;
        return this;
    }

    /***
     * 执行绑定, 交由子类实现
     */
    public abstract void bind();

    /**
     * 构建join on
     */
    protected void buildQueryWrapperJoinOn(){
        for(int i = 0; i< annoObjJoinFlds.size(); i++){
            String annoObjJoinOnCol = annoObjJoinFlds.get(i);
            boolean[] hasNullFlags = new boolean[1];
            List annoObjectJoinOnList = BeanUtils.collectToList(annoObjectList, annoObjJoinOnCol, hasNullFlags);
            // 构建查询条件
            String refObjJoinOnCol = S.toSnakeCase(refObjJoinFlds.get(i));
            if(V.isEmpty(annoObjectJoinOnList)){
                queryWrapper.isNull(refObjJoinOnCol);
            }
            else{
                // 有null值
                if(hasNullFlags[0]){
                    queryWrapper.and(qw -> qw.isNull(refObjJoinOnCol).or(w -> w.in(refObjJoinOnCol, annoObjectJoinOnList)));
                }
                else{
                    queryWrapper.in(refObjJoinOnCol, annoObjectJoinOnList);
                }
            }
        }
    }

    /**
     * 获取EntityList
     * @param queryWrapper
     * @return
     */
    protected List<T> getEntityList(Wrapper queryWrapper) {
        if(referencedService instanceof BaseService){
            return ((BaseService)referencedService).getEntityList(queryWrapper);
        }
        else{
            List<T> list = referencedService.list(queryWrapper);
            return checkedList(list);
        }
    }

    /**
     * 获取Map结果
     * @param queryWrapper
     * @return
     */
    protected List<Map<String, Object>> getMapList(Wrapper queryWrapper) {
        if(referencedService instanceof BaseService){
            return ((BaseService)referencedService).getMapList(queryWrapper);
        }
        else{
            List<Map<String, Object>> list = referencedService.listMaps(queryWrapper);
            return checkedList(list);
        }
    }

    /**
     * 构建中间表查询参数map
     * @return
     */
    protected Map<String, List> buildTrunkObjCol2ValuesMap(){
        // 提取注解条件中指定的对应的列表
        Map<String, List> trunkObjCol2ValuesMap = new HashMap<>();
        for(Map.Entry<String, String> entry : middleTable.getTrunkObjColMapping().entrySet()){
            String annoObjFld = S.toLowerCaseCamel(entry.getKey());
            List valueList = BeanUtils.collectToList(annoObjectList, annoObjFld);
            trunkObjCol2ValuesMap.put(entry.getValue(), valueList);
        }
        return trunkObjCol2ValuesMap;
    }

    /**
     * 从map中取值，如直接取为null尝试转换大写后再取，以支持ORACLE等大写命名数据库
     * @param map
     * @param key
     * @return
     */
    protected Object getValueIgnoreKeyCase(Map<String, Object> map, String key){
        if(key == null){
            return null;
        }
        if(map.containsKey(key)){
            return map.get(key);
        }
        if(map.containsKey(key.toUpperCase())){
            return map.get(key.toUpperCase());
        }
        return null;
    }

    /**
     * 从Map中提取ID的值
     * @param middleTableResultMap
     * @return
     */
    protected List extractIdValueFromMap(Map<String, List> middleTableResultMap) {
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

    /**
     * 检查list，结果过多打印warn
     * @param list
     * @return
     */
    private List checkedList(List list){
        if(list == null){
            list = Collections.emptyList();
        }
        else if(list.size() > BaseConfig.getBatchSize()){
            log.warn("单次查询记录数量过大，返回结果数={}，请检查！", list.size());
        }
        return list;
    }

    /**
     * 格式化条件值
     * @param fieldName 属性名
     * @param value 值
     * @return
     */
    private Object formatValue(String fieldName, Object value){
        if(value instanceof String && S.contains((String)value, "'")){
            return S.replace((String)value, "'", "");
        }
        // 转型
        if(this.referencedEntityClass != null){
            Field field = BeanUtils.extractField(this.referencedEntityClass, S.toLowerCaseCamel(fieldName));
            if(field != null){
                return BeanUtils.convertValueToFieldType(value, field);
            }
        }
        return value;
    }

}