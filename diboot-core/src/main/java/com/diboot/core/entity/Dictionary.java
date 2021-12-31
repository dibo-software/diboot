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
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 数据字典实体
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter @Setter @Accessors(chain = true)
public class Dictionary extends BaseExtEntity {
    private static final long serialVersionUID = 11301L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private Long tenantId;

    /***
     * 上级ID
     */
    @NotNull(message = "上级ID不能为空，如无请设为0")
    @TableField
    private Long parentId = 0L;

    /**
     * 应用模块
     */
    @TableField
    private String appModule;

    /***
     * 数据字典类型
     */
    @NotNull(message = "数据字典类型不能为空！")
    @Length(max = 50, message = "数据字典类型长度超长！")
    @TableField
    private String type;

    /***
     * 数据字典项的显示名称
     */
    @NotNull(message = "数据字典项名称不能为空！")
    @Length(max = 100, message = "数据字典项名称长度超长！")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField
    private String itemName;

    /***
     * 数据字典项的存储值（编码）
     */
    @Length(max = 100, message = "数据字典项编码长度超长！")
    @TableField
    private String itemValue;

    /***
     * 备注信息
     */
    @Length(max = 200, message = "数据字典备注长度超长！")
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

}
