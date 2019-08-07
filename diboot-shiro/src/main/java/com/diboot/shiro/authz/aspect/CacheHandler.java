package com.diboot.shiro.authz.aspect;

import com.diboot.core.util.V;
import com.diboot.shiro.authz.annotation.AuthorizationCache;
import com.diboot.shiro.jwt.BaseJwtRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.tomcat.util.http.parser.Authorization;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 当有操作的时候，自动更新被注解的相关数据
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-24  23:20
 */
@Slf4j
@Aspect
@Component
public class CacheHandler{

    private static final String DEFAULT_AUTHORIZATION_CACHE_SUFFIX = ".authorizationCache";

    @Autowired
    private CacheManager cacheManager;

    /**
     * 设置切片
     */
    @Pointcut("@annotation(com.diboot.shiro.authz.annotation.AuthorizationCache)")
    public void proxyAspect() {}

    /**
     * 当请求{@link AuthorizationCache}注解的方法执行完成后，自动触发此处切面
     * 作用：重新缓存权限和方法
     * @param joinPoint
     */
    @AfterReturning("proxyAspect()")
    public void afterReturning(JoinPoint joinPoint) {
        try {
            log.info("【修改权限】==> 正在调用【{}#{}()】方法修改！", joinPoint.getThis().getClass(), joinPoint.getSignature().getName());
            Cache<Object, Authorization> cache = cacheManager.getCache(BaseJwtRealm.class.getName() + DEFAULT_AUTHORIZATION_CACHE_SUFFIX);
            //统一删除所有的缓存，所有用户需重新加载缓存
            if (V.notEmpty(cache) && cache.size() > 0) {
                cache.clear();
            }
        } catch (Exception e) {
            log.info("【修改权限】==> 调用【{}#{}()】异常:", joinPoint.getThis().getClass(), joinPoint.getSignature().getName(), e);
        }
    }
}
