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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.config.Cons;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.entity.BaseModel;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
* 登录记录 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Getter @Setter @Accessors(chain = true)
@TableName("dbt_iam_login_trace")
public class IamLoginTrace extends BaseEntity<String> {
    private static final long serialVersionUID = -6166037224391478085L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    // 用户类型
    @NotNull(message = "用户类型不能为空")
    @Length(max=100, message="用户类型长度应小于100")
    @TableField()
    private String userType;

    // 用户ID
    @NotNull(message = "用户ID不能为空")
    @TableField()
    private String userId;

    // 认证方式
    @NotNull(message = "认证方式不能为空")
    @Length(max=20, message="认证方式长度应小于20")
    @TableField()
    private String authType;

    // 用户名
    @NotNull(message = "用户名不能为空")
    @Length(max=100, message="用户名长度应小于100")
    @TableField()
    private String authAccount;

    // 是否成功
    @TableField("is_success")
    private Boolean isSuccess;

    @Length(max=50, message="IP长度应小于50")
    @TableField()
    private String ipAddress;

    @Length(max=200, message="User-Agent长度应小于200")
    @TableField()
    private String userAgent;

    /**
     * 退出时间
     */
    @TableField()
    private LocalDateTime logoutTime;

    public IamLoginTrace setUserAgent(String userAgent){
        if(V.notEmpty(userAgent) && userAgent.length() > 200){
            userAgent = S.cut(userAgent, 200);
        }
        this.userAgent = userAgent;
        return this;
    }

    @TableField(exist = false)
    private boolean deleted;

}
