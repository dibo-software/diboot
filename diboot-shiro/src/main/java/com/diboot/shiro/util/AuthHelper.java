package com.diboot.shiro.util;

import com.diboot.core.entity.BaseEntity;
import org.apache.shiro.SecurityUtils;
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

}
