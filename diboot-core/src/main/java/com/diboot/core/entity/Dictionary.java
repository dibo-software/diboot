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
package com.diboot.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.diboot.core.binding.annotation.BindI18n;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.vo.LabelValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据字典实体
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "dbt_dictionary", autoResultMap = true)
public class Dictionary extends BaseEntity<String> {
    private static final long serialVersionUID = 11301L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    /***
     * 上级ID
     */
    @TableField
    private String parentId;

    /**
     * 应用模块
     */
    @TableField
    private String appModule;

    /***
     * 数据字典类型
     */
    @NotNull(message = "{validation.dictionary.type.NotNull.message}")
    @Length(max = 50, message = "{validation.dictionary.type.Length.message}")
    @TableField
    private String type;

    /***
     * 数据字典项的显示名称
     */
    @NotNull(message = "{validation.dictionary.itemName.NotNull.message}")
    @Length(max = 100, message = "{validation.dictionary.itemName.Length.message}")
    @BindQuery(comparison = Comparison.LIKE)
    @BindI18n("itemNameI18n")
    private String itemName;

    /**
     * 数据字典项的显示名称国际化资源标识
     */
    private String itemNameI18n;

    /***
     * 数据字典项的存储值（编码）
     */
    @Length(max = 100, message = "{validation.dictionary.itemValue.Length.message}")
    @TableField
    private String itemValue;

    /***
     * 备注信息
     */
    @Length(max = 200, message = "{validation.dictionary.description.Length.message}")
    @TableField
    private String description;

    /***
     * 排序号
     */
    @TableField
    private Integer sortId;

    /***
     * 是否为系统预置（预置不可删除）
     */
    @TableField("is_deletable")
    private Boolean isDeletable;

    /***
     * 是否可编辑
     */
    @TableField("is_editable")
    private Boolean isEditable;

    /**
     * 扩展字段
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extension;

    /***
     * 从extdata JSON中提取扩展属性值
     * @param extAttrName
     * @return
     */
    public Object getFromExt(String extAttrName){
        if(this.extension == null){
            return null;
        }
        return this.extension.get(extAttrName);
    }

    /***
     * 添加扩展属性和值到extdata JSON中
     * @param extAttrName
     * @param extAttrValue
     */
    public Dictionary addIntoExt(String extAttrName, Object extAttrValue){
        if(extAttrName == null && extAttrValue == null){
            return this;
        }
        if(this.extension == null){
            this.extension = new LinkedHashMap<>();
        }
        this.extension.put(extAttrName, extAttrValue);
        return this;
    }

    /**
     * 转换为选项
     * @return
     */
    public LabelValue toLabelValue() {
        return new LabelValue(this.getItemName(), this.getItemValue()).setExt(this.getExtension());
    }

}
