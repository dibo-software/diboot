/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 组织机构 Entity定义
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2019-12-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_org")
public class IamOrg extends BaseEntity {

    private static final long serialVersionUID = 8942911223090443934L;

    /**
     * 组织树的虚拟根节点 ID
     */
    public static final String VIRTUAL_ROOT_ID = "0";

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    /**
     * 上级ID
     */
    @TableField()
    private String parentId;

    /**
     * 上级ID路径
     */
    @TableField()
    private String parentIdsPath;

    /**
     * 企业ID
     */
    @TableField()
    private String rootOrgId;

    // 名称
    @NotNull(message = "名称不能为空")
    @Length(max = 100, message = "名称长度应小于100")
    @TableField()
    private String name;

    // 权限类别
    @NotNull(message = "组织类别不能为空")
    @Length(max = 100, message = "组织类别长度应小于100")
    @TableField()
    private String type;

    // 编码
    @NotNull(message = "编码不能为空")
    @Length(max = 50, message = "编码长度应小于50")
    @TableField()
    private String code;

    @TableField()
    private String managerId;

    // 排序号
    @TableField()
    private Long sortId;

    @TableField
    private String status;

    /**
     * 组织备注
     */
    private String orgComment;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}
