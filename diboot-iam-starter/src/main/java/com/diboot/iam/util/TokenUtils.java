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

import com.diboot.core.cache.BaseCacheManager;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Token相关操作类
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public class TokenUtils {
    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);

    private static final String AUTH_HEADER = getConfigValue("diboot.iam.token-header-key", "Authorization");
    public static final int EXPIRES_IN_MINUTES = getConfigIntValue("diboot.iam.token-expires-minutes", 60);

    /***
     * 从请求头中获取客户端发来的token
     * @param request
     * @return
     */
    public static String getRequestToken(HttpServletRequest request) {
        String authtoken = request.getHeader(AUTH_HEADER);
        if(authtoken != null){
            if(authtoken.startsWith(Cons.TOKEN_PREFIX_BEARER)){
                authtoken = authtoken.substring(Cons.TOKEN_PREFIX_BEARER.length());
            }
            authtoken = authtoken.trim();
        }
        if(V.isEmpty(authtoken)){
            log.warn("请求未指定token: {}", authtoken);
            return null;
        }
        if(!isActiveAccessToken(authtoken)){
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
     * @param cachedUserInfo
     * @return
     */
    public static synchronized void responseNewTokenIfRequired(ServletResponse response, String cachedUserInfo) {
        if(isCloseToExpired(cachedUserInfo)){
            //将刷新的token放入response header
            String refreshToken = generateToken();
            cacheRefreshToken(refreshToken, cachedUserInfo);
            ((HttpServletResponse)response).setHeader(AUTH_HEADER, refreshToken);
            log.debug("写回刷新token :{}", refreshToken);
        }
    }

    /**
     * 缓存新的token
     * @param accessToken
     * @param userInfoStr
     */
    public static void cacheAccessToken(String accessToken, String userInfoStr) {
        getIamCacheManager().putCacheObj(Cons.CACHE_TOKEN_USERINFO, accessToken, userInfoStr);
    }

    /**
     * 退出时移除失效的全部token
     * @param accessToken
     */
    public static void removeAccessTokens(String accessToken) {
        getIamCacheManager().removeCacheObj(Cons.CACHE_TOKEN_USERINFO, accessToken);
    }

    /**
     * 获取缓存的token信息
     * @param accessToken
     * @return
     */
    public static String getCachedUserInfoStr(String accessToken) {
        String userInfoStr = getIamCacheManager().getCacheString(Cons.CACHE_TOKEN_USERINFO, accessToken);
        if(userInfoStr == null){
            log.info("token {} 缓存信息不存在", accessToken);
        }
        return userInfoStr;
    }

    /**
     * token是否有效: 未过期/刷新token 均有效
     * @param accessToken
     * @return
     */
    public static boolean isActiveAccessToken(String accessToken) {
        String userInfoStr = getCachedUserInfoStr(accessToken);
        if(V.isEmpty(userInfoStr)){
            return false;
        }
        if(isExpired(userInfoStr)){
            IamSecurityUtils.logoutByToken(accessToken);
            return false;
        }
        return true;
    }

    /**
     * 缓存刷新token
     * @param refreshToken
     */
    public synchronized static void cacheRefreshToken(String refreshToken, String userInfoStr) {
        String prefixTemp = S.substringBeforeLast(userInfoStr, Cons.SEPARATOR_COMMA);
        //如果是刷新token则更新颁发时间
        userInfoStr = prefixTemp + Cons.SEPARATOR_COMMA + System.currentTimeMillis();
        cacheAccessToken(refreshToken, userInfoStr);
    }

    /**
     * 是否已过期
     * @param userInfoStr
     * @return
     */
    private static final int EXPIRES_MINUTES_INDEX = 4, ISSUED_AT_INDEX = 5;
    public static boolean isExpired(String userInfoStr){
        if(V.isEmpty(userInfoStr)){
            return false;
        }
        String[] userFields = S.split(userInfoStr);
        int expiresInMinutes = Integer.parseInt(userFields[EXPIRES_MINUTES_INDEX]);
        // 获取当前token的过期时间
        long issuedAt = Long.parseLong(userFields[ISSUED_AT_INDEX]);
        // 获取当前token的过期时间
        long expiredBefore = issuedAt + expiresInMinutes * 60000L;
        return System.currentTimeMillis() > expiredBefore;
    }

    /**
     * 是否临近过期
     * @param userInfoStr
     * @return
     */
    public static boolean isCloseToExpired(String userInfoStr){
        if(V.isEmpty(userInfoStr)){
            return false;
        }
        String[] userFields = S.split(userInfoStr);
        int expiresInMinutes = Integer.parseInt(userFields[EXPIRES_MINUTES_INDEX]);
        // 当前token是否临近过期
        long current = System.currentTimeMillis();
        long issuedAt = Long.parseLong(userFields[ISSUED_AT_INDEX]);
        long expiration = issuedAt + expiresInMinutes* 60000L;
        long remaining = expiration - current;
        if(remaining > 0){
            long elapsed = current - issuedAt;
            // 小于1/4 则更新
            double past = ((double) elapsed / remaining);
            return past > 3.0;
        }
        return false;
    }

    private static BaseCacheManager iamCacheManager;
    private static BaseCacheManager getIamCacheManager() {
        if(iamCacheManager == null) {
            iamCacheManager = (BaseCacheManager)ContextHolder.getBean("iamCacheManager");
            if(iamCacheManager == null) {
                throw new InvalidUsageException("exception.invalidUsage.tokenUtils.getIamCacheManager.message");
            }
        }
        return iamCacheManager;
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