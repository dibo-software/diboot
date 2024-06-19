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
package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.diboot.core.util.D;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
* 系统用户 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Getter @Setter @Accessors(chain = true)
@TableName("dbt_iam_user")
public class IamUser extends BaseLoginUser {
    private static final long serialVersionUID = -8462352695775599715L;

    /**
     * 租户ID
     */
    @TableField
    private String tenantId;

    // 组织ID
    @NotNull(message = "{validation.iamUser.orgId.NotNull.message}")
    @TableField()
    private String orgId;

    @NotNull(message = "{validation.iamUser.userNum.NotNull.message}")
    @Length(max=20, message="{validation.iamUser.userNum.Length.message}")
    @TableField()
    private String userNum;

    // 真实姓名
    @NotNull(message = "{validation.iamUser.realname.NotNull.message}")
    @Length(max=50, message="{validation.iamUser.realname.Length.message}")
    @TableField()
    private String realname;

    // 性别
    @NotNull(message = "{validation.iamUser.gender.NotNull.message}")
    @Length(max=10, message="{validation.iamUser.gender.Length.message}")
    @TableField()
    private String gender;

    // 手机号
    @Length(max=20, message="{validation.iamUser.mobilePhone.Length.message}")
    @TableField()
    private String mobilePhone;

    // Email
    @Length(max=50, message="{validation.iamUser.email.Length.message}")
    @TableField()
    private String email;

    /**
     * 生日
     */
    @JsonFormat(pattern=D.FORMAT_DATE_Y4MD)
    @TableField()
    private LocalDate birthdate;

    // 状态
    @NotNull(message = "{validation.iamUser.status.NotNull.message}")
    @Length(max=10, message="{validation.iamUser.status.Length.message}")
    @TableField()
    private String status;

    // 排序号
    @TableField()
    private Long sortId;

    // 头像
    @TableField()
    private String avatarUrl;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public String getDisplayName() {
        return this.realname;
    }

    @Override
    public String getUserType() {
        return IamUser.class.getSimpleName();
    }

    @Override
    public String getTenantId() {
        return this.tenantId;
    }

}
