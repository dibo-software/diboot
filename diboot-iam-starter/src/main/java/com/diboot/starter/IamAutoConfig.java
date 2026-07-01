/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.starter;

import com.diboot.core.cache.BaseCacheManager;
import com.diboot.core.cache.DictionaryCacheManager;
import com.diboot.core.cache.DynamicMemoryCacheManager;
import com.diboot.core.util.V;
import com.diboot.iam.auth.IamExtensible;
import com.diboot.iam.auth.impl.IamExtensibleImpl;
import com.diboot.iam.cache.SystemConfigCacheManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.config.IamProperties;
import com.diboot.iam.init.IamRedisAutoConfig;
import com.diboot.iam.shiro.IamAuthorizingRealm;
import com.diboot.iam.shiro.ShiroContextTaskDecorator;
import com.diboot.iam.shiro.StatelessAccessControlFilter;
import com.diboot.iam.shiro.StatelessSubjectFactory;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskDecorator;

import java.util.HashMap;
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
@Order(912)
@Configuration
@AutoConfigureAfter(IamRedisAutoConfig.class)
@EnableConfigurationProperties({IamProperties.class})
@ComponentScan(basePackages = {"com.diboot.iam"})
@MapperScan(basePackages = {"com.diboot.iam.mapper"})
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class IamAutoConfig {

    public IamAutoConfig() {
        log.info("初始化 IAM 组件 自动配置");
    }

    @Autowired
    private IamProperties iamProperties;

    /**
     * 根据用户配置的缓存类初始化CacheManager，默认为Shiro内存缓存MemoryConstrainedCacheManager
     *
     * @return
     */
    @Bean(name = "shiroCacheManager")
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheManager shiroCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn({"shiroCacheManager"})
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    protected SessionStorageEvaluator sessionStorageEvaluator() {
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return sessionStorageEvaluator;
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DefaultWebSubjectFactory subjectFactory() {
        StatelessSubjectFactory subjectFactory = new StatelessSubjectFactory();
        return subjectFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public WebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }

    /**
     * 配置ShiroFilter
     *
     * @return
     */
    public AccessControlFilter shiroFilter() {
        return new StatelessAccessControlFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Lazy SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    protected ShiroFilterFactoryBean shiroFilterFactoryBean(SessionsSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置过滤器
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("accessControlFilter", shiroFilter());
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
        Map<String, String> filterChainMap = new LinkedHashMap<>();
        // 设置url
        filterChainMap.put("/static/**", "anon");
        filterChainMap.put("/error/**", "anon");
        filterChainMap.put("/auth/captcha", "anon");
        filterChainMap.put("/auth/login", "anon");
        filterChainMap.put("/auth/token", "anon");
        filterChainMap.put("/auth/client-login", "anon");

        Set<String> anonUrls = iamProperties.getAnonUrls();
        if (V.notEmpty(anonUrls)) {
            for (String url : anonUrls) {
                filterChainMap.put(url, "anon");
            }
        }
        filterChainMap.put("/login", "authc");
        if (V.notEmpty(anonUrls) && anonUrls.contains("/**") && !iamProperties.isEnablePermissionCheck()) {
            log.warn("权限检查已停用，该配置仅用于开发环境 !");
            filterChainMap.put("/**", "anon");
        } else {
            filterChainMap.put("/**", "accessControlFilter");
        }
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinitions(filterChainMap);
        return chainDefinition;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public EventBus eventBus() {
        return new DefaultEventBus();
    }

    /**
     * 用户token缓存管理器
     *
     * @return
     */
    @Bean(name = "iamCacheManager")
    @ConditionalOnMissingBean(name = "iamCacheManager")
    public BaseCacheManager iamCacheManager() {
        log.info("初始化 IAM 内存缓存: DynamicMemoryCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<String, Integer>() {{
            put(Cons.CACHE_TOKEN_USERINFO, iamProperties.getTokenExpiresMinutes());
            put(Cons.CACHE_TOKEN_REFRESH, 10);
            put(Cons.CACHE_CAPTCHA, 5);
        }};
        return new DynamicMemoryCacheManager(cacheName2ExpireMap);
    }

    /**
     * 指定子线程传递用户信息
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskDecorator shiroContextTaskDecorator() {
        log.info("初始化 ShiroContextTaskDecorator");
        return new ShiroContextTaskDecorator();
    }

    /**
     * 系统配置数据缓存管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SystemConfigCacheManager systemConfigCacheManager() {
        log.info("初始化 SystemConfig 内存缓存: DynamicMemoryCacheManager");
        Map<String, Integer> cacheName2ExpireMap = new HashMap<>() {{
            put(com.diboot.core.config.Cons.CACHE_NAME_SYSTEM_CONFIG, 24 * 60);
        }};
        DynamicMemoryCacheManager memoryCacheManager = new DynamicMemoryCacheManager(cacheName2ExpireMap);
        return new SystemConfigCacheManager(memoryCacheManager);
    }

    /**
     * Iam扩展配置
     */
    @Bean
    @ConditionalOnMissingBean
    public IamExtensible iamExtensible() {
        return new IamExtensibleImpl();
    }

}
