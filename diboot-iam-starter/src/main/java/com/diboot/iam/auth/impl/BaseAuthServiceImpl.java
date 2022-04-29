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
package com.diboot.iam.auth.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.process.IamAsyncWorker;
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.iam.util.HttpHelper;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户名密码认证的service实现
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/25
 */
@Slf4j
public abstract class BaseAuthServiceImpl implements AuthService {
    @Autowired
    private IamAccountService accountService;
    @Autowired
    private IamAsyncWorker iamAsyncWorker;
    @Autowired
    private HttpServletRequest request;

    @Override
    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.PWD.name();
    }

    /**
     * 构建查询条件
     * @return
     */
    protected abstract Wrapper buildQueryWrapper(IamAuthToken iamAuthToken);

    @Override
    public IamAccount getAccount(IamAuthToken iamAuthToken) throws AuthenticationException {
        IamAccount latestAccount = accountService.getSingleEntity(buildQueryWrapper(iamAuthToken));
        if(latestAccount == null){
            return null;
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.I.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException("用户账号已禁用! account="+iamAuthToken.getAuthAccount());
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.L.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException("用户账号已锁定! account="+iamAuthToken.getAuthAccount());
        }
        return latestAccount;
    }

    @Override
    public String applyToken(AuthCredential credential) {
        IamAuthToken authToken = initAuthToken(credential);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(authToken);
            if (subject.isAuthenticated()) {
                String accessToken = (String) authToken.getCredentials();
                // 缓存当前token与用户信息
                TokenUtils.cacheAccessToken(accessToken, authToken.buildUserInfoStr(), authToken.getExpiresInMinutes());
                log.debug("申请token成功！authtoken={}", authToken.getCredentials());
                saveLoginTrace(authToken, true);
                // 返回
                return accessToken;
            }
            else {
                log.error("认证失败");
                saveLoginTrace(authToken, false);
                throw new BusinessException(Status.FAIL_OPERATION, "认证失败");
            }
        } catch (Exception e) {
            log.error("登录异常", e);
            saveLoginTrace(authToken, false);
            throw new BusinessException(Status.FAIL_OPERATION, e.getMessage());
        }
    }

    /**
     * 初始化JwtAuthToken实例
     * @param credential
     * @return
     */
    protected IamAuthToken initAuthToken(AuthCredential credential){
        IamAuthToken token = new IamAuthToken(getAuthType(), credential.getUserTypeClass());
        // 设置账号密码
        token.setAuthAccount(credential.getAuthAccount());
        token.setAuthSecret(credential.getAuthSecret());
        token.setRememberMe(credential.isRememberMe());
        token.setTenantId(credential.getTenantId());
        token.setExtObj(credential.getExtObj());
        token.setExpiresInMinutes(getExpiresInMinutes());
        // 生成token
        return token.generateAuthtoken();
    }

    /**
     * 保存登录日志
     * @param authToken
     * @param isSuccess
     */
    protected void saveLoginTrace(IamAuthToken authToken, boolean isSuccess){
        IamLoginTrace loginTrace = new IamLoginTrace();
        loginTrace.setAuthType(getAuthType()).setAuthAccount(authToken.getAuthAccount()).setUserType(authToken.getUserType()).setSuccess(isSuccess);
        BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
        if(currentUser != null){
            loginTrace.setUserId(currentUser.getId());
        }
        // 记录客户端信息
        String userAgent = HttpHelper.getUserAgent(request);
        String ipAddress = HttpHelper.getRequestIp(request);
        loginTrace.setUserAgent(userAgent).setIpAddress(ipAddress);
        iamAsyncWorker.saveLoginTraceLog(loginTrace);
    }

}
