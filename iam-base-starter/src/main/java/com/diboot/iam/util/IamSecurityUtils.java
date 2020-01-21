package com.diboot.iam.util;

import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;

/**
 * IAM认证相关工具类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/26
 */
public class IamSecurityUtils extends SecurityUtils {

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
