package com.diboot.shiro.authz.handler;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import com.diboot.shiro.authz.properties.AuthorizationProperties;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.subject.Subject;

import java.lang.annotation.Annotation;

/**
 * {@link AuthorizationWrapper} 助手类， 参考{@link PermissionAnnotationHandler}实现
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-14  22:19
 */
public class AuthorizationWrapperAnnotationHandler extends AuthorizingAnnotationHandler {

    /**
     * 使用配置中的所有权限角色
     */
    private AuthorizationProperties authorizationProperties;

    /**
     * 标记服务的注解
     */
    public AuthorizationWrapperAnnotationHandler(AuthorizationProperties authorizationProperties) {
        super(AuthorizationWrapper.class);
        this.authorizationProperties = authorizationProperties;
    }


    /**
     * 返回{@link AuthorizationWrapper#value()#value}  value}
     * @param a ${@link AuthorizationWrapper}
     * @return
     */
    protected String[] getAnnotationValue(Annotation a) {
        AuthorizationWrapper rpAnnotation = (AuthorizationWrapper) a;
        return rpAnnotation.value().value();
    }

    /**
     * 校验注解{@link AuthorizationWrapper},自定义包装类，
     * 使用${@link AuthorizationWrapperAnnotationHandler#assertAuthorized(AnnotationResolver, MethodInvocation)}进行权限验证
     */
    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {}

    /**
     * 校验注解{@link AuthorizationWrapper}
     */
    public void assertAuthorized(AnnotationResolver resolver, MethodInvocation mi) throws AuthorizationException {
        //如果方法上存在AuthorizationWrapper注解，那么resolver.getAnnotation()获取的是AuthorizationWrapper注解，会优先从缓存读取
        AuthorizationWrapper authorizationWrapper = (AuthorizationWrapper)resolver.getAnnotation(mi, AuthorizationWrapper.class);
        String[] perms = getAnnotationValue(authorizationWrapper);
        Subject subject = getSubject();
        //当系统配置的所有权限角色集合 ： 如果当前用户包含其中任意一个角色，直接允许访问，否则对当前用户进行资源校验
        if (V.notEmpty(authorizationProperties.getHasAllPermissionsRoleList())) {
            for (String role : authorizationProperties.getHasAllPermissionsRoleList()) {
                if (subject.hasRole(role)) {
                    return;
                }
            }
        } else {
            //当权限没配置的时候，默认ADMIN为最高权限
            if (subject.hasRole("ADMIN")) {
                return;
            }
        }
        //如果忽略前缀，那么则不拼接前缀
        perms = addPrefix(resolver, mi, authorizationWrapper, perms);
        if (perms.length == 1) {
            subject.checkPermission(perms[0]);
            return;
        }
        if (Logical.AND.equals(authorizationWrapper.value().logical())) {
            getSubject().checkPermissions(perms);
            return;
        }
        if (Logical.OR.equals(authorizationWrapper.value().logical())) {
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
     * 向 perms 追加前缀
     * @param resolver
     * @param mi
     * @param authorizationWrapper
     * @param perms
     * @return
     */
    private String[] addPrefix(AnnotationResolver resolver, MethodInvocation mi, AuthorizationWrapper authorizationWrapper, String[] perms) {
        if (!authorizationWrapper.ignorePrefix()) {
            String prefix = "";
            if (V.notEmpty(authorizationWrapper.prefix())) {
                prefix = authorizationWrapper.prefix();
            } else {
                //如果自身不定义，查找前缀注解，存在则设置值
                AuthorizationPrefix authorizationPrefix = (AuthorizationPrefix)resolver.getAnnotation(mi, AuthorizationPrefix.class);
                if (V.notEmpty(authorizationPrefix)) {
                    prefix = authorizationPrefix.prefix();
                }
            }
            String [] permsTemp = new String[perms.length];
            //前缀存在的时候，才做组装，其他情况不处理
            if (V.notEmpty(prefix)) {
                for (int i = 0; i < perms.length; i++) {
                    permsTemp[i] = S.join(prefix, ":", perms[i]);
                }
                perms = permsTemp;
            }
        }
        return perms;
    }
}
