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
package com.diboot.iam.shiro;

import com.diboot.core.util.S;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.util.TokenUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

import java.util.Map;

/**
 * IAM 认证token定义
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/26
 * Copyright © diboot.com
 */
@Getter @Setter
@Slf4j
public class IamAuthToken implements RememberMeAuthenticationToken {
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

    private Object principal;

    private Object credentials;

    private int expiresInMinutes;

    public IamAuthToken(){
    }

    /***
     * 初始化认证token
     * @param authType 认证方式
     * @param userTypeClass 用户类型Class
     */
    public IamAuthToken(String authType, Class userTypeClass){
        this.authType = authType;
        this.userTypeClass = userTypeClass;
    }

    public IamAuthToken(String userInfoStr){
        String[] fields = S.split(userInfoStr, Cons.SEPARATOR_COMMA);
        this.tenantId = Long.parseLong(fields[0]);
        this.authAccount = fields[1];
        if(IamUser.class.getSimpleName().equals(fields[2]) != true){
            try {
                this.userTypeClass = Class.forName(fields[2]);
            }
            catch (ClassNotFoundException e) {
                log.debug("Token验证失败！用户类型{}不存在", fields[2]);
            }
        }
        this.authType = fields[3];
        this.expiresInMinutes = Integer.parseInt(fields[4]);
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
     * 生成token UUID
     * @return
     */
    public IamAuthToken generateAuthtoken(){
        this.authtoken = TokenUtils.generateToken();
        return this;
    }

    @Override
    public boolean isRememberMe() {
        return rememberMe;
    }

    /**
     * 构建唯一串用于缓存
     * @return
     */
    public String buildUserInfoStr(){
        return S.joinWith(Cons.SEPARATOR_COMMA,
                this.getTenantId(), this.getAuthAccount(), this.getUserTypeClass().getName(), this.getAuthType(), this.expiresInMinutes, System.currentTimeMillis());
    }
}
