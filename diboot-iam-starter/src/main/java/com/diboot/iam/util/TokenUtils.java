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
package com.diboot.iam.util;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token相关操作类
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public class TokenUtils {
    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = getConfigValue("diboot.iam.token-header-key", "authtoken");
    public static final int EXPIRES_IN_MINUTES = getConfigIntValue("diboot.iam.token-expires-minutes", 60);

    /***
     * 从请求头中获取客户端发来的token
     * @param request
     * @return
     */
    public static String getRequestToken(HttpServletRequest request) {
        String authtoken = request.getHeader(AUTH_HEADER);
        if(authtoken != null){
            authtoken = authtoken.trim();
            if(authtoken.startsWith(TOKEN_PREFIX)){
                authtoken = authtoken.substring(TOKEN_PREFIX.length());
            }
        }
        if(V.isEmpty(authtoken)){
            log.warn("请求未指定token: {}", authtoken);
            return null;
        }
        if(TokenCacheHelper.isActiveAccessToken(authtoken) == false){
            log.warn("已过期或非系统颁发的token: {}", authtoken);
            return null;
        }
        return authtoken;
    }

    /***
     * 生成Token
     * @return
     */
    public static String generateToken(){
        return S.newUuid();
    }

    /**
     * 临近过期时生成新的token
     * @param currentToken
     * @return
     */
    public static void responseNewTokenIfRequired(ServletResponse response, String currentToken, String cachedUserInfo) {
        if(TokenCacheHelper.isCloseToExpired(cachedUserInfo)){
            //将刷新的token放入response header
            String refreshToken = generateToken();
            ((HttpServletResponse)response).setHeader(AUTH_HEADER, refreshToken);
            log.debug("写回刷新token :{}", refreshToken);
        }
    }

    /**
     * 获取配置参数值
     * @return
     */
    private static String getConfigValue(String key, String defaultValue){
        String value = BaseConfig.getProperty(key);
        return value != null? value : defaultValue;
    }

    /**
     * 获取配置参数值
     * @return
     */
    private static int getConfigIntValue(String key, int defaultValue){
        String value = BaseConfig.getProperty(key);
        return value != null? Integer.parseInt(value) : defaultValue;
    }


}
