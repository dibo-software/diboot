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
package com.diboot.mobile.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.iam.entity.BaseLoginUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 移动端登录用户
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/8/31  10:34
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_member")
public class IamMember extends BaseLoginUser {

    private static final long serialVersionUID = -1947077615295781979L;

    /**
     * gender字段的关联字典
     */
    public static final String DICT_GENDER = "GENDER";
    /**
     * 移动端用户状态
     */
    public static final String DICT_MEMBER_STATUS = "MEMBER_STATUS";

    /**
     * 租户id
     */
    @TableField()
    @JsonIgnore
    private String tenantId;

    /**
     * 默认绑定IamUser
     */
    @TableField()
    private String userId;

    /**
     * 默认绑定用户类型
     */
    @TableField()
    @NotNull(message = "{validation.iamMember.userType.NotNull.message}")
    private String userType;

    /**
     * 组织
     */
    @TableField()
    private String orgId;

    /**
     * openid
     */
    @TableField()
    @NotNull(message = "{validation.iamMember.openid.NotNull.message}")
    private String openid;

    /**
     * 昵称
     */
    @Length(max = 100, message = "{validation.iamMember.nickname.Length.message}")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField()
    private String nickname;

    /**
     * 头像
     */
    @Length(max = 255, message = "{validation.iamMember.avatarUrl.Length.message}")
    @TableField()
    private String avatarUrl;

    /**
     * 城市
     */
    @Length(max = 50, message = "{validation.iamMember.country.Length.message}")
    @TableField()
    private String country;

    /**
     * 省份
     */
    @Length(max = 50, message = "{validation.iamMember.province.Length.message}")
    @TableField()
    private String province;

    /**
     * 城市
     */
    @Length(max = 100, message = "{validation.iamMember.city.Length.message}")
    @TableField()
    private String city;

    /**
     * 手机号
     */
    @Length(max = 20, message = "{validation.iamMember.mobilePhone.Length.message}")
    @TableField()
    private String mobilePhone;

    /**
     * 邮箱
     */
    @Length(max = 100, message = "{validation.iamMember.email.Length.message}")
    @TableField()
    private String email;

    /**
     * 当前状态
     */
    @NotNull(message = "{validation.iamMember.status.NotNull.message}")
    @Length(max = 20, message = "{validation.iamMember.status.Length.message}")
    @TableField()
    private String status;

    /**
     * 性别
     */
    @Length(max = 10, message = "{validation.iamMember.gender.Length.message}")
    @TableField()
    private String gender;

    /**
     * 备注
     */
    @Length(max = 200, message = "{validation.iamMember.description.Length.message}")
    @TableField()
    private String description;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @Override
    public String getDisplayName() {
        return this.nickname;
    }

    @Override
    public String getUserType() {
        return this.userType;
    }

    public String getTenantId() {
        return tenantId;
    }
}
