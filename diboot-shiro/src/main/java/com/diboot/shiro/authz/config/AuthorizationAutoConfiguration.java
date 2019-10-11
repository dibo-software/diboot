package com.diboot.shiro.authz.config;

import com.diboot.shiro.authz.properties.AuthCacheProperties;
import com.diboot.shiro.authz.properties.AuthorizationProperties;
import com.diboot.shiro.authz.storage.AuthorizationStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 权限配置
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-27  10:30
 */
@Configuration
@EnableConfigurationProperties({AuthorizationProperties.class, AuthCacheProperties.class})
public class AuthorizationAutoConfiguration {

    @Autowired
    private AuthorizationProperties authorizationProperties;

    /**
     * 注入权限存储
     * @return
     */
    @Bean
    public AuthorizationStorage authorizationStorage() {
        return new AuthorizationStorage(authorizationProperties.getEnv().getEnv(), authorizationProperties.isStorage());
    }
}
