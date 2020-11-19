/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.annotation.process;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.config.Cons;
import com.diboot.core.util.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class BindPermissionAspect {
    @Autowired(required = false)
    private IamCustomize iamCustomize;

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
        if(iamCustomize.isEnablePermissionCheck() == false){
            log.debug("BindPermission权限检查已停用，如需启用请删除配置项: diboot.iam.enable-permission-check");
            return;
        }
        // 超级管理员 权限放过
        if (iamCustomize.checkCurrentUserHasRole(Cons.ROLE_SUPER_ADMIN)) {
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
            iamCustomize.checkPermission(permissionCode);
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
