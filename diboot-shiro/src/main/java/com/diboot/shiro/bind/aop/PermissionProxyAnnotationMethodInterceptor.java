package com.diboot.shiro.bind.aop;

import com.diboot.shiro.bind.annotation.RequiresPermissionsProxy;
import com.diboot.shiro.bind.handler.PermissionProxyAnnotationHandler;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

/**
 * {@link RequiresPermissionsProxy} 拦截器
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-14  22:19
 */
public class PermissionProxyAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {
    /**
     * Default no-argument constructor that ensures this interceptor looks for
     * {@link org.apache.shiro.authz.annotation.RequiresPermissions RequiresPermissions} annotations in a method declaration.
     */
    public PermissionProxyAnnotationMethodInterceptor() {
        super( new PermissionProxyAnnotationHandler() );
    }

    /**
     * @param resolver
     * @since 1.1
     */
    public PermissionProxyAnnotationMethodInterceptor(AnnotationResolver resolver) {
        super( new PermissionProxyAnnotationHandler(), resolver);
    }
}
