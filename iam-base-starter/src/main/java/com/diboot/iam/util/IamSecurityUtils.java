package com.diboot.iam.util;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.jwt.BaseJwtRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;

import javax.servlet.http.HttpServletRequest;

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
            subject.logout();
        }
    }

    /**
     * 清空指定用户账户的权限信息的缓存 使其立即生效
     */
    public static void clearAuthorizationCache(String username){
        RealmSecurityManager rsm = (RealmSecurityManager) IamSecurityUtils.getSecurityManager();
        BaseJwtRealm baseJwtRealm = (BaseJwtRealm)rsm.getRealms().iterator().next();
        if(baseJwtRealm != null){
            Cache<Object, AuthorizationInfo> cache = baseJwtRealm.getAuthorizationCache();
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
        BaseJwtRealm baseJwtRealm = (BaseJwtRealm)rsm.getRealms().iterator().next();
        if(baseJwtRealm != null){
            Cache<Object, AuthorizationInfo> cache = baseJwtRealm.getAuthorizationCache();
            if(cache != null) {
                for(Object key : cache.keys()) {
                    cache.remove(key);
                }
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

    private static final String[] HEADER_IP_KEYWORDS = {"X-Forwarded-For", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP"};
    /***
     * 获取客户ip地址
     * @param request
     * @return
     */
    public static String getRequestIp(HttpServletRequest request) {
        for(String header : HEADER_IP_KEYWORDS){
            String ipAddresses = request.getHeader(header);
            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
                continue;
            }
            if (V.notEmpty(ipAddresses)) {
                return ipAddresses.split(Cons.SEPARATOR_COMMA)[0];
            }
        }
        return request.getRemoteAddr();
    }
}
