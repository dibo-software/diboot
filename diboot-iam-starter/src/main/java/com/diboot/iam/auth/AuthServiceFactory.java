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
package com.diboot.iam.auth;

import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;

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
    private static final Map<String, AuthService> AUTHTYPE_SERVICE_CACHE = new HashMap<>();

    /**
     * 获取对应认证类型的Service实现
     * @param authType
     * @return
     */
    public static AuthService getAuthService(String authType){
        if(AUTHTYPE_SERVICE_CACHE.isEmpty()){
            List<AuthService> authServiceList = ContextHolder.getBeans(AuthService.class);
            if(V.notEmpty(authServiceList)){
                authServiceList.stream().forEach((service)->{
                    if(AUTHTYPE_SERVICE_CACHE.containsKey(service.getAuthType())){
                        if(service.getClass().getAnnotation(Primary.class) != null){
                            AUTHTYPE_SERVICE_CACHE.put(service.getAuthType(), service);
                        }
                    }
                    else{
                        AUTHTYPE_SERVICE_CACHE.put(service.getAuthType(), service);
                    }
                });
            }
        }
        AuthService service = AUTHTYPE_SERVICE_CACHE.get(authType);
        if(service == null){
            log.warn("无法找到认证类型: {} 的AccountAuthService实现，请检查！", authType);
        }
        return service;
    }
}
