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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.dto.OAuth2SSOCredential;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.iam.config.IamProperties;
import lombok.extern.slf4j.Slf4j;
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
public class OAuth2SSOServiceImpl extends BaseAuthServiceImpl {
    @Autowired(required = false)
    private RestTemplate restTemplate;
    @Autowired
    private IamProperties iamProperties;

    @Override
    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.SSO.name();
    }

    /**
     * 构建查询条件
     * @param iamAuthToken
     * @return
     */
    @Override
    protected Wrapper buildQueryWrapper(IamAuthToken iamAuthToken) {
        // 查询最新的记录
        LambdaQueryWrapper<IamAccount> queryWrapper = Wrappers.<IamAccount>lambdaQuery()
                .select(IamAccount::getAuthAccount, IamAccount::getUserType, IamAccount::getUserId, IamAccount::getStatus)
                .eq(IamAccount::getUserType, iamAuthToken.getUserType())
                .eq(V.notEmpty(iamAuthToken.getTenantId()) ,IamAccount::getTenantId, iamAuthToken.getTenantId())
                .eq(IamAccount::getAuthAccount, iamAuthToken.getAuthAccount())
                .orderByDesc(IamAccount::getId);
        return queryWrapper;
    }

    @Override
    public String applyToken(AuthCredential credential) {
        // 通过授权码得到账号
        parseCode(credential);
        OAuth2SSOCredential ssoCredential = (OAuth2SSOCredential) credential;
        if(V.isEmpty(ssoCredential.getAuthAccount())){
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.oauth2SSOService.authCenterValidFailed");
        }
        return super.applyToken(credential);
    }

    /**
     * 初始化AuthToken实例
     *
     * @param credential
     * @return
     */
    @Override
    protected IamAuthToken initAuthToken(AuthCredential credential) {
        IamAuthToken token = new IamAuthToken(getAuthType(), credential.getUserTypeClass());
        // 设置账号密码
        token.setAuthAccount(credential.getAuthAccount());
        token.setTenantId(credential.getTenantId());
        token.setRememberMe(credential.isRememberMe());
        token.setExpiresInMinutes(getExpiresInMinutes());
        // 生成token
        return token.generateAuthtoken();
    }

    /**
     * 解析授权码
     */
    protected void parseCode(AuthCredential credential) {
        if(restTemplate == null){
            throw new InvalidUsageException("exception.invalidUsage.userService.initRestTemplate");
        }
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
        Map tokenMap = response.getBody();
        if(V.notEmpty(tokenMap)){
            ssoCredential.setUserType(S.valueOf(tokenMap.get("userType")));
            ssoCredential.setAuthAccount(S.valueOf(tokenMap.get("username")));
        }
    }

}
