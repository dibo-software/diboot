package com.diboot.shiro.authz.storage;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import com.diboot.shiro.authz.properties.AuthorizationProperties;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.service.PermissionService;
import com.diboot.shiro.service.impl.PermissionServiceImpl;
import com.diboot.shiro.util.ProxyToTargetObjectHelper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link AuthorizationStorage}中封装了将{@link com.diboot.shiro.authz.annotation.AuthorizationWrapper}权限自动入库的操作,
 * <strong>注：权限入库每个Controller需要加上类注解{@link AuthorizationPrefix}用于识别</strong>
 * <br/>
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-27  10:01
 */
@Slf4j
public class AuthorizationStorage {

    private String env;

    private boolean storage;

    public AuthorizationStorage(String env, boolean storage) {
        this.env = env;
        this.storage = storage;
    }

    /**存储数据库中已经存在的permissionCode和ID的关系，主要用于更新数据*/
    private static Map<String, Permission> dbPermissionMap = new HashMap<>();

    /**代码中的所有权限数据 key: {@link PermissionStorageEntity#getPermissionCode()} value: {@link Permission}*/
    private static Map<String, PermissionStorageEntity> loadCodePermissionMap = new ConcurrentHashMap<>();

    /**
     * 系统启动后存储权限
     * @param applicationContext
     */
    public void autoStorage(ApplicationContext applicationContext) {
        if (!storage) {
            log.debug("【初始化权限】<==未配置自动存储权限");
            return;
        }
        try {
            if (V.notEmpty(applicationContext)) {
                PermissionService permissionService = applicationContext.getBean(PermissionServiceImpl.class);
                //获取当前数据库中的有效的所有权限
                List<Permission> permissionList = permissionService.getEntityList(Wrappers.emptyWrapper());
                //存储数据库值
                for (Permission permission : permissionList) {
                    dbPermissionMap.put(permission.getPermissionCode(), permission);
                }
                //初始化数据：获取所有注解为AuthPrefix的代理bean<bean名称，bean的代理对象>
                Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(AuthorizationPrefix.class);
                if (V.isEmpty(beansWithAnnotation)) {
                    return;
                }
                for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
                    //获取代理对象的目标对象（代理对象无法获取类上注解，所以需要转化成目标对象）
                    Object target = ProxyToTargetObjectHelper.getTarget(entry.getValue());
                    Class<?> targetClass = target.getClass();
                    //获取类注解上的相关描述
                    AuthorizationPrefix authorizationPrefix = targetClass.getAnnotation(AuthorizationPrefix.class);
                    //判断Controller类上是否包含认证注解的包装
                    if (targetClass.isAnnotationPresent(AuthorizationWrapper.class)) {
                        buildPermissionStorageToMap(authorizationPrefix, targetClass.getAnnotation(AuthorizationWrapper.class), false);
                    }
                    //获取类中所有方法
                    Method[] methods = target.getClass().getMethods();
                    for (Method method : methods) {
                        //取出含有注解的方法
                        if (method.isAnnotationPresent(AuthorizationWrapper.class)) {
                            buildPermissionStorageToMap(authorizationPrefix, method.getAnnotation(AuthorizationWrapper.class), true);
                        }
                    }
                }
                //保存、更新、删除 权限
                saveOrUpdateOrDeletePermission(permissionService);
            }
        } catch (Exception e) {
            log.error("【初始化权限】<== 异常：", e);
        }
    }

    /**
     * 构建待存储的权限到map中
     * @param authorizationPrefix  {@link AuthorizationPrefix}
     * @param authorizationWrapper {@link AuthorizationWrapper}
     */
    private void buildPermissionStorageToMap(AuthorizationPrefix authorizationPrefix, AuthorizationWrapper authorizationWrapper, boolean highPriority) {
        String menuCode = authorizationPrefix.code();
        String menuName = authorizationPrefix.name();
        //如果不忽略前缀， 那么进行前缀补充 (如果允许，优先使用 AuthorizationWrapper注解的前缀)
        String prefix = "";
        if (!authorizationWrapper.ignorePrefix()) {
            prefix = V.notEmpty(authorizationWrapper.prefix()) ? authorizationWrapper.prefix() : authorizationPrefix.prefix();
        }
        //获取权限
        RequiresPermissions requiresPermissions = authorizationWrapper.value();
        //获取所有描述的权限code和name
        String[] value = requiresPermissions.value();
        String[] name = authorizationWrapper.name();
        //设置单个权限code和name
        String permissionName = "";
        String permissionCode = "";
        PermissionStorageEntity permissionStorageEntity;
        for (int i = 0; i < value.length; i++) {
            //如果权限名称和值无法一一对应，那么当前权限组的所有数据的名称全部设置为最后一个name值
            permissionName = value.length != name.length ? name[name.length - 1] : name[i];
            //拼接权限值
            permissionCode = V.notEmpty(prefix) ? S.join(prefix, ":", value[i]) : value[i];
            PermissionStorageEntity existPermission = loadCodePermissionMap.get(permissionCode);
            //如果不存在权限构建；当前存在的权限不是是高优先级的时候替换
            if (V.isEmpty(existPermission) || !existPermission.isHighPriority()) {
                //组装需要存储的权限
                permissionStorageEntity = PermissionStorageEntity.builder()
                        .menuCode(menuCode).menuName(menuName)
                        .permissionCode(permissionCode).permissionName(permissionName)
                        .deleted(false).highPriority(highPriority).build();
                //设置缓存
                loadCodePermissionMap.put(permissionCode, permissionStorageEntity);
            }
        }
    }

    /**
     * <h3>保存、更新、删除 权限</h3>
     * <p>
     *  操作原则 以数据库字段为基准 匹配数据库中的权限和代码中的权限：<br>
     *          * 如果代码中和数据库中相同，更新数据库中数据；<br>
     *          * 如果代码中不存在，删除数据库中数据；<br>
     * </p>
     * @param permissionService
     */
    private void saveOrUpdateOrDeletePermission(PermissionService permissionService) {
        //记录修改和删除的权限数量
        int modifyCount = 0, removeCount = 0, totalCount = loadCodePermissionMap.values().size();;
        List<Permission> saveOrUpdateOrDeletePermissionList = new ArrayList<>();
        //设置删除 or 修改
        for (Map.Entry<String, Permission> entry : dbPermissionMap.entrySet()) {
            PermissionStorageEntity permissionStorageEntity = loadCodePermissionMap.get(entry.getKey());
            Permission permission = entry.getValue();
            //代码中存在则更新（设置ID）
            if (V.notEmpty(permissionStorageEntity)) {
                if (isNeedModify(permission, permissionStorageEntity)) {
                    modifyCount++;
                    permissionStorageEntity.setId(permission.getId());
                    //重置
                    loadCodePermissionMap.put(entry.getKey(), permissionStorageEntity);
                } else {
                    //数据库中不需要修改的，在加载的数据中删除
                    loadCodePermissionMap.remove(entry.getKey());
                }
            } else {
                //代码中不存在且生产环境/测试环境: 表示需要删除
                if (AuthorizationProperties.EnvEnum.PROD.getEnv().equals(this.env) ||
                        AuthorizationProperties.EnvEnum.TEST.getEnv().equals(this.env)) {
                    removeCount++;
                    permission.setDeleted(true);
                    saveOrUpdateOrDeletePermissionList.add(permission);
                } else {
                    log.debug("【初始化权限】<== 当前启动环境为：【{}】, 不删除系统中不存在的权限！" , env);
                }
            }

        }
        //需要操作的数据=》转化为List<Permission>
        List<Permission> saveOrUpdatePermissionList = new ArrayList<>();
        if (V.notEmpty(loadCodePermissionMap)) {
            List<PermissionStorageEntity> permissionStorageEntityList = loadCodePermissionMap.values().stream().collect(Collectors.toList());
            saveOrUpdatePermissionList = BeanUtils.convertList(permissionStorageEntityList, Permission.class);
            saveOrUpdateOrDeletePermissionList.addAll(saveOrUpdatePermissionList);
        }
        log.debug("当前系统权限共计【{}】个 已自动 新增【{}】个, 修改【{}】个, 删除【{}】个！",
                totalCount, (saveOrUpdateOrDeletePermissionList.size() - modifyCount - removeCount), modifyCount, removeCount);
        if (V.notEmpty(saveOrUpdateOrDeletePermissionList)) {
            int loopCount = (int) Math.ceil(saveOrUpdateOrDeletePermissionList.size() * 1.0 / BaseConfig.getBatchSize());
            //截取数据
            List<Permission> permissionList;
            int subEndIndex;
            for (int i = 0; i < loopCount; i++) {
                //截取的开始下标
                int subStartIndex = i * BaseConfig.getBatchSize();
                //截取的结束下标，如果是最后一次需要判断剩余数量是否小于超过配置的可执行数量，如果小于，下标就用剩余量
                if ((i + 1) == loopCount) {
                    subEndIndex = saveOrUpdateOrDeletePermissionList.size();
                } else {
                    subEndIndex = subStartIndex + BaseConfig.getBatchSize();
                }
                //截取
                permissionList = saveOrUpdateOrDeletePermissionList.subList(subStartIndex, subEndIndex);
                //保存、更新、删除 权限
                boolean success = permissionService.createOrUpdateOrDeleteEntities(permissionList, BaseConfig.getBatchSize());
                if (success) {
                    log.debug("【初始化权限】<== 共【{}】批次，第【{}】批次成功，调整【{}】个权限！", loopCount, i + 1, permissionList.size());
                } else {
                    log.debug("【初始化权限】<== 共【{}】批次，第【{}】批次失败，调整【{}】个权限！", loopCount, i + 1, permissionList.size());
                }
            }
        }
    }

    /**
     * 比较两个对象的属性是否相同，如果存在一处不同那么就需要修改
     * @param permission
     * @param permissionStorageEntity
     * @return
     */
    private boolean isNeedModify(Permission permission, PermissionStorageEntity permissionStorageEntity){
        if (!V.equals(permission.getMenuCode(), permissionStorageEntity.getMenuCode())
                || !V.equals(permission.getMenuName(), permissionStorageEntity.getMenuName())
                || !V.equals(permission.getPermissionCode(), permissionStorageEntity.getPermissionCode())
                || !V.equals(permission.getPermissionName(), permissionStorageEntity.getPermissionName())
        ) {
            return true;
        }
        return false;
    }

    /**
     * 权限临时存储类
     * @author wee
     * @version v2.0
     * @date 2019/6/6
     */
    @Data
    @Builder
    private static class PermissionStorageEntity implements Serializable {

        private static final long serialVersionUID = 147840093814049689L;

        /***
         * 默认逻辑删除标记，deleted=0有效
         */
        private boolean deleted = false;

        /**菜单编码*/
        private String menuCode;

        /**菜单名称*/
        private String menuName;

        /**权限编码*/
        private String permissionCode;

        /**权限名称*/
        private String permissionName;

        /**是否高优先级：方法上的优先级高于类上，同时出现以方法为准*/
        private boolean highPriority;

        private Long id;
    }
}
