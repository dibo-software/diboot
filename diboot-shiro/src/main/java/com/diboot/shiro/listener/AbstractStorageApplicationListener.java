package com.diboot.shiro.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import com.diboot.shiro.authz.storage.EnableStorageEnum;
import com.diboot.shiro.authz.storage.PermissionStorage;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.service.impl.PermissionServiceImpl;
import com.diboot.shiro.util.ProxyToTargetObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link AbstractStorageApplicationListener}实现{@link ApplicationListener}接口,
 * <br/>
 * 并重新对外提供抽象{@link AbstractStorageApplicationListener#customExecute}，功能等同于{@link ApplicationListener#onApplicationEvent};
 * <br/>
 * {@link AbstractStorageApplicationListener}中封装了将{@link com.diboot.shiro.authz.annotation.AuthorizationWrapper}权限自动入库的操作,
 * <strong>注：权限入库每个Controller需要加上类注解{@link AuthorizationPrefix}用于识别</strong>
 * <br/>
 * 当你使用注解{@link com.diboot.shiro.authz.annotation.AuthorizationWrapper}，建议直接继承{@link AbstractStorageApplicationListener},
 * <br/>
 * 且你的实现类需要手动设置一个默认构造函数来设置{@link AbstractStorageApplicationListener#storagePermissions}，传递是否自动权限入库
 *
 * @author : wee
 * @version : v 2.0
 * @Date 2019-06-18  23:12
 */
@Slf4j
public abstract class AbstractStorageApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    /**存储数据库中已经存在的permissionCode和ID的关系，主要用于更新数据*/
    private static Map<String, Permission> dbPermissionMap = new HashMap<>();

    /**代码中的所有权限数据 key: {@link PermissionStorage#getPermissionCode()} value: {@link Permission}*/
    private static Map<String, PermissionStorage> loadCodePermissionMap = new ConcurrentHashMap<>();

    private static List<Permission> updateOrCreateOrDeletePermissionList = new ArrayList<>();
    /**
     * 默认开启存储
     */
    protected boolean storagePermissions;

    protected AbstractStorageApplicationListener(EnableStorageEnum enableStorageEnum) {
        this.storagePermissions = enableStorageEnum.isStoragePermissions();
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //防止重复执行
        if (V.isEmpty(event.getApplicationContext().getParent())) {
            /*自定义处理内容*/
            customExecute(event);
            /*判断客户是否开启存储权限，如果开启那么执行存储权限*/
            if (storagePermissions) {
                storagePermissions(event);
            }
        }
    }

    /**
     * 系统启动后，客户自定义事件
     *
     * @param event
     */
    protected abstract void customExecute(ContextRefreshedEvent event);

    /**
     * 执行完自动权限后，自动赋值
     *
     * @param event
     */
    private void storagePermissions(ContextRefreshedEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();

            if (V.notEmpty(applicationContext)) {
                PermissionServiceImpl permissionService = applicationContext.getBean(PermissionServiceImpl.class);

                //获取当前数据库中的有效的所有权限
                LambdaQueryWrapper<Permission> permissionLambdaQueryWrapper = Wrappers.lambdaQuery();
                permissionLambdaQueryWrapper.eq(Permission::isDeleted, false);
                List<Permission> permissionList = permissionService.getEntityList(permissionLambdaQueryWrapper);
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
        PermissionStorage permissionStorage;
        for (int i = 0; i < value.length; i++) {
            //如果权限名称和值无法一一对应，那么当前权限组的所有数据的名称全部设置为最后一个name值
            permissionName = value.length != name.length ? name[name.length - 1] : name[i];
            //拼接权限值
            permissionCode = V.notEmpty(prefix) ? S.join(prefix, ":", value[i]) : value[i];
            PermissionStorage existPermission = loadCodePermissionMap.get(permissionCode);
            //如果不存在权限构建；当前存在的权限不是是高优先级的时候替换
            if (V.isEmpty(existPermission) || !existPermission.isHighPriority()) {
                //组装需要存储的权限
                permissionStorage = PermissionStorage.builder()
                        .menuId(3L).menuCode(menuCode).menuName(menuName)
                        .permissionCode(permissionCode).permissionName(permissionName)
                        .deleted(false).highPriority(highPriority).build();
                //设置缓存
                loadCodePermissionMap.put(permissionCode, permissionStorage);
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
    private void saveOrUpdateOrDeletePermission(PermissionServiceImpl permissionService) {
        //记录修改和删除的权限数量
        int modifyCount = 0, removeCount = 0, totalCount = 0;
        List<Permission> saveOrUpdateOrDeletePermissionList = new ArrayList<>();
        //设置删除 or 修改
        for (Map.Entry<String, Permission> entry : dbPermissionMap.entrySet()) {
            PermissionStorage permissionStorage = loadCodePermissionMap.get(entry.getKey());
            Permission permission = entry.getValue();
            //存在则更新（设置ID）
            if (V.notEmpty(permissionStorage)) {
                if (isNeedModify(permission, permissionStorage)) {
                    modifyCount++;
                    permissionStorage.setId(permission.getId());
                    //重置
                    loadCodePermissionMap.put(entry.getKey(), permissionStorage);
                } else {
                    //数据库中不需要修改的，在加载的数据中删除
                    loadCodePermissionMap.remove(entry.getKey());
                }
            } else {
                //不存在: 表示需要删除
                removeCount++;
                permission.setDeleted(true);
                saveOrUpdateOrDeletePermissionList.add(permission);
            }

        }
        //需要操作的数据=》转化为List<Permission>
        List<Permission> saveOrUpdatePermissionList;
        if (V.notEmpty(loadCodePermissionMap)) {
            totalCount = loadCodePermissionMap.values().size();
            List<PermissionStorage> permissionStorageList = loadCodePermissionMap.values().stream().collect(Collectors.toList());
            saveOrUpdatePermissionList = BeanUtils.convertList(permissionStorageList, Permission.class);
            saveOrUpdateOrDeletePermissionList.addAll(saveOrUpdatePermissionList);
        }
        //保存、更新、删除 权限
        boolean success = permissionService.saveOrUpdateBatch(saveOrUpdateOrDeletePermissionList);
        if (success) {
            log.debug("【初始化权限】<== 成功!共计权限{}个 新增【{}】个, 修改【{}】个,删除【{}】个！",
                    totalCount, (saveOrUpdateOrDeletePermissionList.size() - modifyCount - removeCount), modifyCount, removeCount);
        } else {
            log.debug("【初始化权限】<== 失败!");
        }
    }

    /**
     * 比较两个对象的属性是否相同，如果存在一处不同那么就需要修改
     * @param permission
     * @param permissionStorage
     * @return
     */
    public boolean isNeedModify(Permission permission, PermissionStorage permissionStorage){
        if (!V.equals(permission.getMenuId(), permissionStorage.getMenuId())
            || !V.equals(permission.getMenuCode(), permissionStorage.getMenuCode())
            || !V.equals(permission.getMenuName(), permissionStorage.getMenuName())
            || !V.equals(permission.getPermissionCode(), permissionStorage.getPermissionCode())
            || !V.equals(permission.getPermissionName(), permissionStorage.getPermissionName())
        ) {
            return true;
        }
        return false;
    }

}
