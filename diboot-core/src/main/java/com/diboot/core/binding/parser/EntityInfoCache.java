package com.diboot.core.binding.parser;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/17
 * Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class EntityInfoCache implements Serializable {
    private static final long serialVersionUID = 4102769515630377456L;

    private String tableName;

    private String entityClassName;

    private Map<String, String> fieldToColumnMap;

    private Map<String, String> columnToFieldMap;

    private String idColumn;

    private String deletedColumn;

    /**
     * 表对应的entity类
     */
    private Class<?> entityClass;
    /**
     * service 实现类
     */
    @Setter
    private IService service;
    /**
     * 表对应的mapper类
     */
    private BaseMapper baseMapper;

    public EntityInfoCache(Class<?> entityClass, IService iService){
        this.entityClass = entityClass;
        this.entityClassName = entityClass.getName();
        // 初始化字段-列名的映射
        this.fieldToColumnMap = new HashMap<>();
        this.columnToFieldMap = new HashMap<>();
        List<Field> fields = BeanUtils.extractAllFields(entityClass);
        if(V.notEmpty(fields)){
            for(Field fld : fields){
                String fldName = fld.getName();
                String columnName = null;
                TableField tableField = fld.getAnnotation(TableField.class);
                if(tableField != null){
                    if(tableField.exist() == false){
                        columnName = null;
                    }
                    else {
                        if (V.notEmpty(tableField.value())){
                            columnName = tableField.value();
                        }
                        else{
                            columnName = S.toSnakeCase(fldName);
                        }
                    }
                }
                // 主键
                TableId tableId = fld.getAnnotation(TableId.class);
                if(tableId != null){
                    if (V.notEmpty(tableId.value())){
                        columnName = tableId.value();
                    }
                    else if(columnName == null){
                        columnName = S.toSnakeCase(fldName);
                    }
                    this.idColumn = columnName;
                }
                else{
                    TableLogic tableLogic = fld.getAnnotation(TableLogic.class);
                    if(tableLogic != null){
                        if (V.notEmpty(tableLogic.value())){
                            columnName = tableLogic.value();
                        }
                        else if(columnName == null){
                            columnName = S.toSnakeCase(fldName);
                        }
                        this.deletedColumn = columnName;
                    }
                }
                this.fieldToColumnMap.put(fldName, columnName);
                if(V.notEmpty(columnName)){
                    this.columnToFieldMap.put(columnName, fldName);
                }
            }
        }
        // 初始化tableName
        TableName tableNameAnno = AnnotationUtils.findAnnotation(entityClass, TableName.class);
        if(tableNameAnno != null){
            this.tableName = tableNameAnno.value();
        }
        else{
            this.tableName = S.toSnakeCase(entityClass.getSimpleName());
        }
        // 设置当前service实例
        this.service = iService;
        if(iService != null){
            this.baseMapper = iService.getBaseMapper();
        }
    }

    /**
     * 设置当前service实例
     * @param iService
     */
    public void setService(IService iService){
        // 设置当前service实例
        this.service = iService;
        if(iService != null){
            this.baseMapper = iService.getBaseMapper();
        }
    }

}
