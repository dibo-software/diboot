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

import com.diboot.core.util.*;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.config.Cons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 注解提取器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Slf4j
public class ApiPermissionExtractor {

    /**
     * 接口权限缓存
     */
    private static List<ApiPermissionWrapper> API_PERMISSION_CACHE = null;
    /**
     * 唯一KEY
     */
    private static Set<String> UNIQUE_KEY_SET = null;

    /**
     * 提取所有的权限定义
     * @return
     */
    public static List<ApiPermissionWrapper> extractAllApiPermissions(){
        if(API_PERMISSION_CACHE == null){
            API_PERMISSION_CACHE = new ArrayList<>();
            UNIQUE_KEY_SET = new HashSet<>();
            // 初始化
            // 提取rest controller
            List<Object> controllerList = ContextHelper.getBeansByAnnotation(RestController.class);
            extractApiPermissions(controllerList);
            // 提取controller
            controllerList = ContextHelper.getBeansByAnnotation(Controller.class);
            extractApiPermissions(controllerList);
        }
        return API_PERMISSION_CACHE;
    }

    /**
     * 提取permission
     * @param controllerList
     */
    private static void extractApiPermissions(List<Object> controllerList){
        if(V.isEmpty(controllerList)) {
            return;
        }
        for (Object obj : controllerList) {
            Class controllerClass = BeanUtils.getTargetClass(obj);
            String title = null;
            // 提取类信息
            String codePrefix = null;
            // 注解
            BindPermission bindPermission = AnnotationUtils.findAnnotation(controllerClass, BindPermission.class);
            if(bindPermission != null){
                // 当前资源权限
                codePrefix = bindPermission.code();
                if(V.isEmpty(codePrefix)){
                    Class<?> entityClazz = BeanUtils.getGenericityClass(controllerClass, 0);
                    if(entityClazz != null){
                        codePrefix = entityClazz.getSimpleName();
                    }
                    else{
                        log.warn("无法获取{}相关的Entity，请指定注解BindPermission.code参数！", controllerClass.getName());
                    }
                }
                title = bindPermission.name();
            }
            else{
                title = S.substringBeforeLast(controllerClass.getSimpleName(), "Controller");
            }
            ApiPermissionWrapper wrapper = new ApiPermissionWrapper(controllerClass.getSimpleName(), title);
            buildApiPermissionsInClass(wrapper, controllerClass, codePrefix);
            if(V.notEmpty(wrapper.getChildren())){
                API_PERMISSION_CACHE.add(wrapper);
            }
        }
    }

    /**
     * 构建controller中的Api权限
     * @param wrapper
     * @param controllerClass
     * @param codePrefix
     */
    private static void buildApiPermissionsInClass(ApiPermissionWrapper wrapper, Class controllerClass, String codePrefix) {
        String urlPrefix = "";
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
        if(requestMapping != null){
            urlPrefix = AnnotationUtils.getNotEmptyStr(requestMapping.value(), requestMapping.path());
        }
        List<Method> annoMethods = AnnotationUtils.extractAnnotationMethods(controllerClass, BindPermission.class);
        if(V.notEmpty(annoMethods)){
            List<ApiPermission> apiPermissions = new ArrayList<>();
            for(Method method : annoMethods){
                // 忽略私有方法
                if(Modifier.isPrivate(method.getModifiers())){
                    continue;
                }
                // 处理BindPermission注解
                BindPermission bindPermission = AnnotationUtils.getAnnotation(method, BindPermission.class);
                String[] permissionCodes = getExtPermissionCodes(method);
                if(bindPermission == null && permissionCodes == null){
                    continue;
                }
                // 提取方法上的注解url
                String[] methodAndUrl = AnnotationUtils.extractRequestMethodAndMappingUrl(method);
                if(methodAndUrl[0] == null || methodAndUrl[1] == null){
                    continue;
                }
                if(bindPermission != null){
                    String permissionCode = (codePrefix != null)? codePrefix+":"+bindPermission.code() : bindPermission.code();
                    if (":".equals(permissionCode)) {
                        continue;
                    }
                    // 提取请求url-permission code的关系
                    buildApiPermission(apiPermissions, controllerClass, urlPrefix, wrapper.getClassTitle(), permissionCode, methodAndUrl, bindPermission.name());
                }
                // 处理RequirePermissions注解
                else if(V.notEmpty(permissionCodes)){
                    for(String permissionCode : permissionCodes){
                        // 提取请求url-permission code的关系
                        buildApiPermission(apiPermissions, controllerClass, urlPrefix, wrapper.getClassTitle(), permissionCode, methodAndUrl, null);
                    }
                }
            }
            // 添加至wrapper
            if(apiPermissions.size() > 0){
                wrapper.setChildren(apiPermissions);
            }
        }
    }

    /**
     * 构建ApiPermission
     * @param apiPermissions
     * @param controllerClass
     * @param urlPrefix
     * @param title
     * @param permissionCode
     * @param methodAndUrl
     * @param apiName
     */
    private static void buildApiPermission(List<ApiPermission> apiPermissions, Class controllerClass, String urlPrefix, String title,
                                    String permissionCode, String[] methodAndUrl, String apiName){
        String requestMethod = methodAndUrl[0], url = methodAndUrl[1];
        for(String m : requestMethod.split(Cons.SEPARATOR_COMMA)){
            for(String u : url.split(Cons.SEPARATOR_COMMA)){
                if(V.notEmpty(urlPrefix)){
                    for(String path : urlPrefix.split(Cons.SEPARATOR_COMMA)){
                        ApiPermission apiPermission = new ApiPermission().setClassName(controllerClass.getName()).setClassTitle(title);
                        apiPermission.setApiMethod(m).setApiName(apiName).setApiUri(path + u).setPermissionCode(permissionCode).setValue(m + ":" + path + u);
                        if(!UNIQUE_KEY_SET.contains(apiPermission.buildUniqueKey())){
                            apiPermissions.add(apiPermission);
                            UNIQUE_KEY_SET.add(apiPermission.buildUniqueKey());
                        }
                    }
                }
                else{
                    ApiPermission apiPermission = new ApiPermission().setClassName(controllerClass.getName()).setClassTitle(title);
                    apiPermission.setApiMethod(m).setApiName(apiName).setApiUri(u).setPermissionCode(permissionCode).setValue(m + ":" + u);
                    if(!UNIQUE_KEY_SET.contains(apiPermission.buildUniqueKey())){
                        apiPermissions.add(apiPermission);
                        UNIQUE_KEY_SET.add(apiPermission.buildUniqueKey());
                    }
                }
            }
        }
    }

    /**
     * 获取扩展的待检查的权限码
     * @param method
     * @return
     */
    private static String[] getExtPermissionCodes(Method method){
        IamCustomize iamCustomize = ContextHelper.getBean(IamCustomize.class);
        if(iamCustomize != null){
            return iamCustomize.getOrignPermissionCodes(method);
        }
        return null;
    }

}
