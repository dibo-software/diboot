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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.dto.SSOCredential;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.jwt.BaseJwtAuthToken;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamLoginTraceService;
import com.diboot.iam.util.BeanUtils;
import com.diboot.iam.util.HttpHelper;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户名SSO认证的service实现
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/25
 */
@Service
@Slf4j
public class SSOAuthServiceImpl implements AuthService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IamAccountService accountService;
    @Autowired
    private IamLoginTraceService iamLoginTraceService;
    // cas server url
    private String casUrlPrefix;

    private String getCasUrlPrefix(){
        if(this.casUrlPrefix == null){
            this.casUrlPrefix = BaseConfig.getProperty("cas.server-url-prefix");
        }
        if(V.isEmpty(this.casUrlPrefix)){
            throw new BusinessException("未配置cas参数: cas.server-url-prefix");
        }
        if(!this.casUrlPrefix.endsWith("/")){
            this.casUrlPrefix += "/";
        }
        return this.casUrlPrefix;
    }

    @Override
    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.SSO.name();
    }

    @Override
    public IamAccount getAccount(BaseJwtAuthToken jwtToken) throws AuthenticationException {
        // 查询最新的记录
        LambdaQueryWrapper<IamAccount> queryWrapper = new LambdaQueryWrapper<IamAccount>()
                .select(IamAccount::getAuthAccount, IamAccount::getUserType, IamAccount::getUserId, IamAccount::getStatus)
                .eq(IamAccount::getUserType, jwtToken.getUserType())
                //.eq(IamAccount::getAuthType, jwtToken.getAuthType()) SSO只检查用户名，支持任意类型账号
                .eq(IamAccount::getAuthAccount, jwtToken.getAuthAccount())
                .orderByDesc(IamAccount::getId);
        IamAccount latestAccount = accountService.getSingleEntity(queryWrapper);
        if(latestAccount == null){
            return null;
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.I.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException("用户账号已禁用! account="+jwtToken.getAuthAccount());
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.L.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException("用户账号已锁定! account="+jwtToken.getAuthAccount());
        }
        return latestAccount;
    }

    @Override
    public String applyToken(AuthCredential credential) {
        BaseJwtAuthToken authToken = initBaseJwtAuthToken(credential);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(authToken);
            if (subject.isAuthenticated()) {
                log.debug("申请token成功！authtoken={}", authToken.getCredentials());
                saveLoginTrace(authToken, true);
                // 跳转到首页
                return (String) authToken.getCredentials();
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
    private BaseJwtAuthToken initBaseJwtAuthToken(AuthCredential credential){
        // 通过CAS得到账号
        SSOCredential ssoCredential = (SSOCredential)credential;
        BaseJwtAuthToken token = new BaseJwtAuthToken(getAuthType(), ssoCredential.getUserTypeClass());
        String username = parseCasTicket(ssoCredential);
        ssoCredential.setAuthAccount(username);
        // 设置账号密码
        token.setAuthAccount(ssoCredential.getAuthAccount()).setRememberMe(ssoCredential.isRememberMe());
        // 生成token
        return token.generateAuthtoken(getExpiresInMinutes());
    }

    /**
     * 保存登录日志
     * @param authToken
     * @param isSuccess
     */
    protected void saveLoginTrace(BaseJwtAuthToken authToken, boolean isSuccess){
        IamLoginTrace loginTrace = new IamLoginTrace();
        loginTrace.setAuthType(getAuthType()).setAuthAccount(authToken.getAuthAccount()).setUserType(authToken.getUserType()).setSuccess(isSuccess);
        Object currentUser = IamSecurityUtils.getCurrentUser();
        if(currentUser != null){
            Long userId = (Long) BeanUtils.getProperty(currentUser, Cons.FieldName.id.name());
            loginTrace.setUserId(userId);
        }
        // 记录客户端信息
        String userAgent = request.getHeader("user-agent");
        String ipAddress = IamSecurityUtils.getRequestIp(request);
        loginTrace.setUserAgent(userAgent).setIpAddress(ipAddress);
        try{
            iamLoginTraceService.createEntity(loginTrace);
        }
        catch (Exception e){
            log.warn("保存登录日志异常", e);
        }
    }

    /**
     * 解析CAS ticket，提取username
     * @param ssoCredential
     */
    protected String parseCasTicket(SSOCredential ssoCredential) {
        String casServiceValidateUrl =  this.getCasUrlPrefix() + "p3/serviceValidate?service="+ssoCredential.getServiceUrl()+"&ticket="+ssoCredential.getTicket();
        String responseBody = HttpHelper.callGet(casServiceValidateUrl, null);
        // 检查结果
        String errorMsg = S.substringBetween(responseBody, "<cas:authenticationFailure", "</cas:authenticationFailure>");
        if(V.notEmpty(errorMsg)){
            errorMsg = S.substringAfter(errorMsg, ">");
            throw new BusinessException("CAS登录失败:" + errorMsg);
        }
        // 提取用户名
        String username = S.substringBetween(responseBody, "<cas:user>", "</cas:user>");

        log.debug("CAS ticket {}, user = {}", ssoCredential.getTicket(), username);
        return username;
    }

}
