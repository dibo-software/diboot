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
package com.diboot.iam.jwt;

import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.util.JwtUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

import java.util.Map;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Getter @Setter @Accessors(chain = true)
@Slf4j
public class BaseJwtAuthToken implements RememberMeAuthenticationToken {
    private static final long serialVersionUID = -5518501153334708409L;

    /**
     * 用户类型的Class
     */
    private Class userTypeClass = IamUser.class;
    /**
     * 认证类型，密码/微信...
     */
    private String authType;
    /**
     * 认证账号
     */
    private String authAccount;
    /**
     * 认证凭证
     */
    private String authSecret;
    /**
     * 记住我
     */
    private boolean rememberMe;

    /**
     * 扩展属性
     */
    private Map<String, Object> extObj;

    /**authz token*/
    private String authtoken;

    /**
     * 租户id
     */
    private Long tenantId = 0L;

    /**
     * 是否校验密码
     */
    private boolean validPassword = true;

    public BaseJwtAuthToken(){
    }

    /***
     * 初始化认证token
     * @param authType 认证方式
     * @param userTypeClass 用户类型Class
     */
    public BaseJwtAuthToken(String authType, Class userTypeClass){
        this.authType = authType;
        this.userTypeClass = userTypeClass;
    }

    /***
     * 验证失败的时候清空token
     */
    public void clearAuthtoken(){
        this.authtoken = null;
    }

    /**
     * 设置
     * @return
     */
    @Override
    public Object getPrincipal() {
        return this.authtoken;
    }

    @Override
    public Object getCredentials() {
        return this.authtoken;
    }

    /**
     * 获取用户类型
     * @return
     */
    public String getUserType(){
        return userTypeClass.getSimpleName();
    }



    /**
     * 生成token  tenantId,account,userTypeClass,authType,60
     * @param expiresInMinutes
     * @return
     */
    public BaseJwtAuthToken generateAuthtoken(int expiresInMinutes){
        String tokenInput = this.tenantId + Cons.SEPARATOR_COMMA
                            + this.authAccount + Cons.SEPARATOR_COMMA
                            + this.userTypeClass.getTypeName() + Cons.SEPARATOR_COMMA
                            + this.authType + Cons.SEPARATOR_COMMA
                            + expiresInMinutes;
        this.authtoken = JwtUtils.generateToken(tokenInput, expiresInMinutes);
        return this;
    }

    @Override
    public boolean isRememberMe() {
        return rememberMe;
    }
}
