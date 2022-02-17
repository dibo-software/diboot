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
package com.diboot.iam.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.dto.OAuth2SSOCredential;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.jwt.BaseJwtAuthToken;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamLoginTraceService;
import com.diboot.iam.starter.IamProperties;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * OAuth2SSO认证的service实现
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/02/16
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "diboot.iam.oauth2-client", name = {"client-id", "client-secret", "redirect-uri", "access-token-uri"})
public class OAuth2SSOServiceImpl implements AuthService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IamAccountService accountService;
    @Autowired
    private IamLoginTraceService iamLoginTraceService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private IamProperties iamProperties;

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
                .eq(IamAccount::getTenantId, jwtToken.getTenantId())
                //.eq(IamAccount::getAuthType, jwtToken.getAuthType()) SSO只检查用户名，支持任意类型账号
                .eq(IamAccount::getUserId, jwtToken.getAuthAccount()) // 直接查询对应用户ID
                .orderByDesc(IamAccount::getId);
        IamAccount latestAccount = accountService.getSingleEntity(queryWrapper);
        if (latestAccount == null) {
            return null;
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.I.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException("用户账号已禁用! account=" + jwtToken.getAuthAccount());
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.L.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException("用户账号已锁定! account=" + jwtToken.getAuthAccount());
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
            } else {
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
     *
     * @param credential
     * @return
     */
    private BaseJwtAuthToken initBaseJwtAuthToken(AuthCredential credential) {
        // 通过授权码得到账号
        parseCode(credential);
        BaseJwtAuthToken token = new BaseJwtAuthToken(getAuthType(), credential.getUserTypeClass());
        // 设置账号密码
        token.setAuthAccount(credential.getAuthAccount());
        token.setTenantId(credential.getTenantId());
        token.setRememberMe(credential.isRememberMe());
        // 生成token
        return token.generateAuthtoken(getExpiresInMinutes());
    }

    /**
     * 解析授权码
     */
    protected void parseCode(AuthCredential credential) {
        OAuth2SSOCredential ssoCredential = (OAuth2SSOCredential) credential;
        IamProperties.Oauth2ClientProperties oauth2Client = iamProperties.getOauth2Client();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        byte[] authorization = (oauth2Client.getClientId() + ":" + oauth2Client.getClientSecret()).getBytes(StandardCharsets.UTF_8);
        String base64Auth =  Base64.getEncoder().encodeToString(authorization);
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + base64Auth);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("grant_type", "authorization_code");
        param.add("code", ssoCredential.getCode());
        param.add("redirect_uri", oauth2Client.getRedirectUri());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(oauth2Client.getAccessTokenUri(), request, Map.class);
        ssoCredential.setAuthAccount(S.valueOf(response.getBody().get("userId")));
    }

    /**
     * 保存登录日志
     *
     * @param authToken
     * @param isSuccess
     */
    protected void saveLoginTrace(BaseJwtAuthToken authToken, boolean isSuccess) {
        IamLoginTrace loginTrace = new IamLoginTrace();
        loginTrace.setAuthType(getAuthType()).setAuthAccount(authToken.getAuthAccount()).setUserType(authToken.getUserType()).setSuccess(isSuccess);
        BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
        if (currentUser != null) {
            Long userId = currentUser.getId();
            loginTrace.setUserId(userId);
        }
        // 记录客户端信息
        String userAgent = request.getHeader("user-agent");
        String ipAddress = IamSecurityUtils.getRequestIp(request);
        loginTrace.setUserAgent(userAgent).setIpAddress(ipAddress);
        try {
            iamLoginTraceService.createEntity(loginTrace);
        } catch (Exception e) {
            log.warn("保存登录日志异常", e);
        }
    }

}
