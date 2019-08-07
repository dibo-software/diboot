package com.diboot.shiro.authz.aop;

import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import com.diboot.shiro.authz.handler.AuthorizationWrapperAnnotationHandler;
import com.diboot.shiro.authz.properties.AuthorizationProperties;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

/**
 * {@link AuthorizationWrapper} 拦截器
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-14  22:19
 */
public class AuthorizationWrapperAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {
    /**
     * Default no-argument constructor that ensures this interceptor looks for
     * {@link AuthorizationWrapper AuthorizationWrapper} annotations in a method declaration.
     */
    public AuthorizationWrapperAnnotationMethodInterceptor(AuthorizationProperties authorizationProperties) {
        super( new AuthorizationWrapperAnnotationHandler(authorizationProperties) );
    }

    /**
     * @param resolver
     * @since 1.1
     */
    public AuthorizationWrapperAnnotationMethodInterceptor(AnnotationResolver resolver, AuthorizationProperties authorizationProperties) {
        super( new AuthorizationWrapperAnnotationHandler(authorizationProperties), resolver);
    }

    /**
     * 当使用AuthorizationWrapper注解进行权限验证的时候，自动的去追加前缀
     * @param mi
     * @throws AuthorizationException
     */
    @Override
    public void assertAuthorized(MethodInvocation mi) throws AuthorizationException {
        try {
            ((AuthorizationWrapperAnnotationHandler)getHandler()).assertAuthorized(getResolver(), mi);
        }
        catch(AuthorizationException ae) {
            if (ae.getCause() == null) {
                ae.initCause(new AuthorizationException("Not authorized to invoke method: " + mi.getMethod()));
            }
            throw ae;
        }
    }
}
