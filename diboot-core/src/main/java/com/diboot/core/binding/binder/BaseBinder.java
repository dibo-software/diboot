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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 关系绑定Binder父类
 * @author Mazhicheng
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
     * VO注解对象中的外键属性
     */
    protected String annoObjectForeignKey;
    /**
     * 被关联对象的Service实例
     */
    protected IService<T> referencedService;
    /***
     * DO对象中的主键属性名
     */
    protected String referencedEntityPrimaryKey;
    /**
     * 初始化QueryWrapper
     */
    protected QueryWrapper queryWrapper;

    /**
     * 多对多关联的桥接表，如 user_role<br>
     * 多对多注解示例: id=user_role.user_id AND user_role.role_id=id
     */
    protected MiddleTable middleTable;

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
        this.annoObjectForeignKey = S.toLowerCaseCamel(annoObjectForeignKey);
        this.referencedEntityPrimaryKey = S.toLowerCaseCamel(referencedEntityPrimaryKey);
        return this;
    }

    public BaseBinder<T> andEQ(String fieldName, Object value){
        queryWrapper.eq(S.toSnakeCase(fieldName), formatValue(value));
        return this;
    }
    public BaseBinder<T> andNE(String fieldName, Object value){
        queryWrapper.ne(S.toSnakeCase(fieldName), formatValue(value));
        return this;
    }
    public BaseBinder<T> andGT(String fieldName, Object value){
        queryWrapper.gt(S.toSnakeCase(fieldName), formatValue(value));
        return this;
    }
    public BaseBinder<T> andGE(String fieldName, Object value){
        queryWrapper.ge(S.toSnakeCase(fieldName), formatValue(value));
        return this;
    }
    public BaseBinder<T> andLT(String fieldName, Object value){
        queryWrapper.lt(S.toSnakeCase(fieldName), formatValue(value));
        return this;
    }
    public BaseBinder<T> andLE(String fieldName, Object value){
        queryWrapper.le(S.toSnakeCase(fieldName), formatValue(value));
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
        queryWrapper.between(S.toSnakeCase(fieldName), formatValue(begin), formatValue(end));
        return this;
    }
    public BaseBinder<T> andLike(String fieldName, String value){
        queryWrapper.like(S.toSnakeCase(fieldName), formatValue(value));
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
        queryWrapper.notBetween(S.toSnakeCase(fieldName), formatValue(begin), formatValue(end));
        return this;
    }
    public BaseBinder<T> andNotLike(String fieldName, String value){
        queryWrapper.notLike(S.toSnakeCase(fieldName), formatValue(value));
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
     * @param value
     * @return
     */
    private Object formatValue(Object value){
        if(value instanceof String && S.contains((String)value, "'")){
            value = S.replace((String)value, "'", "");
        }
        return value;
    }
}