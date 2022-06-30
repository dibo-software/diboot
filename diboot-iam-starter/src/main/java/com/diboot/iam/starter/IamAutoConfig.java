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

import com.diboot.core.cache.BaseCacheManager;
import com.diboot.core.cache.DynamicMemoryCacheManager;
import com.diboot.core.data.access.DataAccessInterface;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import com.diboot.iam.data.DataAccessPermissionUserOrgImpl;
import com.diboot.iam.shiro.IamAuthorizingRealm;
import com.diboot.iam.shiro.StatelessAccessControlFilter;
import com.diboot.iam.shiro.StatelessSubjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * IAM自动配置类
 *
 * @author : uu
 * @version : v2.0
 * @date 2019-10-11  10:54
 */
@SuppressWarnings("JavaDoc")
@Slf4j
@Order(922)
@Configuration
@EnableConfigurationProperties({IamProperties.class})
@ComponentScan(basePackages = {"com.diboot.iam"})
@MapperScan(basePackages = {"com.diboot.iam.mapper"})
public class IamAutoConfig {

    @Autowired
    private IamProperties iamProperties;

    /**
     * 根据用户配置的缓存类初始化CacheManager，默认为Shiro内存缓存MemoryConstrainedCacheManager
     *
     * @return
     */
    @Bean(name = "shiroCacheManager")
    @ConditionalOnMissingBean
    public CacheManager shiroCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn({"shiroCacheManager"})
    public Realm realm() {
        IamAuthorizingRealm realm = new IamAuthorizingRealm();
        CacheManager cacheManager = shiroCacheManager();
        if (cacheManager != null) {
            realm.setCachingEnabled(true);
            realm.setAuthenticationCachingEnabled(true);
            realm.setCacheManager(cacheManager);
        }
        return realm;
    }

    /**
     * 配置securityManager
     *
     * @return
     */
    @Bean(name = "shiroSecurityManager")
    @ConditionalOnMissingBean
    public DefaultWebSecurityManager shiroSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSubjectFactory(subjectFactory());
        securityManager.setSessionManager(sessionManager());
        securityManager.setRealm(realm());
        securityManager.setCacheManager(shiroCacheManager());
        // subject禁止存储到session
        ((DefaultSubjectDAO) securityManager.getSubjectDAO()).setSessionStorageEvaluator(sessionStorageEvaluator());
        return securityManager;
    }

    @Bean
    @ConditionalOnMissingBean
    protected SessionStorageEvaluator sessionStorageEvaluator() {
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return sessionStorageEvaluator;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultWebSubjectFactory subjectFactory(){
        StatelessSubjectFactory subjectFactory = new StatelessSubjectFactory();
        return subjectFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultSessionManager sessionManager(){
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }

    /**
     * 配置ShiroFilter
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessControlFilter shiroFilter() {
        return new StatelessAccessControlFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Lazy SecurityManager securityManager) {
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
        filters.put("jwt", shiroFilter());
        shiroFilterFactoryBean.setFilters(filters);
        //Shiro securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //用户访问未对其授权的资源时的错误提示页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition().getFilterChainMap());
        return shiroFilterFactoryBean;
    }

    @Bean
    @ConditionalOnMissingBean
    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
        Map<String, String> filterChainMap = new LinkedHashMap<>();
        // 设置url
        filterChainMap.put("/static/**", "anon");
        filterChainMap.put("/diboot/**", "anon");
        filterChainMap.put("/error/**", "anon");
        filterChainMap.put("/auth/captcha", "anon");
        filterChainMap.put("/auth/login", "anon");
        filterChainMap.put("/auth/token", "anon");
        filterChainMap.put("/auth/2step-code", "anon");
        filterChainMap.put("/uploadFile/download/*/image", "anon");

        Set<String> anonUrls = iamProperties.getAnonUrls();
        if (V.notEmpty(anonUrls)) {
            for (String url : anonUrls) {
                filterChainMap.put(url, "anon");
            }
        }
        filterChainMap.put("/login", "authc");
        if (V.notEmpty(anonUrls) && anonUrls.contains("/**") && !iamProperties.isEnablePermissionCheck()) {
            filterChainMap.put("/**", "anon");
        } else {
            filterChainMap.put("/**", "jwt");
        }
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinitions(filterChainMap);
        return chainDefinition;
    }

    /**
     * 用户token缓存管理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public BaseCacheManager baseCacheManager(){
        log.info("初始化IAM本地缓存: DynamicMemoryCacheManager");
        return new DynamicMemoryCacheManager(iamProperties.getTokenExpiresMinutes(),
                Cons.CACHE_TOKEN_USERINFO,
                Cons.CACHE_CAPTCHA);
    }

    /**
     * 数据访问控制实现，默认基于用户和部门过滤
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DataAccessInterface dataAccessInterface(){
        return new DataAccessPermissionUserOrgImpl();
    }

}
