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
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.config.Cons;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cache.CacheManager;
import org.springframework.util.Assert;

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
     * 缓存新的token
     * @param accessToken
     * @param userInfoStr
     * @param expiresInMinutes
     */
    public static void cacheAccessToken(String accessToken, String userInfoStr, int expiresInMinutes) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        baseCacheManager.putCacheObj(Cons.CACHE_TOKEN_USERINFO, accessToken, userInfoStr, expiresInMinutes);
    }

    /**
     * 移除失效的token
     * @param accessToken
     */
    public static void removeAccessToken(String accessToken) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        baseCacheManager.removeCacheObj(Cons.CACHE_TOKEN_USERINFO, accessToken);
    }

    /**
     * 获取缓存的token信息
     * @param accessToken
     * @return
     */
    public static String getCachedUserInfoStr(String accessToken) {
        BaseCacheManager baseCacheManager = ContextHelper.getBean(BaseCacheManager.class);
        return baseCacheManager.getCacheString(Cons.CACHE_TOKEN_USERINFO, accessToken);
    }

    /**
     * token是否有效: 未过期/刷新token 均有效
     * @param accessToken
     * @return
     */
    public static boolean isActiveAccessToken(String accessToken) {
        String userInfoStr = getCachedUserInfoStr(accessToken);
        if(V.isEmpty(userInfoStr)){
            //TODO 判断是否为刷新token
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
     * 是否已过期
     * @param userInfoStr
     * @return
     */
    public static boolean isExpired(String userInfoStr){
        if(V.isEmpty(userInfoStr)){
            return false;
        }
        String[] userFields = S.split(userInfoStr);
        AuthService authService = AuthServiceFactory.getAuthService(userFields[3]);
        int expiresInMinutes = authService.getExpiresInMinutes();
        // 获取当前token的过期时间
        long issuedAt = Long.parseLong(userFields[4]);
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
        AuthService authService = AuthServiceFactory.getAuthService(userFields[3]);
        int expiresInMinutes = authService.getExpiresInMinutes();
        // 获取当前token的过期时间
        long issuedAt = Long.parseLong(userFields[4]);
        // 获取当前token的过期时间
        long expiredBefore = issuedAt + expiresInMinutes * 60000;
        // 当前token是否临近过期
        long current = System.currentTimeMillis();
        long takes = (current - issuedAt)/60000;
        if(takes < expiresInMinutes){
            if((takes / expiresInMinutes) > 3/4){
                return true;
            }
        }
        return false;
    }

}
