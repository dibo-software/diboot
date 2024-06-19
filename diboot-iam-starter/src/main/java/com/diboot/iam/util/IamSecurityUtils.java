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

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.service.IamLoginTraceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.lang.util.ByteSource;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

import java.util.Collection;

/**
 * IAM认证相关工具类
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/26
 */
@Slf4j
public class IamSecurityUtils extends SecurityUtils {

    /**
     * 加密算法与hash次数
     */
    private static final String ALGORITHM = "md5";
    private static final int ITERATIONS = 2;

    /**
     * 获取当前用户类型和id信息
     *
     * @return
     */
    public static <T> T getCurrentUser() {
        Subject subject = getSubject();
        if (subject != null) {
            return (T) subject.getPrincipal();
        }
        return null;
    }

    /**
     * 基于 accessToken 获取登录用户信息
     */
    public static BaseLoginUser getLoginUserByToken(String accessToken) {
        CacheManager cacheManager = ContextHolder.getBean(CacheManager.class);
        if (cacheManager != null && cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME) != null) {
            SimpleAuthenticationInfo authInfo = (SimpleAuthenticationInfo) cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).get(accessToken);
            if (authInfo != null) {
                SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) authInfo.getPrincipals();
                return (BaseLoginUser) principalCollection.getPrimaryPrincipal();
            } else {
                log.warn("缓存中不存在的无效token: {}", accessToken);
            }
        } else {
            throw new InvalidUsageException("exception.invalidUsage.iamSecurityUtils.getLoginUserByToken.message");
        }
        return null;
    }

    /**
     * 退出 当前登录用户
     */
    public static void logout() {
        BaseLoginUser user = getCurrentUser();
        if (user != null) {
            try {
                ContextHolder.getBean(IamLoginTraceService.class).updateLogoutInfo(user.getClass().getSimpleName(), user.getId());
            } catch (Exception e) {
                log.warn("更新用户退出时间异常: {}", e.getMessage());
            }
        }
        Subject subject = getSubject();
        if (subject.isAuthenticated() || subject.getPrincipals() != null) {
            subject.logout();
        }
    }

    /**
     * 退出 指定用户
     */
    public static void logout(String userTypeAndId) {
        CacheManager cacheManager = ContextHolder.getBean(CacheManager.class);
        if (cacheManager == null || cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME) == null) {
            log.warn("cacheManager 实例异常");
            return;
        }
        IamLoginTraceService iamLoginTraceService = ContextHolder.getBean(IamLoginTraceService.class);
        Collection<Object> cacheVals = cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).values();
        for (Object obj : cacheVals) {
            SimpleAuthenticationInfo authInfo = (SimpleAuthenticationInfo) obj;
            SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) authInfo.getPrincipals();
            BaseLoginUser user = (BaseLoginUser) principalCollection.getPrimaryPrincipal();
            if (userTypeAndId.equals(user.getUserTypeAndId())) {
                cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).remove(authInfo.getCredentials());
                TokenUtils.removeAccessTokens(principalCollection.toString());
                log.info("强制退出用户: {}", userTypeAndId);
                try {
                    iamLoginTraceService.updateLogoutInfo(user.getClass().getSimpleName(), user.getId());
                } catch (Exception e) {
                    log.warn("更新用户 {} 退出时间异常: {}", userTypeAndId, e.getMessage());
                }
            }
        }
    }

    /**
     * 基于 accessToken 退出 指定用户
     */
    public static void logoutByToken(String accessToken) {
        IamSecurityUtils.logout();
        CacheManager cacheManager = ContextHolder.getBean(CacheManager.class);
        if (cacheManager != null && cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME) != null) {
            cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).remove(accessToken);
        }
        TokenUtils.removeAccessTokens(accessToken);
        log.debug("token 已过期注销: {}", accessToken);
    }

    /**
     * 获取用户 "ID" 的值
     *
     * @return
     */
    public static String getCurrentUserId() {
        BaseLoginUser user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取用户 "tenantId" 的值
     *
     * @return
     */
    public static String getCurrentTenantId() {
        try {
            BaseLoginUser user = getCurrentUser();
            if (user != null) {
                return user.getTenantId();
            }
        } catch (Exception e) {
            log.debug("当前调用链路无登录用户信息：{}", e.getMessage());
        }
        return Cons.ID_PREVENT_NULL;
    }

    /**
     * 获取用户 "类型:ID" 的值
     *
     * @return
     */
    public static String getUserTypeAndId() {
        BaseLoginUser user = getCurrentUser();
        if (user == null) {
            return null;
        }
        return S.join(user.getClass().getSimpleName(), Cons.SEPARATOR_COLON, user.getId());
    }

    /**
     * 清空指定用户账户的权限信息的缓存 使其立即生效
     */
    public static void clearAuthorizationCache(String userType, String userId) {
        CacheManager cacheManager = ContextHolder.getBean(CacheManager.class);
        if (cacheManager == null || cacheManager.getCache(Cons.AUTHORIZATION_CAHCE_NAME) == null) {
            log.warn("cacheManager 实例异常");
            return;
        }
        String userTypeAndId = userType + Cons.SEPARATOR_COLON + userId;
        Cache<Object, AuthenticationInfo> authenticationCache = cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME);
        for (Object obj : authenticationCache.values()) {
            SimpleAuthenticationInfo authInfo = (SimpleAuthenticationInfo) obj;
            SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) authInfo.getPrincipals();
            BaseLoginUser user = (BaseLoginUser) principalCollection.getPrimaryPrincipal();
            if (userTypeAndId.equals(user.getUserTypeAndId())) {
                String accessToken = principalCollection.toString();
                Cache<Object, AuthorizationInfo> authorizationCache = cacheManager.getCache(Cons.AUTHORIZATION_CAHCE_NAME);
                authorizationCache.remove(accessToken);
                log.info("清空用户权限缓存，使新权限生效: {}", userTypeAndId);
                return;
            }
        }
    }

    /**
     * 清空所有权限信息的缓存 使其立即生效
     */
    public static void clearAllAuthorizationCache() {
        CacheManager cacheManager = ContextHolder.getBean(CacheManager.class);
        if (cacheManager == null || cacheManager.getCache(Cons.AUTHORIZATION_CAHCE_NAME) == null) {
            log.warn("cacheManager 实例异常");
            return;
        }
        Cache<Object, AuthorizationInfo> cache = cacheManager.getCache(Cons.AUTHORIZATION_CAHCE_NAME);
        if (cache != null) {
            cache.clear();
            log.debug("已清空全部登录用户的权限缓存，以便新权限生效.");
        }
    }

    /***
     * 对用户密码加密
     * @param iamAccount
     */
    public static void encryptPwd(IamAccount iamAccount) {
        if (Cons.DICTCODE_AUTH_TYPE.PWD.name().equals(iamAccount.getAuthType())) {
            if (iamAccount.getSecretSalt() == null) {
                String salt = S.cut(S.newUuid(), 8);
                iamAccount.setSecretSalt(salt);
            }
            String encryptedPwd = encryptPwd(iamAccount.getAuthSecret(), iamAccount.getSecretSalt());
            iamAccount.setAuthSecret(encryptedPwd);
        }
    }

    /***
     * 对用户密码加密
     * @param password
     * @param salt
     */
    public static String encryptPwd(String password, String salt) {
        return new SimpleHash(ALGORITHM, password, ByteSource.Util.bytes(salt), ITERATIONS).toHex();
    }

    /**
     * 是否为超管
     * @return
     */
    public static boolean isSuperAdmin() {
        Subject subject = getSubject();
        if (subject != null) {
            return subject.hasRole(Cons.ROLE_SUPER_ADMIN);
        }
        return false;
    }

}
