package com.diboot.iam.starter;

import com.diboot.core.service.impl.DictionaryServiceImpl;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import com.diboot.iam.jwt.BaseJwtRealm;
import com.diboot.iam.jwt.DefaultJwtAuthFilter;
import com.diboot.iam.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * IAM自动配置类
 * @author : wee
 * @version : v2.0
 * @Date 2019-10-11  10:54
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({IamBaseProperties.class})
@ComponentScan(basePackages = {"com.diboot.iam"})
@MapperScan(basePackages={"com.diboot.iam.mapper"})
@AutoConfigureAfter(value = {DictionaryServiceImpl.class,
        IamRoleServiceImpl.class, IamUserServiceImpl.class, IamAccountServiceImpl.class, IamFrontendPermissionServiceImpl.class,
        IamBaseInitializer.class, ShiroProxyConfig.class})
@Order(2)
public class IamBaseAutoConfig{

    @Autowired
    Environment environment;

    @Autowired
    IamBaseProperties iamBaseProperties;

    @Bean
    @ConditionalOnMissingBean(IamBasePluginManager.class)
    public IamBasePluginManager iamBasePluginManager(){
        IamBasePluginManager pluginManager = new IamBasePluginManager();
        pluginManager.initPlugin(iamBaseProperties);
        return pluginManager;
    }

    /**
     * 根据用户配置的缓存类初始化CacheManager，默认为Shiro内存缓存MemoryConstrainedCacheManager
     * @return
     */
    @Bean
    public CacheManager cacheManager() {
        String className = iamBaseProperties.getCacheManagerClass();
        if(V.isEmpty(className)){
            return null;
        }
        try{
            return (CacheManager) Class.forName(className).newInstance();
        }
        catch (Exception e){
            log.warn("无法初始化CacheManager，请检查配置: diboot.iam.cacheManagerClass");
            return null;
        }
    }

    @Bean
    @DependsOn({"cacheManager"})
    public Realm realm(){
        BaseJwtRealm realm = new BaseJwtRealm();
        CacheManager cacheManager = cacheManager();
        if(cacheManager != null){
            realm.setCachingEnabled(true);
            realm.setCacheManager(cacheManager);
        }
        return realm;
    }

    //TODO 支持无状态

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SessionsSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    protected ShiroFilterFactoryBean shiroFilterFactoryBean(SessionsSecurityManager securityManager){
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

        String anonUrls = iamBaseProperties.getAnonUrls();
        if(V.notEmpty(anonUrls)){
            for(String url : anonUrls.split(Cons.SEPARATOR_COMMA)){
                filterChainDefinitionMap.put(url, "anon");
            }
        }
        filterChainDefinitionMap.put("/login", "authc");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/**", "jwt");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

}