/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.core.vo.ApiUri;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.cache.IamCacheManager;
import com.diboot.iam.config.Cons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 权限码注解提取器
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/21
 * Copyright © diboot.com
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
            if(UNIQUE_KEY_SET.contains(controllerClass.getName())){
               continue;
            }
            UNIQUE_KEY_SET.add(controllerClass.getName());
            ApiPermissionWrapper wrapper = IamCacheManager.getPermissionCodeWrapper(controllerClass);
            buildApiPermissionsInClass(wrapper, controllerClass);
            if(wrapper.notEmpty()){
                API_PERMISSION_CACHE.add(wrapper);
            }
        }
    }

    /**
     * 构建controller中的Api权限
     * @param wrapper
     * @param controllerClass
     */
    private static void buildApiPermissionsInClass(ApiPermissionWrapper wrapper, Class controllerClass) {
        List<Method> annoMethods = AnnotationUtils.extractAnnotationMethods(controllerClass, BindPermission.class);
        if(V.isEmpty(annoMethods)){
            return;
        }
        String urlPrefix = "";
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
        if(requestMapping != null){
            urlPrefix = AnnotationUtils.getNotEmptyStr(requestMapping.value(), requestMapping.path());
        }
        List<ApiPermission> apiPermissions = new ArrayList<>();
        Map<String, ApiPermission> tempCode2ObjMap = new HashMap<>();
        for(Method method : annoMethods){
            // 忽略私有方法
            if(Modifier.isPrivate(method.getModifiers())){
                continue;
            }
            // 处理BindPermission注解
            BindPermission bindPermission = AnnotationUtils.getAnnotation(method, BindPermission.class);
            if(bindPermission == null){
                continue;
            }
            // 提取方法上的注解url
            ApiUri apiUriCombo = AnnotationUtils.extractRequestMethodAndMappingUrl(method);
            if(apiUriCombo.isEmpty()){
                continue;
            }
            if(bindPermission != null){
                if(V.isEmpty(bindPermission.code())){
                    log.warn("忽略无效的权限配置(未指定code): {}.{}", controllerClass.getSimpleName(), method.getName());
                    continue;
                }
                String name = bindPermission.name();
                String code = (wrapper.getCode() != null)? wrapper.getCode()+":"+bindPermission.code() : bindPermission.code();
                ApiPermission apiPermission = tempCode2ObjMap.get(code);
                if(apiPermission == null){
                    apiPermission = new ApiPermission(code);
                    tempCode2ObjMap.put(code, apiPermission);
                    apiPermissions.add(apiPermission);
                }
                apiUriCombo.setLabel(name);
                // 提取请求url-permission code的关系
                buildApiPermission(apiPermission, urlPrefix, apiUriCombo);
            }
        }
        // 添加至wrapper
        if(apiPermissions.size() > 0){
            wrapper.setApiPermissionList(apiPermissions);
        }
    }

    /**
     * 构建ApiPermission
     * @param apiPermission
     * @param urlPrefix
     * @param apiUriCombo
     */
    private static void buildApiPermission(ApiPermission apiPermission, String urlPrefix, ApiUri apiUriCombo){
        String requestMethod = apiUriCombo.getMethod(), url = apiUriCombo.getUri();
        List<ApiUri> apiUriList = apiPermission.getApiUriList();
        for(String m : requestMethod.split(Cons.SEPARATOR_COMMA)){
            for(String u : url.split(Cons.SEPARATOR_COMMA)){
                String uri = u;
                if(V.notEmpty(urlPrefix)){
                    uri = urlPrefix + u;
                }
                ApiUri apiUri = new ApiUri(m, uri, apiUriCombo.getLabel());
                apiUriList.add(apiUri);
            }
        }
    }

}
