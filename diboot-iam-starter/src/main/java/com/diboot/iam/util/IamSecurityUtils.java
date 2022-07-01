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

import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.shiro.IamAuthorizingRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * IAM认证相关工具类
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
     * @return
     */
    public static <T> T getCurrentUser(){
        Subject subject = getSubject();
            if(subject != null){
            return (T)subject.getPrincipal();
        }
        return null;
    }

    /**
     * 退出 注销用户
     */
    public static void logout(){
        Subject subject = getSubject();
        if(subject.isAuthenticated() || subject.getPrincipals() != null){
            // 缓存当前token与用户信息
            //TokenCacheHelper.removeAccessToken(accessToken);
            subject.logout();
        }
    }

    /**
     * 退出 注销指定用户
     */
    public static void logout(String userTypeAndId){
        CacheManager cacheManager = ContextHelper.getBean(CacheManager.class);
        if(cacheManager == null || cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME) == null){
            log.warn("cacheManager 实例异常");
            return;
        }
        Collection<Object> cacheVals = cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).values();
        for(Object obj : cacheVals){
            SimpleAuthenticationInfo authInfo = (SimpleAuthenticationInfo)obj;
            SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) authInfo.getPrincipals();
            BaseLoginUser user = (BaseLoginUser) principalCollection.getPrimaryPrincipal();
            if (userTypeAndId.equals(user.getUserTypeAndId())) {
                cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).remove(authInfo.getCredentials());
                log.info("强制退出用户: {}", userTypeAndId);
            }
        }
    }

    /**
     * 基于 accessToken 退出 注销指定用户
     */
    public static void logoutByToken(String accessToken){
        IamSecurityUtils.logout();
        CacheManager cacheManager = ContextHelper.getBean(CacheManager.class);
        if(cacheManager != null && cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME) != null){
            cacheManager.getCache(Cons.AUTHENTICATION_CAHCE_NAME).remove(accessToken);
        }
        TokenUtils.removeAccessTokens(accessToken);
        log.debug("token 已过期注销: {}", accessToken);
    }

    /**
     * 获取用户 "ID" 的值
     * @return
     */
    public static Long getCurrentUserId(){
        BaseLoginUser user = getCurrentUser();
        return user != null? user.getId() : null;
    }

    /**
     * 获取用户 "类型:ID" 的值
     * @return
     */
    public static String getUserTypeAndId(){
        BaseLoginUser user = getCurrentUser();
        if(user == null){
            return null;
        }
        return S.join(user.getClass().getSimpleName(), Cons.SEPARATOR_COLON, user.getId());
    }

    /**
     * 清空指定用户账户的权限信息的缓存 使其立即生效
     */
    public static void clearAuthorizationCache(String username){
        RealmSecurityManager rsm = (RealmSecurityManager) IamSecurityUtils.getSecurityManager();
        IamAuthorizingRealm authorizingRealm = (IamAuthorizingRealm)rsm.getRealms().iterator().next();
        if(authorizingRealm != null){
            Cache<Object, AuthorizationInfo> cache = authorizingRealm.getAuthorizationCache();
            if(cache != null) {
                cache.remove(username);
                log.debug("已清空账号 {} 的权限缓存，以便新权限生效.", username);
            }
        }
    }

    /**
     * 清空所有权限信息的缓存 使其立即生效
     */
    public static void clearAllAuthorizationCache(){
        RealmSecurityManager rsm = (RealmSecurityManager) IamSecurityUtils.getSecurityManager();
        IamAuthorizingRealm authorizingRealm = (IamAuthorizingRealm)rsm.getRealms().iterator().next();
        if(authorizingRealm != null){
            Cache<Object, AuthorizationInfo> cache = authorizingRealm.getAuthorizationCache();
            if(cache != null) {
                cache.clear();
                log.debug("已清空全部登录用户的权限缓存，以便新权限生效.");
            }
        }
    }

    /***
     * 对用户密码加密
     * @param iamAccount
     */
    public static void encryptPwd(IamAccount iamAccount){
        if(Cons.DICTCODE_AUTH_TYPE.PWD.name().equals(iamAccount.getAuthType())){
            if(iamAccount.getSecretSalt() == null){
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
    public static String encryptPwd(String password, String salt){
        String encryptedPassword = new SimpleHash(ALGORITHM, password, ByteSource.Util.bytes(salt), ITERATIONS).toHex();
        return encryptedPassword;
    }

    /***
     * 获取客户ip地址
     * @param request
     * @return
     */
    @Deprecated
    public static String getRequestIp(HttpServletRequest request) {
        return HttpHelper.getRequestIp(request);
    }
}
