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
package com.diboot.iam.dto;

import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 登录凭证
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class AuthCredential implements Serializable {
    private static final long serialVersionUID = -4721950772621829194L;

    /**
     * 唯一标识
     */
    private String traceId;

    /**
     * 用户类型的Class
     */
    private Class<? extends BaseLoginUser> userTypeClass = IamUser.class;
    /**
     * 用户类型
     */
    private String userType;

    @NotNull(message = "{validation.authCredential.authType.NotNull.message}")
    private String authType;
    /**
     * 记住我
     */
    private boolean rememberMe;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 扩展属性
     */
    private Map<String, Object> extObj;

    /**
     * 账号
     * @return
     */
    public abstract String getAuthAccount();

    /**
     * 认证密码凭证
      */
    public abstract String getAuthSecret();

    /**
     * 获取用户类型
     * @return
     */
    public String getUserType(){
        if(userType != null){
            return userType;
        }
        return userTypeClass.getSimpleName();
    }

    /**
     * 指定用户类型class
     * @param userTypeClass
     */
    public void setUserTypeClass(Class<? extends BaseLoginUser> userTypeClass){
       this.userTypeClass = userTypeClass;
       if(this.userType == null){
           this.userType = userTypeClass.getSimpleName();
       }
    }

}
