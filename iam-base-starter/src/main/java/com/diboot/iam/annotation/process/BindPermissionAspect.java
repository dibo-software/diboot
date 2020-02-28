package com.diboot.iam.annotation.process;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.config.Cons;
import com.diboot.iam.exception.PermissionException;
import com.diboot.iam.util.AnnotationUtils;
import com.diboot.iam.util.IamSecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * BindPermission注解的切面处理
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/30
 */
@Aspect
@Component
public class BindPermissionAspect {

    /**
     * 注解切面
     */
    @Pointcut("@annotation(com.diboot.iam.annotation.BindPermission)")
    public void pointCut() {}

    /**
     * 权限处理
     * @param joinPoint
     */
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        // 超级管理员 权限放过
        if (IamSecurityUtils.getSubject().hasRole(Cons.ROLE_SUPER_ADMIN)) {
            return;
        }
        // 获取当前uri
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
        // 根据uri获取对应权限标识
        String uriMapping = formatUriMapping(request);
        String permissionCode = ApiPermissionCache.getPermissionCode(request.getMethod(), uriMapping);
        if (permissionCode == null){
            return;
        }
        // 需要验证
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        BindPermission bindPermission = AnnotationUtils.getAnnotation(method, BindPermission.class);
        if(permissionCode.endsWith(":"+bindPermission.code())){
            try{
                IamSecurityUtils.getSubject().checkPermission(permissionCode);
            }
            catch (Exception e){
                throw new PermissionException(e);
            }
        }
    }

    /**
     * 格式化URL，将当前url转换为Mapping定义中参数化url
     * @param request
     * @return
     */
    private String formatUriMapping(HttpServletRequest request){
        boolean hasContextPath = (V.notEmpty(request.getContextPath()) && !request.getContextPath().equals("/"));
        String url = hasContextPath? S.substringAfter(request.getRequestURI(), request.getContextPath()) : request.getRequestURI();
        Map<String, Object> map = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if(V.notEmpty(map)){
            for(Map.Entry<String, Object> entry : map.entrySet()){
                if(url.endsWith("/"+entry.getValue())){
                    url = S.substringBeforeLast(url,"/"+entry.getValue()) + "/{"+entry.getKey()+"}";
                }
                else if(url.contains("/"+entry.getValue()+"/")){
                    url = S.replace(url,"/"+entry.getValue()+"/","/{"+entry.getKey()+"}/");
                }
            }
        }
        return url;
    }
}
