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

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.util.D;
import com.diboot.iam.entity.BaseLoginUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 移动端登陆用户
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/8/31  10:34
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamMember extends BaseLoginUser {

    private static final long serialVersionUID = -1947077615295781979L;

    /**
     * gender字段的关联字典
     */
    public static final String DICT_GENDER = "GENDER";
    /**
     * 移动端用户状态
     */
    public static final String DICT_MEMBER_STATUS = "ACCOUNT_STATUS";

    /**
     * 租户id
     */
    @TableField()
    private Long tenantId;

    /**
     * 组织
     */
    @TableField()
    private Long orgId;

    /**
     * 组织
     */
    @TableField()
    private String openid;

    /**
     * 昵称
     */
    @NotNull(message = "昵称不能为空")
    @Length(max = 100, message = "昵称长度应小于100")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField()
    private String nickname;

    /**
     * 头像
     */
    @Length(max = 255, message = "头像长度应小于255")
    @TableField()
    private String avatarUrl;

    /**
     * 城市
     */
    @Length(max = 50, message = "国家长度应小于50")
    @TableField()
    private String country;

    /**
     * 省份
     */
    @Length(max = 50, message = "省份长度应小于50")
    @TableField()
    private String province;

    /**
     * 城市
     */
    @Length(max = 100, message = "城市长度应小于100")
    @TableField()
    private String city;

    /**
     * 手机号
     */
    @Length(max = 20, message = "手机号长度应小于20")
    @TableField()
    private String mobilePhone;

    /**
     * 邮箱
     */
    @Length(max = 100, message = "邮箱长度应小于100")
    @TableField()
    private String email;

    /**
     * 当前状态
     */
    @NotNull(message = "当前状态不能为空")
    @Length(max = 20, message = "当前状态长度应小于20")
    @TableField()
    private String status;

    /**
     * 性别
     */
    @Length(max = 10, message = "性别长度应小于10")
    @TableField()
    private String gender;

    /**
     * 备注
     */
    @Length(max = 200, message = "用户备注应小于200")
    @TableField()
    private String description;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = D.FORMAT_DATETIME_Y4MDHM)
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Date updateTime;

    @Override
    public String getDisplayName() {
        return this.nickname;
    }

    @Override
    public String getUserType() {
        return IamMember.class.getSimpleName();
    }
}
