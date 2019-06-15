package com.diboot.shiro.bind.handler;

import com.diboot.shiro.bind.annotation.RequiresPermissionsProxy;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;

/**
 * {@link RequiresPermissionsProxy} 助手类， 参考{@link PermissionAnnotationHandler}实现
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-14  22:19
 */
public class PermissionProxyAnnotationHandler extends AuthorizingAnnotationHandler {

    private final static String REQUIRES_PERMISSIONS_VALUE = "value";

    private final static String REQUIRES_PERMISSIONS_LOGICAL = "logical";

    private final static String JDK_MEMBER_VALUES = "memberValues";

    /**
     * 标记服务的注解
     */
    public PermissionProxyAnnotationHandler() {
        super(RequiresPermissionsProxy.class);
    }

    /**
     * 将{@link RequiresPermissionsProxy} 代理的内容赋值给{@link RequiresPermissions}
     */
    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RequiresPermissionsProxy)) {
            return;
        }
        RequiresPermissionsProxy rrAnnotation = (RequiresPermissionsProxy) a;
        try {
            //获取RequiresPermissionsProxy上的RequiresPermissions注解
            RequiresPermissions requiresPermissions = rrAnnotation.annotationType().getAnnotation(RequiresPermissions.class);

            InvocationHandler invocationHandler = Proxy.getInvocationHandler(requiresPermissions);
            /* memberValues 为JDK中存储所有成员变量值的Map {@link AnnotationInvocationHandler#memberValues}*/
            Field jdkValue = invocationHandler.getClass().getDeclaredField(JDK_MEMBER_VALUES);
            jdkValue.setAccessible(true);
            /*获取RequiresPermissions对应的代理属性值*/
            Map<String, Object> memberValues = (Map<String, Object>) jdkValue.get(invocationHandler);
            /*动态设置RequiresPermissions注解的内容*/
            memberValues.put(REQUIRES_PERMISSIONS_VALUE, rrAnnotation.value());
            memberValues.put(REQUIRES_PERMISSIONS_LOGICAL, rrAnnotation.logical());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
