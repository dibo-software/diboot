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

import com.diboot.core.util.AnnotationUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.cache.IamPermissionCacheManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.exception.PermissionException;
import com.diboot.iam.config.IamProperties;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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
    @Autowired
    private IamProperties iamProperties;

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
        if(!iamProperties.isEnablePermissionCheck()){
            log.debug("BindPermission权限检查已停用，如需启用请删除配置项: diboot.iam.enable-permission-check=false");
            return;
        }
        // 超级管理员 权限放过
        if (IamSecurityUtils.isSuperAdmin()) {
            return;
        }
        // 需要验证
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        BindPermission methodAnno = AnnotationUtils.getAnnotation(method, BindPermission.class);
        String permissionCode = methodAnno.code();
        Class<?> controllerClass = joinPoint.getTarget().getClass();
        ApiPermissionWrapper classAnno = IamPermissionCacheManager.getPermissionCodeWrapper(controllerClass);
        if(V.notEmpty(classAnno.getCode())){
            permissionCode = classAnno.getCode() + ":" + permissionCode;
        }
        try{
            IamSecurityUtils.getSubject().checkPermission(permissionCode);
        }
        catch (UnauthenticatedException e){
            throw new PermissionException(Status.FAIL_INVALID_TOKEN, e);
        }
        catch (Exception e){
            BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
            String loginUser = currentUser != null? currentUser.getDisplayName() : null;
            log.warn("用户 {} 无 {} 的访问权限", loginUser, permissionCode);
            throw new PermissionException(Status.FAIL_NO_PERMISSION, e);
        }
    }

}
