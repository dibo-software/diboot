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
package com.diboot.file.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * file基础父类
 * @author wangyl@dibo.ltd
 * @version v2.0
 * @date 2019/07/18
 */
@Getter @Setter @Accessors(chain = true)
public class UploadFile extends BaseEntity {
    private static final long serialVersionUID = 201L;

    // 废弃默认主键
    @TableField(exist = false)
    private Long id;
    // 声明新主键uuid
    @TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private Long tenantId;

    /**
     * 应用模块
     */
    @TableField
    private String appModule;

    @NotNull(message = "关联对象类不能为空！")
    @TableField
    private String relObjType = null;
    @TableField
    @NotNull(message = "关联对象ID不能为空！")
    private String relObjId;

    @TableField
    @NotNull(message = "关联对象属性不能为空！")
    private String relObjField;

    @TableField
    @BindQuery(comparison = Comparison.LIKE)
    @NotNull(message = "文件名不能为空！")
    @Length(max = 100, message = "文件名长度超出了最大限制！")
    private String fileName;

    @TableField
    @JsonIgnore
    private String storagePath;

    /**
     * 访问URL
     */
    @TableField
    private String accessUrl;

    @TableField
    private String fileType;

    /**
     * 文件包含记录数
     */
    @TableField
    @BindQuery(ignore = true)
    private int dataCount = 0;

    @TableField
    @Length(max = 100, message = "备注长度超出了最大限制！")
    private String description;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

}
