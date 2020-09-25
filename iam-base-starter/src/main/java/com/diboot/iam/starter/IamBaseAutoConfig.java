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
package com.diboot.iam.starter;

import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import com.diboot.iam.jwt.BaseJwtRealm;
import com.diboot.iam.jwt.DefaultJwtAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * IAM自动配置类
 *
 * @author : uu
 * @version : v2.0
 * @Date 2019-10-11  10:54
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({IamBaseProperties.class})
@ComponentScan(basePackages = {"com.diboot.iam"})
@MapperScan(basePackages = {"com.diboot.iam.mapper"})
@Order(10)
public class IamBaseAutoConfig {

    @Autowired
    private IamBaseProperties iamBaseProperties;

    @Bean
    @ConditionalOnMissingBean(IamBasePluginManager.class)
    public IamBasePluginManager iamBasePluginManager() {
        IamBasePluginManager pluginManager = new IamBasePluginManager();
        pluginManager.initPlugin(iamBaseProperties);
        return pluginManager;
    }

    /**
     * 根据用户配置的缓存类初始化CacheManager，默认为Shiro内存缓存MemoryConstrainedCacheManager
     *
     * @return
     */
    @Bean(name = "shiroCacheManager")
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager shiroCacheManager() {
        String className = iamBaseProperties.getCacheManagerClass();
        if (V.isEmpty(className)) {
            return null;
        }
        try {
            return (CacheManager) Class.forName(className).newInstance();
        } catch (Exception e) {
            log.warn("无法初始化CacheManager，请检查配置: diboot.iam.cacheManagerClass");
            return null;
        }
    }

    @Bean
    @DependsOn({"shiroCacheManager"})
    public Realm realm() {
        BaseJwtRealm realm = new BaseJwtRealm();
        CacheManager cacheManager = shiroCacheManager();
        if (cacheManager != null) {
            realm.setCachingEnabled(true);
            realm.setCacheManager(cacheManager);
            realm.setAuthenticationCachingEnabled(true);
        }
        return realm;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SessionsSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    protected ShiroFilterFactoryBean shiroFilterFactoryBean(SessionsSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置过滤器
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt", new DefaultJwtAuthFilter());
        shiroFilterFactoryBean.setFilters(filters);

        //Shiro securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //用户访问未对其授权的资源时的错误提示页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 设置url
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/diboot/**", "anon");
        filterChainDefinitionMap.put("/error/**", "anon");
        filterChainDefinitionMap.put("/auth/**", "anon");
        filterChainDefinitionMap.put("/uploadFile/download/*/image", "anon");

        boolean allAnon = false;
        String anonUrls = iamBaseProperties.getAnonUrls();
        if (V.notEmpty(anonUrls)) {
            for (String url : anonUrls.split(Cons.SEPARATOR_COMMA)) {
                filterChainDefinitionMap.put(url, "anon");
                if (url.equals("/**")) {
                    allAnon = true;
                }
            }
        }
        filterChainDefinitionMap.put("/login", "authc");
        filterChainDefinitionMap.put("/logout", "logout");
        if (allAnon && iamBaseProperties.isEnablePermissionCheck() == false) {
            filterChainDefinitionMap.put("/**", "anon");
        } else {
            filterChainDefinitionMap.put("/**", "jwt");
        }
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

}