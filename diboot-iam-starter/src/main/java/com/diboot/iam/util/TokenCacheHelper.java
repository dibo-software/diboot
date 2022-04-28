/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.CacheManager;

/**
 * token缓存相关辅助类
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/27
 * Copyright © diboot.com
 */
@Slf4j
public class TokenCacheHelper {

    /**
     * 刷新token存储的过期时间
     */
    public static int REFRESH_TOKEN_EXPIRES_MINUTES = (TokenUtils.EXPIRES_IN_MINUTES / 4);

    /**
     * 缓存新的token
     * @param accessToken
     * @param userInfoStr
     */
    public static void cacheAccessToken(String accessToken, String userInfoStr, int expiresInMinutes) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        baseCacheManager.putCacheObj(Cons.CACHE_TOKEN_USERINFO, accessToken, userInfoStr, expiresInMinutes);
    }

    /**
     * 移除失效的旧token
     * @param newToken
     */
    public static void updateUserInfoCacheAndRemovePreviousToken(String newToken) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        String previousToken = baseCacheManager.getCacheString(Cons.CACHE_REFRESHTOKEN_TOKEN, newToken);
        if(previousToken != null){
            String cachedUserInfoStr = baseCacheManager.getCacheString(Cons.CACHE_TOKEN_USERINFO, previousToken);
            if(cachedUserInfoStr != null){
                String prefixTemp = S.substringBeforeLast(cachedUserInfoStr, Cons.SEPARATOR_COMMA);
                int expiresMinutes = Integer.parseInt(S.substringAfterLast(prefixTemp, Cons.SEPARATOR_COMMA));
                //如果是刷新token则更新颁发时间
                cachedUserInfoStr = prefixTemp + Cons.SEPARATOR_COMMA + System.currentTimeMillis();
                cacheAccessToken(newToken, cachedUserInfoStr, expiresMinutes);
            }
            baseCacheManager.removeCacheObj(Cons.CACHE_TOKEN_USERINFO, previousToken);
            baseCacheManager.removeCacheObj(Cons.CACHE_TOKEN_REFRESHTOKEN, previousToken);
        }
    }

    /**
     * 退出时移除失效的全部token
     * @param accessToken
     */
    public static void removeAccessTokens(String accessToken) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        baseCacheManager.removeCacheObj(Cons.CACHE_TOKEN_USERINFO, accessToken);
        String refreshToken = baseCacheManager.getCacheString(Cons.CACHE_TOKEN_REFRESHTOKEN, accessToken);
        if(refreshToken != null){
            baseCacheManager.removeCacheObj(Cons.CACHE_TOKEN_USERINFO, refreshToken);
            baseCacheManager.removeCacheObj(Cons.CACHE_REFRESHTOKEN_TOKEN, refreshToken);
        }
        baseCacheManager.removeCacheObj(Cons.CACHE_TOKEN_REFRESHTOKEN, accessToken);
    }

    /**
     * 获取缓存的token信息
     * @param accessToken
     * @return
     */
    public static String getCachedUserInfoStr(String accessToken) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        String cachedUserInfoStr = baseCacheManager.getCacheString(Cons.CACHE_TOKEN_USERINFO, accessToken);
        if(cachedUserInfoStr == null && isRefreshToken(accessToken)){
            String previousToken = baseCacheManager.getCacheString(Cons.CACHE_TOKEN_REFRESHTOKEN, accessToken);
            if(previousToken != null){
                cachedUserInfoStr = baseCacheManager.getCacheString(Cons.CACHE_TOKEN_USERINFO, previousToken);
                //如果是刷新token则更新颁发时间
                if(cachedUserInfoStr != null){
                    cachedUserInfoStr = S.substringBeforeLast(cachedUserInfoStr, Cons.SEPARATOR_COMMA) + System.currentTimeMillis();
                }
            }
        }
        return cachedUserInfoStr;
    }

    /**
     * token是否有效: 未过期/刷新token 均有效
     * @param accessToken
     * @return
     */
    public static boolean isActiveAccessToken(String accessToken) {
        String userInfoStr = getCachedUserInfoStr(accessToken);
        if(V.isEmpty(userInfoStr)){
            // 判断是否为刷新token
            if(TokenCacheHelper.isRefreshToken(accessToken)){
                TokenCacheHelper.updateUserInfoCacheAndRemovePreviousToken(accessToken);
                log.debug("请求已切换为刷新token : {}", accessToken);
                return true;
            }
            else{
                log.debug("非刷新token: {}", accessToken);
            }
            return false;
        }
        CacheManager cacheManager = ContextHelper.getBean(CacheManager.class);
        if(isExpired(userInfoStr)){
            log.warn("token 已过期: {}", accessToken);
            if(cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME) != null){
                Object obj = cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).get(accessToken);
                log.debug("过期未退出前，AUTHENTICATION object={}", obj);
            }
            if(cacheManager.getCache(Cons.AUTHORIZATION_CAHCE_NAME) != null){
                Object obj = cacheManager.getCache(Cons.AUTHORIZATION_CAHCE_NAME).get(accessToken);
                log.debug("过期未退出前，AUTHORIZATION object={}", obj);
            }
            IamSecurityUtils.logoutByToken(accessToken);
            if(cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME) != null){
                Object obj = cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).get(accessToken);
                log.debug("过期退出后，AUTHENTICATION object={}", obj);
            }
            if(cacheManager.getCache(Cons.AUTHORIZATION_CAHCE_NAME) != null){
                Object obj = cacheManager.getCache(Cons.AUTHORIZATION_CAHCE_NAME).get(accessToken);
                log.debug("过期退出后，AUTHORIZATION object={}", obj);
            }
            return false;
        }
        return true;
    }

    /**
     * 获取刷新token
     * @param currentToken
     * @return
     */
    public static String getCachedRefreshToken(String currentToken) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        return baseCacheManager.getCacheString(Cons.CACHE_TOKEN_REFRESHTOKEN, currentToken);
    }

    /**
     * 缓存刷新token
     * @param currentToken
     * @param refreshToken
     */
    public synchronized static void cacheRefreshToken(String currentToken, String refreshToken) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        // 缓存双向的token，便于后面的请求中识别refreshToken
        baseCacheManager.putCacheObj(Cons.CACHE_TOKEN_REFRESHTOKEN, currentToken, refreshToken, REFRESH_TOKEN_EXPIRES_MINUTES);
        baseCacheManager.putCacheObj(Cons.CACHE_REFRESHTOKEN_TOKEN, refreshToken, currentToken, REFRESH_TOKEN_EXPIRES_MINUTES);
    }

    /**
     * 判断是否为刷新token
     * @param accessToken
     * @return
     */
    private static boolean isRefreshToken(String accessToken) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        return baseCacheManager.getCacheString(Cons.CACHE_REFRESHTOKEN_TOKEN, accessToken) != null;
    }

    /**
     * 是否已过期
     * @param userInfoStr
     * @return
     */
    public static boolean isExpired(String userInfoStr){
        if(V.isEmpty(userInfoStr)){
            return false;
        }
        String[] userFields = S.split(userInfoStr);
        int expiresInMinutes = Integer.parseInt(userFields[4]);
        // 获取当前token的过期时间
        long issuedAt = Long.parseLong(userFields[5]);
        // 获取当前token的过期时间
        long expiredBefore = issuedAt + expiresInMinutes * 60000;
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
        int expiresInMinutes = Integer.parseInt(userFields[4]);
        // 当前token是否临近过期
        long current = System.currentTimeMillis();
        long issuedAt = Long.parseLong(userFields[5]);
        long expiration = issuedAt + expiresInMinutes*60000;
        long remaining = expiration - current;
        if(remaining > 0){
            long elapsed = current - issuedAt;
            // 小于1/4 则更新
            double past = (elapsed / remaining);
            return past > 3.0;
        }
        return false;
    }

}
