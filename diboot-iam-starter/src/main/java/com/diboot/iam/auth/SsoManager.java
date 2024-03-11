package com.diboot.iam.auth;

import com.diboot.iam.dto.SsoAuthorizeInfo;

import java.util.Map;

public interface SsoManager {

    /**
     * 获取认证类型
     * @return
     */
    String getAuthType();

    /**
     * 获取单点登录的登录地址
     * @param callback 前端回调地址
     * @return
     */
    SsoAuthorizeInfo getAuthorizeInfo(String callback);

    /**
     * 通过单点登录的凭证信息获取认证后的token
     * @param paramsMap 获取token的单点认证的凭证信息
     * @return
     */
    String getToken(Map<String, Object> paramsMap);
}
