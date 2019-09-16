package com.diboot.shiro.config;

import com.diboot.core.util.V;
import com.diboot.shiro.authz.aop.CustomAuthorizationAttributeSourceAdvisor;
import com.diboot.shiro.authz.cache.MemoryCondition;
import com.diboot.shiro.authz.cache.RedisCacheManager;
import com.diboot.shiro.authz.cache.RedisCondition;
import com.diboot.shiro.authz.properties.AuthorizationProperties;
import com.diboot.shiro.authz.properties.AuthCacheProperties;
import com.diboot.shiro.jwt.BaseJwtAuthenticationFilter;
import com.diboot.shiro.jwt.BaseJwtRealm;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/***
 *
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Configuration
@AutoConfigureAfter(AuthCacheProperties.class)
@EnableConfigurationProperties({AuthorizationProperties.class, AuthCacheProperties.class})
public class ShiroConfig {
    private static final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    @Autowired
    private AuthorizationProperties authorizationProperties;

    @Autowired
    private AuthCacheProperties authCacheProperties;

    /**
     * 将数据缓存到内存中
     * @return
     */
    @Bean("cacheManager")
    @Conditional(MemoryCondition.class)
    public CacheManager memoryCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    /**
     * 将数据存储到redis缓存
     * @return
     */
    @Bean("cacheManager")
    @Conditional(RedisCondition.class)
    public CacheManager redisCacheManager()  {
        return new RedisCacheManager();
    }


    @Bean
    public Realm realm(){
        BaseJwtRealm realm = new BaseJwtRealm();
        if (authCacheProperties.isPermissionCachingEnabled()) {
            //设置权限缓存
            realm.setCachingEnabled(true);
            CacheManager cacheManager = V.notEmpty(redisCacheManager())? redisCacheManager(): memoryCacheManager();
            realm.setCacheManager(cacheManager);
        }
        return realm;
    }

    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //Shiro securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //用户访问未对其授权的资源时的错误提示页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinition());

        // 设置过滤器
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("anon", new AnonymousFilter());
        filters.put("jwt", new BaseJwtAuthenticationFilter());
        shiroFilterFactoryBean.setFilters(filters);

        return shiroFilterFactoryBean;
    }

    @Bean
    public Map filterChainDefinition(){
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/error/**", "anon");
        filterChainDefinitionMap.put("/*.html", "anon");
        filterChainDefinitionMap.put("/diboot/**", "anon");

        filterChainDefinitionMap.put("/auth/login", "anon");
        filterChainDefinitionMap.put("/auth/buildOAuthUrl", "anon");
        filterChainDefinitionMap.put("/auth/apply", "anon");
        filterChainDefinitionMap.put("/auth/register", "anon");
        filterChainDefinitionMap.put("/auth/static", "anon");
        filterChainDefinitionMap.put("/error", "anon");
        filterChainDefinitionMap.put("/auth/logout", "logout");
        filterChainDefinitionMap.put("/**", "jwt");

        return filterChainDefinitionMap;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /***
     * 以下两个为使用注解权限相关的配置
     * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public CustomAuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        CustomAuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new CustomAuthorizationAttributeSourceAdvisor(authorizationProperties);
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }
}
