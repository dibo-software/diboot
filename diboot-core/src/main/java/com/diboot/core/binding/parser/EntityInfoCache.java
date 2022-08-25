package com.diboot.core.binding.parser;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;

/**
 * Entity相关信息缓存
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
@Getter
@Setter
public class EntityInfoCache implements Serializable {
    private static final long serialVersionUID = 4102769515630377456L;

    private String tableName;

    private String entityClassName;

    /**
     * 属性信息
     */
    private PropInfo propInfo;

    /**
     * 表对应的entity类
     */
    private Class<?> entityClass;
    /**
     * service 实例名
     */
    private String serviceBeanName;
    /**
     * 表对应的mapper类
     */
    private Class<? extends BaseMapper> mapperClass;

    public EntityInfoCache(Class<?> entityClass, String serviceBeanName){
        this.entityClass = entityClass;
        this.entityClassName = entityClass.getName();
        // 初始化字段-列名的映射
        this.propInfo = BindingCacheManager.getPropInfoByClass(entityClass);
        // 初始化tableName
        TableName tableNameAnno = AnnotationUtils.findAnnotation(entityClass, TableName.class);
        if(tableNameAnno != null && V.notEmpty(tableNameAnno.value())){
            this.tableName = tableNameAnno.value();
        }
        else{
            this.tableName = S.toSnakeCase(entityClass.getSimpleName());
        }
        // 设置当前service实例名
        this.serviceBeanName = serviceBeanName;
    }

    /**
     * 设置当前service实例名
     * @param serviceBeanName
     */
    public void setService(String serviceBeanName){
        this.serviceBeanName = serviceBeanName;
    }

    public IService getService(){
        return this.serviceBeanName == null ? null : (IService) ContextHelper.getApplicationContext().getBean(this.serviceBeanName);
    }

    public void setBaseMapper(Class<? extends BaseMapper> mapper) {
        this.mapperClass = mapper;
    }

    public BaseMapper getBaseMapper() {
        return mapperClass == null ? getService().getBaseMapper() : ContextHelper.getBean(this.mapperClass);
    }

    /**
     * 根据列名获取字段
     * @return
     */
    public String getFieldByColumn(String columnName){
        if(this.propInfo == null){
            return null;
        }
        return this.propInfo.getFieldByColumn(columnName);
    }

    /**
     * 根据列名获取字段
     * @return
     */
    public String getColumnByField(String fieldName){
        if(this.propInfo == null){
            return null;
        }
        return this.propInfo.getColumnByField(fieldName);
    }

    /**
     * 获取ID列
     * @return
     */
    public String getIdColumn(){
        if(this.propInfo == null){
            return null;
        }
        return this.propInfo.getIdColumn();
    }

    /**
     * 获取逻辑删除标记列
     * @return
     */
    public String getDeletedColumn(){
        if(this.propInfo == null){
            return null;
        }
        return this.propInfo.getDeletedColumn();
    }

    /**
     * 是否包含某字段
     * @param column
     * @return
     */
    public boolean containsColumn(String column){
        return this.propInfo.getColumns().contains(column);
    }

}
