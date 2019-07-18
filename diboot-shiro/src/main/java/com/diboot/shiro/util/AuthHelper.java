package com.diboot.shiro.util;

import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.S;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthHelper {
    private static final Logger logger = LoggerFactory.getLogger(AuthHelper.class);

    /**
     * 得到当前登录的用户名
     * @return
     */
    public static <T extends BaseEntity>T getCurrentUser(){
        try{
            Subject subject = SecurityUtils.getSubject();
            if(subject != null && subject.isAuthenticated()){
                return (T)subject.getPrincipal();
            }
        }
        catch (Exception e){
            logger.warn("获取用户信息异常", e);
        }
        return null;
    }

    /**
     * 得到当前登录的用户id
     * @return
     */
    public static Long getCurrentUserId(){
        BaseEntity user = getCurrentUser();
        if(user != null){
            return (Long)user.getId();
        }
        if(logger.isDebugEnabled()){
            logger.warn("无法获取当前用户Id!");
        }
        return null;
    }

    /**
     * 创建盐：32位的uuid
     * @return
     */
    public static String createSalt() {
        return S.newUuid();
    }

    /**
     * MD5加密
     * @param password 密码
     * @param salt 盐
     * @param enableSalt 是否启用盐
     * @return 加密后的密码
     */
    public static String encryptMD5(String password, String salt, boolean enableSalt) {
        if (enableSalt) {
            return new Md5Hash(password, salt, 1024).toString();
        } else {
            return new Md5Hash(password).toString();
        }
    }
}
