package com.diboot.iam.annotation.process;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.config.Cons;
import com.diboot.iam.util.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.aop.support.AopUtils;
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
     * 提取所有的权限定义
     * @return
     */
    public static List<ApiPermissionWrapper> extractAllApiPermissions(){
        List<ApiPermissionWrapper> apiPermissionWrappers = new ArrayList<>();
        // 提取rest controller
        List<Object> controllerList = ContextHelper.getBeansByAnnotation(RestController.class);
        extractApiPermissions(controllerList, apiPermissionWrappers);
        // 提取controller
        controllerList = ContextHelper.getBeansByAnnotation(Controller.class);
        extractApiPermissions(controllerList, apiPermissionWrappers);
        // 缓存抓取结果
        return apiPermissionWrappers;
    }

    /**
     * 提取permission
     * @param controllerList
     * @param apiPermissionWrappers
     */
    private static void extractApiPermissions(List<Object> controllerList, List<ApiPermissionWrapper> apiPermissionWrappers){
        if(V.notEmpty(controllerList)) {
            for (Object obj : controllerList) {
                Class controllerClass = AopUtils.getTargetClass(obj);
                String title = null;
                // 提取类信息
                String codePrefix = null;
                // 注解
                BindPermission bindPermission = AnnotationUtils.findAnnotation(controllerClass, BindPermission.class);
                if(bindPermission != null){
                    // 当前菜单权限
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
                ApiPermissionWrapper wrapper = new ApiPermissionWrapper(title);
                buildApiPermissionsInClass(wrapper, controllerClass, codePrefix);
                if(V.notEmpty(wrapper.getChildren())){
                    apiPermissionWrappers.add(wrapper);
                }
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
            Set<String> existKey = new HashSet<>();
            for(Method method : annoMethods){
                // 忽略私有方法
                if(Modifier.isPrivate(method.getModifiers())){
                    continue;
                }
                // 处理BindPermission注解
                BindPermission bindPermission = AnnotationUtils.getAnnotation(method, BindPermission.class);
                RequiresPermissions requiresPermissions = AnnotationUtils.getAnnotation(method, RequiresPermissions.class);
                if(bindPermission == null && requiresPermissions == null){
                    continue;
                }
                // 提取方法上的注解url
                String[] methodAndUrl = AnnotationUtils.extractRequestMethodAndMappingUrl(method);
                if(methodAndUrl[0] == null){
                    continue;
                }
                if(bindPermission != null){
                    String permissionCode = (codePrefix != null)? codePrefix+":"+bindPermission.code() : bindPermission.code();
                    // 提取请求url-permission code的关系
                    buildApiPermission(apiPermissions, controllerClass, urlPrefix, wrapper.getClassTitle(), permissionCode, methodAndUrl, bindPermission.name(), existKey);
                }
                // 处理RequirePermissions注解
                else if(requiresPermissions != null){
                    String[] permissionCodes = requiresPermissions.value();
                    for(String permissionCode : permissionCodes){
                        // 提取请求url-permission code的关系
                        buildApiPermission(apiPermissions, controllerClass, urlPrefix, wrapper.getClassTitle(), permissionCode, methodAndUrl, null, existKey);
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
                                    String permissionCode, String[] methodAndUrl, String apiName, Set<String> existKey){
        String requestMethod = methodAndUrl[0], url = methodAndUrl[1];
        for(String m : requestMethod.split(Cons.SEPARATOR_COMMA)){
            for(String u : url.split(Cons.SEPARATOR_COMMA)){
                if(V.notEmpty(urlPrefix)){
                    for(String path : urlPrefix.split(Cons.SEPARATOR_COMMA)){
                        ApiPermission apiPermission = new ApiPermission().setClassName(controllerClass.getName()).setClassTitle(title);
                        apiPermission.setApiMethod(m).setApiName(apiName).setApiUri(path + u).setPermissionCode(permissionCode).setValue(m + ":" + path + u);
                        if(!existKey.contains(apiPermission.buildUniqueKey())){
                            apiPermissions.add(apiPermission);
                            existKey.add(apiPermission.buildUniqueKey());
                        }
                    }
                }
                else{
                    ApiPermission apiPermission = new ApiPermission().setClassName(controllerClass.getName()).setClassTitle(title);
                    apiPermission.setApiMethod(m).setApiName(apiName).setApiUri(u).setPermissionCode(permissionCode);
                    if(!existKey.contains(apiPermission.buildUniqueKey())){
                        apiPermissions.add(apiPermission);
                        existKey.add(apiPermission.buildUniqueKey());
                    }
                }
            }
        }
    }

}
