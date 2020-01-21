package com.diboot.iam.auth;

import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账号认证Manager
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/25
 */
@Slf4j
public class AuthServiceFactory {
    private static Map<String, AuthService> AUTHTYPE_SERVICE_CACHE = new HashMap<>();

    /**
     * 获取对应认证类型的Service实现
     * @param authType
     * @return
     */
    public static AuthService getAuthService(String authType){
        if(AUTHTYPE_SERVICE_CACHE.isEmpty()){
            List<AuthService> authServiceList = ContextHelper.getBeans(AuthService.class);
            if(V.notEmpty(authServiceList)){
                authServiceList.stream().forEach((service)->{
                    AUTHTYPE_SERVICE_CACHE.put(service.getAuthType(), service);
                });
            }
        }
        AuthService service = AUTHTYPE_SERVICE_CACHE.get(authType);
        if(service == null){
            log.warn("无法找到认证类型: {} 的AccountAuthService实现，请检查！");
        }
        return service;
    }
}
