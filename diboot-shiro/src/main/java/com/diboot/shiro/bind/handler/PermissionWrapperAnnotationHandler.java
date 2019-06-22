package com.diboot.shiro.bind.handler;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.shiro.bind.annotation.PermissionsPrefix;
import com.diboot.shiro.bind.annotation.RequiresPermissionsWrapper;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.subject.Subject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * {@link RequiresPermissionsWrapper} 助手类， 参考{@link PermissionAnnotationHandler}实现
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-14  22:19
 */
public class PermissionWrapperAnnotationHandler extends AuthorizingAnnotationHandler {

    private final static String REQUIRES_PERMISSIONS_VALUE = "value";

    private final static String REQUIRES_PERMISSIONS_LOGICAL = "logical";

    private final static String JDK_MEMBER_VALUES = "memberValues";

    /**
     * 标记服务的注解
     */
    public PermissionWrapperAnnotationHandler() {
        super(RequiresPermissionsWrapper.class);
    }


    /**
     * Returns the annotation {@link RequiresPermissions#value value}, from which the Permission will be constructed.
     *
     * @param a the RequiresPermissions annotation being inspected.
     * @return the annotation's <code>value</code>, from which the Permission will be constructed.
     */
    protected String[] getAnnotationValue(Annotation a) {
        RequiresPermissionsWrapper rpAnnotation = (RequiresPermissionsWrapper) a;
        return rpAnnotation.value();
    }

    /**
     * 校验注解{@link RequiresPermissionsWrapper}
     */
    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RequiresPermissionsWrapper)) {
            return;
        }
        RequiresPermissionsWrapper rppAnnotation = (RequiresPermissionsWrapper) a;
        String[] perms = getAnnotationValue(a);
        Subject subject = getSubject();

        if (perms.length == 1) {
            subject.checkPermission(perms[0]);
            return;
        }
        if (Logical.AND.equals(rppAnnotation.logical())) {
            getSubject().checkPermissions(perms);
            return;
        }
        if (Logical.OR.equals(rppAnnotation.logical())) {
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms) {
                if (getSubject().isPermitted(permission)) {
                    hasAtLeastOnePermission = true;
                }
            }
            if (!hasAtLeastOnePermission) {
                getSubject().checkPermission(perms[0]);
            }
        }
    }

    /**
     * 校验注解{@link RequiresPermissionsWrapper}
     */
    public void assertAuthorized(AnnotationResolver resolver, MethodInvocation mi) throws AuthorizationException {
        //如果方法上存在RequiresPermissionsWrapper注解，那么resolver.getAnnotation()获取的是RequiresPermissionsWrapper注解
        //优先从缓存读取
        RequiresPermissionsWrapper requiresPermissionsWrapper = (RequiresPermissionsWrapper)resolver.getAnnotation(mi, RequiresPermissionsWrapper.class);
        String prefix = "";
        if (V.notEmpty(requiresPermissionsWrapper.prefix())) {
            prefix = requiresPermissionsWrapper.prefix();
        } else {
            //如果自身不定义，查找前缀注解，存在则设置值
            PermissionsPrefix permissionsPrefix = (PermissionsPrefix)resolver.getAnnotation(mi, PermissionsPrefix.class);
            if (V.notEmpty(permissionsPrefix)) {
                prefix = permissionsPrefix.prefix();
            }
        }

        String[] perms = getAnnotationValue(requiresPermissionsWrapper);
        Subject subject = getSubject();

        String [] permsTemp = new String[perms.length];
        //前缀存在的时候，才做组装，其他情况不处理
        if (V.notEmpty(prefix)) {
            for (int i = 0; i < perms.length; i++) {
                permsTemp[i] = S.join(prefix, ":", perms[i]);
            }
            perms = permsTemp;
        }

        if (perms.length == 1) {
            subject.checkPermission(perms[0]);
            return;
        }
        if (Logical.AND.equals(requiresPermissionsWrapper.logical())) {
            getSubject().checkPermissions(perms);
            return;
        }
        if (Logical.OR.equals(requiresPermissionsWrapper.logical())) {
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms) {
                if (getSubject().isPermitted(permission)) {
                    hasAtLeastOnePermission = true;
                }
            }
            if (!hasAtLeastOnePermission) {
                getSubject().checkPermission(perms[0]);
            }
        }
    }

    /**
     * 动态修改注解的值（不可用）
     * @param rppAnnotation
     */
    @Deprecated
    private void proxy(RequiresPermissionsWrapper rppAnnotation) {
                try {
            //获取RequiresPermissionsProxy上的RequiresPermissions注解
            RequiresPermissions requiresPermissions = rppAnnotation.annotationType().getAnnotation(RequiresPermissions.class);

            InvocationHandler invocationHandler = Proxy.getInvocationHandler(requiresPermissions);
            /* memberValues 为JDK中存储所有成员变量值的Map {@link AnnotationInvocationHandler#memberValues}*/
            Field jdkValue = invocationHandler.getClass().getDeclaredField(JDK_MEMBER_VALUES);
            jdkValue.setAccessible(true);
            /*获取RequiresPermissions对应的代理属性值*/
            Map<String, Object> memberValues = (Map<String, Object>) jdkValue.get(invocationHandler);
            /*动态设置RequiresPermissions注解的内容*/
            memberValues.put(REQUIRES_PERMISSIONS_VALUE, rppAnnotation.value());
            memberValues.put(REQUIRES_PERMISSIONS_LOGICAL, rppAnnotation.logical());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
