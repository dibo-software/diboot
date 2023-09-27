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

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.diboot.core.util.D;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
* 系统用户 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Getter @Setter @Accessors(chain = true)
public class IamUser extends BaseLoginUser {
    private static final long serialVersionUID = -8462352695775599715L;
    /**
     * 更改id为雪花
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private Long tenantId;

    // 组织ID
    @NotNull(message = "组织ID不能为空")
    @TableField()
    private Long orgId;

    @NotNull(message = "用户编号不能为空")
    @Length(max=20, message="用户编号长度应小于50")
    @TableField()
    private String userNum;

    // 真实姓名
    @NotNull(message = "真实姓名不能为空")
    @Length(max=50, message="真实姓名长度应小于50")
    @TableField()
    private String realname;

    // 性别
    @NotNull(message = "性别不能为空")
    @Length(max=10, message="性别长度应小于10")
    @TableField()
    private String gender;

    // 手机号
    @Length(max=20, message="手机号长度应小于20")
    @TableField()
    private String mobilePhone;

    // Email
    @Length(max=50, message="Email长度应小于50")
    @TableField()
    private String email;

    /**
     * 生日
     */
    @JsonFormat(pattern=D.FORMAT_DATE_Y4MD)
    @TableField()
    private Date birthdate;

    // 状态
    @NotNull(message = "状态不能为空")
    @Length(max=10, message="状态长度应小于10")
    @TableField()
    private String status;

    // 头像
    @TableField()
    private String avatarUrl;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Override
    public String getDisplayName() {
        return this.realname;
    }

    @Override
    public String getUserType() {
        return IamUser.class.getSimpleName();
    }

}