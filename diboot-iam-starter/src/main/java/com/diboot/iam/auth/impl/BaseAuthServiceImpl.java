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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.I18n;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.process.IamAsyncWorker;
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.mapper.IamAccountMapper;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamLoginTraceService;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.iam.config.IamProperties;
import com.diboot.iam.util.HttpHelper;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired
    private IamLoginTraceService loginTraceService;
    @Autowired
    private IamProperties iamProperties;

    @Autowired
    private IamAccountMapper iamAccountMapper;

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
        List<IamAccount> latestAccounts = iamAccountMapper.findLoginAccount(buildQueryWrapper(iamAuthToken), BaseConfig.getActiveFlagValue());
        if(V.isEmpty(latestAccounts)){
            return null;
        }
        IamAccount latestAccount = latestAccounts.get(0);
        if (Cons.DICTCODE_ACCOUNT_STATUS.I.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException(I18n.message("exception.authentication.authService.accountForbidden", iamAuthToken.getAuthAccount()));
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.L.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException(I18n.message("exception.authentication.authService.accountLocked", iamAuthToken.getAuthAccount()));
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
                TokenUtils.cacheAccessToken(accessToken, authToken.buildUserInfoStr());
                log.debug("申请token成功！Authorization={}", authToken.getCredentials());
                saveLoginTrace(authToken, true);
                // 返回
                return accessToken;
            }
            else {
                log.error("认证失败");
                saveLoginTrace(authToken, false);
                throw new BusinessException(Status.FAIL_OPERATION, "exception.business.authService.authFailed");
            }
        } catch (Exception e) {
            log.error("登录异常", e);
            saveLoginTrace(authToken, false);
            throw new BusinessException(Status.FAIL_OPERATION, e.getMessage());
        }
    }

    /**
     * 初始化AuthToken实例
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
        loginTrace.setAuthType(getAuthType()).setAuthAccount(authToken.getAuthAccount()).setUserType(authToken.getUserType()).setIsSuccess(isSuccess);
        BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
        String currentUserId = currentUser == null ? Cons.ID_PREVENT_NULL : currentUser.getId();
        loginTrace.setUserId(currentUserId);
        // 记录客户端信息
        String userAgent = HttpHelper.getUserAgent(request);
        String ipAddress = HttpHelper.getRequestIp(request);
        loginTrace.setUserAgent(userAgent).setIpAddress(ipAddress);
        iamAsyncWorker.saveLoginTraceLog(loginTrace);
    }

    /**
     * 失败次数超限锁定账号
     * @param latestAccount
     */
    protected void lockAccountIfRequired(IamAccount latestAccount) {
        // 查询最新1天内的失败记录
        LambdaQueryWrapper<IamLoginTrace> queryWrapper = Wrappers.<IamLoginTrace>lambdaQuery()
                .select(IamLoginTrace::getIsSuccess)
                .eq(IamLoginTrace::getUserType, latestAccount.getUserType())
                .eq(IamLoginTrace::getAuthType, latestAccount.getAuthType())
                .eq(IamLoginTrace::getAuthAccount, latestAccount.getAuthAccount())
                .gt(IamLoginTrace::getCreateTime, LocalDateTime.now().minusDays(1))
                .eq(V.notEmpty(latestAccount.getTenantId()) ,IamLoginTrace::getTenantId, latestAccount.getTenantId());
        // 检查是否超出最大次数
        int maxLoginAttempts = iamProperties.getMaxLoginAttempts();
        List<IamLoginTrace> loginList = loginTraceService.getEntityListLimit(queryWrapper, maxLoginAttempts);
        if(V.notEmpty(loginList) && loginList.size() >= maxLoginAttempts) {
            int failCount = 0;
            for(IamLoginTrace loginTrace : loginList) {
                if(loginTrace.getIsSuccess()) {
                    break;
                }
                failCount++;
            }
            if(failCount >= maxLoginAttempts) {
                latestAccount.setStatus(Cons.DICTCODE_ACCOUNT_STATUS.L.name());
                log.warn("用户登录失败次数超过最大限值，账号 {} 已被锁定！", latestAccount.getAuthAccount());
                accountService.updateAccountStatus(latestAccount.getId(), Cons.DICTCODE_ACCOUNT_STATUS.L.name());
            }
        }
    }
}
