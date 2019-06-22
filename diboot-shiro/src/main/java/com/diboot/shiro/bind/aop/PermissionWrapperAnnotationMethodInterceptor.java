package com.diboot.shiro.bind.aop;

import com.diboot.shiro.bind.annotation.RequiresPermissionsWrapper;
import com.diboot.shiro.bind.handler.PermissionWrapperAnnotationHandler;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

/**
 * {@link RequiresPermissionsWrapper} 拦截器
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-14  22:19
 */
public class PermissionWrapperAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {
    /**
     * Default no-argument constructor that ensures this interceptor looks for
     * {@link org.apache.shiro.authz.annotation.RequiresPermissions RequiresPermissions} annotations in a method declaration.
     */
    public PermissionWrapperAnnotationMethodInterceptor() {
        super( new PermissionWrapperAnnotationHandler() );
    }

    /**
     * @param resolver
     * @since 1.1
     */
    public PermissionWrapperAnnotationMethodInterceptor(AnnotationResolver resolver) {
        super( new PermissionWrapperAnnotationHandler(), resolver);
    }

    /**
     * 当使用RequiresPermissionsWrapper注解进行权限验证的时候，自动的去追加前缀
     * @param mi
     * @throws AuthorizationException
     */
    @Override
    public void assertAuthorized(MethodInvocation mi) throws AuthorizationException {
        try {

            //默认是直接调用方法上注解，现在修改成 获取类和方法上的注解
//            ((AuthorizingAnnotationHandler)getHandler()).assertAuthorized(getAnnotation(mi));
            ((PermissionWrapperAnnotationHandler)getHandler()).assertAuthorized(getResolver(), mi);
        }
        catch(AuthorizationException ae) {
            if (ae.getCause() == null) {
                ae.initCause(new AuthorizationException("Not authorized to invoke method: " + mi.getMethod()));
            }
            throw ae;
        }
    }
}
