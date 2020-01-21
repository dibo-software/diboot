package com.diboot.iam.annotation.process;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.config.Cons;
import com.diboot.iam.service.IamPermissionService;
import com.diboot.iam.util.AnnotationUtils;
import com.diboot.iam.vo.PermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 注解提取器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Slf4j
@Async
@Component
public class AnnotationExtractor {

    @Autowired
    private IamPermissionService iamPermissionService;

    /**
     * 更新permissions
     */
    public void updatePermissions(boolean isEnablePermissionUpdate){
        // 提取到的全部注解（必须先执行该初始化，确保url和permissionCode被加载）
        List<PermissionVO> voListInCode = extractAllPermissions();
        if(!isEnablePermissionUpdate){
            return;
        }
        log.info("diboot-iam 开始异步更新权限 ...");
        // 查询数据库中的所有权限
        List<PermissionVO> voListInDb = iamPermissionService.getAllPermissionsTree(Cons.APPLICATION);
        // 需要新增/修改的权限
        if(V.notEmpty(voListInCode)){
            if(V.notEmpty(voListInDb)){// 修改
                Map<String, PermissionVO> key2ObjMap = new HashMap<>();
                for(PermissionVO oldVo : voListInDb){
                    key2ObjMap.put(oldVo.buildExistKey(), oldVo);
                }
                // 基于新的权限-新增/更新
                for(PermissionVO newVo : voListInCode){
                    PermissionVO oldVo = key2ObjMap.get(newVo.buildExistKey());
                    iamPermissionService.updatePermissionAndChildren(oldVo, newVo);
                }
                key2ObjMap.clear();
                for(PermissionVO newVo : voListInCode){
                    key2ObjMap.put(newVo.buildExistKey(), newVo);
                }
                // 基于老的权限删除
                for(PermissionVO oldVo : voListInDb){
                    PermissionVO newVo = key2ObjMap.get(oldVo.buildExistKey());
                    if(newVo == null){ //非空的情况已经在上面处理过
                        iamPermissionService.updatePermissionAndChildren(oldVo, null);
                    }
                }
            }// 新增
            else{
                for(PermissionVO newVo : voListInCode){
                    iamPermissionService.updatePermissionAndChildren(null, newVo);
                }
            }
        }
        // 需要删除的权限
        else if(V.notEmpty(voListInDb)){
            for(PermissionVO oldVo : voListInDb){
                iamPermissionService.updatePermissionAndChildren(oldVo, null);
            }
        }
        log.info("diboot-iam 权限更新完成.");
    }

    /**
     * 提取所有的权限定义
     * @return
     */
    public List<PermissionVO> extractAllPermissions(){
        List<Object> controllerList = ContextHelper.getBeansByAnnotation(RestController.class);
        if(V.notEmpty(controllerList)) {
            List<PermissionVO> permissionList = new ArrayList<>();
            for (Object obj : controllerList) {
                Class controllerClass = AopUtils.getTargetClass(obj);
                BindPermission bindPermission = AnnotationUtils.findAnnotation(controllerClass, BindPermission.class);
                if(bindPermission == null){
                    continue;
                }
                // 当前菜单权限
                PermissionVO permission = buildPermission(controllerClass, bindPermission);
                // 子操作定义
                PermissionVO baseClone = BeanUtils.cloneBean(permission);
                baseClone.setParentId(null);
                baseClone.setType(Cons.DICTCODE_PERMISSION_TYPE.OPERATION.name());
                List<PermissionVO> children = buildOperationsList(controllerClass, baseClone);
                permission.setChildren(children);
                // 添加到对象中
                permissionList.add(permission);
            }
            return permissionList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 构建注解
     * @param controllerClass
     * @return
     */
    private static PermissionVO buildPermission(Class<?> controllerClass, BindPermission bindPermission){
        PermissionVO permission = new PermissionVO(Cons.APPLICATION, Cons.DICTCODE_PERMISSION_TYPE.MENU.name());
        String code = bindPermission.code();
        if(V.isEmpty(code)){
            Class<?> entityClazz = BeanUtils.getGenericityClass(controllerClass, 0);
            if(entityClazz != null){
                code = entityClazz.getSimpleName();
            }
            else{
                log.warn("无法获取{}相关的Entity，请指定注解BindPermission.code参数！", controllerClass.getName());
            }
        }
        permission.setName(bindPermission.name()).setCode(code).setSortId(bindPermission.sortId());
        return permission;
    }

    /**
     * 构建操作权限
     * @return
     */
    private static List<PermissionVO> buildOperationsList(Class<?> controllerClass, PermissionVO clonePermission){
        String urlPrefix = "";
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
        if(requestMapping != null){
            urlPrefix = AnnotationUtils.getNotEmptyStr(requestMapping.value(), requestMapping.path());
        }
        List<Method> annoMethods = AnnotationUtils.extractAnnotationMethods(controllerClass, BindPermission.class);
        if(V.notEmpty(annoMethods)){
            List<PermissionVO> permissions = new ArrayList<>();
            for(Method method : annoMethods){
                PermissionVO child = BeanUtils.cloneBean(clonePermission);
                BindPermission bindPermission = AnnotationUtils.getAnnotation(method, BindPermission.class);
                String permissionCode = clonePermission.getCode()+":"+bindPermission.code();
                child.setOperationName(bindPermission.name()).setOperationCode(permissionCode).setSortId(bindPermission.sortId());
                permissions.add(child);
                // 提取请求url-permission code的关系
                AnnotationUtils.extractAndCacheUrl2PermissionCode(method, urlPrefix, permissionCode);
            };
            return permissions;
        }
        return Collections.EMPTY_LIST;
    }

}
