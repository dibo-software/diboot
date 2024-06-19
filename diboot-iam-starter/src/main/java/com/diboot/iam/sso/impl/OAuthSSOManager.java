package com.diboot.iam.sso.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.sso.SSOManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.SsoAuthorizeInfo;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.sso.credential.OAuth2Credential;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "diboot.iam.oauth2", name = {"client-id", "client-secret", "auth-center-url", "access-token-uri", "user-info-uri", "callback"})
public class OAuthSSOManager implements SSOManager {

    private static final String CODE_KEY = "code";

    private static final String STATE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final String ACCESS_TOKEN = "access_token";
    private static final String TOKEN_TYPE = "token_type";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IamUserService iamUserService;

    @Autowired
    private IamAccountService iamAccountService;

    @Value("${diboot.iam.oauth2.client-id}")
    private String clientId;

    @Value("${diboot.iam.oauth2.client-secret}")
    private String clientSecret;

    @Value("${diboot.iam.oauth2.auth-center-url}")
    private String authCenterUrl;

    @Value("${diboot.iam.oauth2.access-token-uri}")
    private String accessTokenUri;

    @Value("${diboot.iam.oauth2.user-info-uri}")
    private String userInfoUri;

    @Value("${diboot.iam.oauth2.callback}")
    private String callback;

    @Override
    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.OAuth2.name();
    }

    @Override
    public SsoAuthorizeInfo getAuthorizeInfo(String callback) {
        if (V.isEmpty(callback)) {
            callback = this.callback;
        }
        if (V.isEmpty(callback)) {
            throw new BusinessException("exception.business.SSOManager.nullCallback");
        }
        try {
            callback = URLEncoder.encode(callback, "UTF-8");
        } catch(Exception e) {
            log.error("URL编码出错", e);
        }
        String state = getState();
        String url = this.authCenterUrl + "/oauth2/authorize?client_id=" + this.clientId + "&redirect_uri=" + callback + "&response_type=code&state=" + state;
        SsoAuthorizeInfo authorizeInfo = new SsoAuthorizeInfo(url, state);
        return authorizeInfo;
    }

    @Override
    public String getToken(Map<String, Object> paramsMap) {
        if (V.isEmpty(paramsMap) || paramsMap.get(CODE_KEY) == null) {
            return null;
        }
        String code = String.valueOf(paramsMap.get(CODE_KEY));
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("grant_type", "authorization_code");
        param.add("code", code);
        param.add("redirect_uri", this.callback);
        // 获取accessToken
        Map accessTokenMap = getAccessToken(param, clientId);
        // 获取用户信息
        Map userInfoMap = getUserInfo(accessTokenMap);
        // 处理用户信息
        IamUser iamUser = syncUserInfo(userInfoMap);
        // 基于当前系统用户工号信息分发token
        OAuth2Credential credential = new OAuth2Credential();
        credential.setAuthAccount(iamUser.getUserNum()).setUserType(IamUser.class.getSimpleName()).setAuthType(Cons.DICTCODE_AUTH_TYPE.OAuth2.name());
        return AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.OAuth2.name()).applyToken(credential);
    }

    /**
     * 获取state
     * @return
     */
    protected String getState() {
        StringBuilder state = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(52);
            state.append(STATE_CHARS.charAt(randomIndex));
        }

        return state.toString();
    }

    /**
     * 获取AccessToken
     */
    protected Map getAccessToken(MultiValueMap<String, String> param, String clientId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        if(S.isBlank(this.clientSecret)){
            throw new BusinessException("exception.business.oauthSSOManager.nonConfigClientSecret", clientId);
        }

        byte[] authorization = (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8);
        String base64Auth = Base64.encodeBase64String(authorization);
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + base64Auth);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUri, request , Map.class);
        return response.getBody();
    }

    protected Map getUserInfo(Map accessTokenMap) {
        String accessToken = String.valueOf(accessTokenMap.get(ACCESS_TOKEN));
        String tokenType = String.valueOf(accessTokenMap.get(TOKEN_TYPE));

        HttpHeaders headers = new HttpHeaders();

        if(S.isBlank(accessToken)){
            throw new BusinessException("exception.business.oauthSSOManager.nullAccessToken");
        }

        headers.add(HttpHeaders.AUTHORIZATION, tokenType + " " + accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                Map.class
        );
        Map responseBody = response.getBody();
        if (responseBody.get("code") != null) {
            if (V.equals(String.valueOf(responseBody.get("code")), "0") && responseBody.get("data") != null) {
                return JSON.toMap(JSON.stringify(responseBody.get("data")));
            } else {
                throw new BusinessException("exception.business.oauthSSOManager.ssoLoginFailed");
            }
        }
        return response.getBody();
    }

    /**
     * 同步用户信息
     * @param userInfoMap
     * @return
     */
    protected IamUser syncUserInfo(Map userInfoMap) {
        if (V.isEmpty(userInfoMap)) {
            throw new BusinessException("exception.business.oauthSSOManager.fetchUserInfoFailed");
        }
        // 将Map转换为IamUser
        IamUser userInfo = JSON.parseObject(JSON.toJSONString(userInfoMap), IamUser.class);
        // 查询在IamUser中是否已存在
        IamUser oldIamUser = iamUserService.getSingleEntity(
                Wrappers.<IamUser>lambdaQuery().eq(IamUser::getUserNum, userInfo.getUserNum())
        );
        if (oldIamUser == null) {
            iamUserService.createEntity(userInfo);
        } else {
            userInfo.setId(oldIamUser.getId());
            iamUserService.updateEntity(userInfo);
        }
        // 查询在IamAccount中是否已存在
        IamAccount iamAccount = iamAccountService.getSingleEntity(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getTenantId, userInfo.getTenantId())
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getUserId, userInfo.getId())
                        .eq(IamAccount::getAuthType, this.getAuthType())
                        .eq(IamAccount::getAuthAccount, userInfo.getUserNum())
        );
        if (iamAccount == null) {
            iamAccount = new IamAccount();
            iamAccount.setUserType(IamUser.class.getSimpleName())
                    .setTenantId(userInfo.getTenantId())
                    .setUserId(userInfo.getId())
                    .setAuthType(this.getAuthType())
                    .setAuthAccount(userInfo.getUserNum());
            iamAccountService.createEntity(iamAccount);
        }
        return userInfo;
    }
}
