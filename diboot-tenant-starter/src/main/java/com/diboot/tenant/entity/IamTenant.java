/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

/**
 * 租户 Entity定义
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_tenant")
public class
IamTenant extends BaseEntity<String> {
    private static final long serialVersionUID = 8712006740734247869L;

    /**
     * status字段的关联字典
     */
    public static final String DICT_TENANT_STATUS = "TENANT_STATUS";

    /**
     * 租户名称
     */
    @NotNull(message = "{validation.iamTenant.name.NotNull.message}")
    @Length(max = 100, message = "{validation.iamTenant.name.Length.message}")
    @TableField()
    private String name;

    /**
     * 租户code
     */
    @NotNull(message = "{validation.iamTenant.code.NotNull.message}")
    @Length(max = 20, message = "{validation.iamTenant.code.Length.message}")
    @TableField()
    private String code;

    /**
     * 有效开始日期
     */
    @NotNull(message = "{validation.iamTenant.startDate.NotNull.message}")
    @TableField()
    private LocalDate startDate;

    /**
     * 有效结束日期
     */
    @NotNull(message = "{validation.iamTenant.endDate.NotNull.message}")
    @TableField()
    private LocalDate endDate;

    /**
     * 负责人
     */
    @Length(max = 50, message = "{validation.iamTenant.manager.Length.message}")
    @BindQuery(comparison = Comparison.LIKE)
    private String manager;

    /**
     * 联系电话
     */
    @Length(max = 20, message = "{validation.iamTenant.phone.Length.message}")
    @TableField()
    private String phone;

    /**
     * 描述
     */
    @Length(max = 100, message = "{validation.iamTenant.description.Length.message}")
    @TableField()
    private String description;

    /**
     * 租户状态
     */
    @Length(max = 10, message = "{validation.iamTenant.status.Length.message}")
    @TableField()
    private String status;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;


}
