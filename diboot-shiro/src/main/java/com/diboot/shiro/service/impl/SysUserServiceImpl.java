package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.shiro.entity.*;
import com.diboot.shiro.enums.IUserType;
import com.diboot.shiro.exception.ShiroCustomException;
import com.diboot.shiro.mapper.SysUserMapper;
import com.diboot.shiro.service.PermissionService;
import com.diboot.shiro.service.RoleService;
import com.diboot.shiro.service.SysUserService;
import com.diboot.shiro.service.UserRoleService;
import com.diboot.shiro.util.AuthHelper;
import com.diboot.shiro.vo.RoleVO;
import com.diboot.shiro.vo.SysUserVO;
import com.sun.org.apache.bcel.internal.generic.IUSHR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Override
    public SysUserVO getSysUser(Long id) {
        SysUserVO sysUserVO = super.getViewObject(id, SysUserVO.class);
        if(V.notEmpty(sysUserVO) && V.notEmpty(sysUserVO.getRoleList())){
            List<Long> roleIdList = new ArrayList();
            sysUserVO.getRoleList()
                    .stream()
                    .forEach(role -> roleIdList.add(role.getId()));
            sysUserVO.setRoleIdList(roleIdList);
        }
        return sysUserVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSysUser(SysUser sysUser, IUserType iUserType) throws Exception{
        if (V.isEmpty(sysUser.getUsername()) || V.isEmpty(sysUser.getPassword())) {
            throw new ShiroCustomException(Status.FAIL_INVALID_PARAM, "用户名密码不能为空!");
        }
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, sysUser.getUsername())
                .eq(SysUser::getUserType, iUserType.getType());
        SysUser dbSysUser = super.getOne(wrapper);
        //校验数据库中数据是否已经存在
        if (V.notEmpty(dbSysUser)) {
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "用户已存在！");
        }
        //构建 + 创建账户信息
        sysUser = this.buildSysUser(sysUser, iUserType);
        boolean success = super.createEntity(sysUser);
        if(!success){
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "创建用户失败！");
        }
        //构建 + 创建（账户-角色）关系
        success = this.createUserRole(sysUser);
        if (!success) {
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "创建用户失败！");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSysUser(SysUser user, IUserType iUserType) throws Exception {
        // 对密码进行处理
        if (V.notEmpty(user.getPassword())){
            this.buildSysUser(user, iUserType);
        }
        //更新用户信息
        boolean success = super.updateEntity(user);
        if(!success){
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "更新用户失败！");
        }
        //组装需要删除 or 创建的（账户-角色）
        List<UserRole> createOrDeleteUserRoleList = new ArrayList<>();
        //构建用户角色关系
        this.buildCreateOrDeleteUserRoleList(user, iUserType, createOrDeleteUserRoleList);

        if (V.notEmpty(createOrDeleteUserRoleList)) {
            success = userRoleService.createOrUpdateOrDeleteEntities(createOrDeleteUserRoleList, createOrDeleteUserRoleList.size());
            if (!success) {
                throw new ShiroCustomException(Status.FAIL_VALIDATION, "更新用户失败！");
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSysUser(Long id, IUserType iUserType) throws Exception {
        //删除账户信息
        boolean success = super.deleteEntity(id);
        if(!success){
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "删除用户失败！");
        }
        //删除账户绑定的角色信息
        Map<String, Object> criteria = new HashMap(){{
            put("userId", id);
            put("userType", iUserType.getType());
        }};

        if (userRoleService.deletePhysics(criteria)) {
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "删除用户失败！");
        }
        return true;
    }

    /**
     * 获取登录的账号信息
     * @param account
     * @return
     */
    @Override
    public SysUser getLoginAccountInfo(TokenAccountInfo account) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, account.getAccount())
                .eq(SysUser::getUserType, account.getUserType());
        List<SysUser> userList = getEntityList(sysUserLambdaQueryWrapper);
        if (V.isEmpty(userList)){
            throw new ShiroCustomException(Status.FAIL_OPERATION, "获取数据失败!");
        }
        SysUser sysUser = userList.get(0);
        //获取账户绑定的角色权限
        List<RoleVO> roleVOList = roleService.getRelatedRoleAndPermissionListByUser(account.getUserType(), sysUser.getId());
        if (V.isEmpty(roleVOList)){
            throw new ShiroCustomException(Status.FAIL_OPERATION, "未配置角色，获取数据失败");
        }
        // 如果具有管理员角色，则赋予所有权限
        for (RoleVO roleVO : roleVOList){
            if (roleVO.isAdmin()){
                List<Permission> allPermissionList = permissionService.getEntityList(null);
                roleVO.setPermissionList(allPermissionList);
                break;
            }
        }
        sysUser.setRoleVOList(roleVOList);
        return sysUser;
    }

    @Override
    public Map<Long, SysUser> getSysUserListWithRolesAndPermissionsByUserIdList(List<Long> userIdList, IUserType iUserType) {
        //1、获取账户信息
        LambdaQueryWrapper<SysUser> sysUserQueryWrapper = Wrappers.<SysUser>lambdaQuery()
                .in(SysUser::getUserId, userIdList)
                .eq(SysUser::getUserType, iUserType.getType());
        List<SysUser> sysUserList = getEntityList(sysUserQueryWrapper);
        return buildSysUserAndRoleAndPermissionRelation(sysUserList);
    }

    /**
     * 组装账户 1-n 角色 1-n 权限关系
     * @param sysUserList
     * @return
     */
    private Map<Long, SysUser> buildSysUserAndRoleAndPermissionRelation(List<SysUser> sysUserList) {
        Map<Long, SysUser> userIdSysUserMap = new HashMap<>(32);
        List<Long> sysUserIdList = sysUserList.stream().map(SysUser::getId).collect(Collectors.toList());

        // 1.1 获取账户对应的角色信息
        List<RoleVO> roleVOList = roleService.getRoleByUserIdList(sysUserIdList);
        if (V.notEmpty(roleVOList)) {
            List<Long> roleIdList = roleVOList.stream().map(roleVO -> roleVO.getId()).collect(Collectors.toList());
            //1.2 获取角色对应的权限
            List<Permission> permissionList =  permissionService.getPermissionListByRoleIdList(roleIdList);
            //1.3 构建角色和权限的关系
            Map<Long, List<Permission>> roleIdPermissionListMap = new HashMap<>(128);
            if (V.notEmpty(permissionList)) {
                permissionList.stream().forEach(permission -> {
                    List<Permission> roleIdPermissionList = roleIdPermissionListMap.get(permission.getRoleId());
                    if (V.notEmpty(roleIdPermissionList)) {
                        roleIdPermissionList = new ArrayList<>();
                    }
                    roleIdPermissionList.add(permission);
                    roleIdPermissionListMap.put(permission.getRoleId(), roleIdPermissionList);
                });
                //1.4 绑定角色和权限，构建账户和角色关系
                Map<Long, List<RoleVO>> userIdRoleVoListMap = new HashMap<>(16);
                roleVOList.stream().forEach(roleVO -> {
                    //1.4.1 绑定角色和权限关系
                    roleVO.setPermissionList(roleIdPermissionListMap.get(roleVO.getId()));
                    //1.4.2 构建角色和账户关系
                    List<RoleVO> userIdRoleVoList = userIdRoleVoListMap.get(roleVO.getUserId());
                    if (V.notEmpty(userIdRoleVoList)) {
                        userIdRoleVoList = new ArrayList<>();
                    }
                    userIdRoleVoList.add(roleVO);
                    userIdRoleVoListMap.put(roleVO.getUserId(), userIdRoleVoList);
                });
                //1.5 绑定账户和角色关系， 构建用户和账户关系
                sysUserList.stream().forEach(sysUser -> {
                    //1.5.1 绑定账户和角色关系
                    sysUser.setRoleVOList(userIdRoleVoListMap.get(sysUser.getId()));
                    //1.5.2 构建用户和账户关系
                    userIdSysUserMap.put(sysUser.getUserId(), sysUser);
                });
            }
        }
        return userIdSysUserMap;
    }

    /**
     * 创建账户角色关联关系
     * @param sysUser
     * @return
     */
    private boolean createUserRole(SysUser sysUser) {
        List<UserRole> urList = new ArrayList<>();
        if(V.notEmpty(sysUser.getRoleList())){
            sysUser.getRoleList().stream()
                    .forEach(role -> {
                        UserRole userRole = new UserRole();
                        userRole.setRoleId(role.getId());
                        userRole.setUserId(sysUser.getId());
                        userRole.setUserType(sysUser.getUserType());
                        urList.add(userRole);
                    });
        }
        return userRoleService.createEntities(urList);
    }

    /**
     * 构建需要创建或者删除的（账户-角色）集合
     * @param user
     * @param iUserType
     * @param createOrDeleteUserRoleList
     */
    private void buildCreateOrDeleteUserRoleList(SysUser user, IUserType iUserType, List<UserRole> createOrDeleteUserRoleList) {
        //获取数据库中用户角色信息
        LambdaQueryWrapper<UserRole> queryWrapper = Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserType, iUserType.getType())
                .eq(UserRole::getUserId, user.getId());
        List<UserRole> dbUserRoleList = userRoleService.getEntityList(queryWrapper);

        //组装新的角色信息
        StringBuffer newRoleIdBuffer = new StringBuffer("_");
        if(V.notEmpty(user.getRoleList())){
            user.getRoleList()
                    .stream()
                    .forEach(role -> newRoleIdBuffer.append(role.getId()).append("_"));
        }

        //筛选出需要被删除的（账户-角色）
        StringBuffer dbRoleIdBuffer = new StringBuffer("_");
        if(V.notEmpty(dbUserRoleList)){
            dbUserRoleList.stream()
                    .forEach(userRole -> {
                        dbRoleIdBuffer.append(userRole.getRoleId()).append("_");
                        if (!newRoleIdBuffer.toString().contains(S.join("_", userRole.getRoleId(), "_"))) {
                            userRole.setDeleted(true);
                            createOrDeleteUserRoleList.add(userRole);
                        }
                    });
        }

        //对比数据库 筛选 + 构建 页面提交需要添加的角色信息
        if(V.notEmpty(user.getRoleList())){
            user.getRoleList()
                    .stream()
                    .forEach(role -> {
                        if (!dbRoleIdBuffer.toString().contains(S.join("_", role.getId(), "_"))) {
                            UserRole entity = new UserRole();
                            entity.setRoleId(role.getId());
                            entity.setUserId(user.getId());
                            entity.setUserType(iUserType.getType());
                            createOrDeleteUserRoleList.add(entity);
                        }
                    });
        }
    }

    /***
     * 设置加密密码相关的数据
     * @param sysUser
     */
    private SysUser buildSysUser(SysUser sysUser, IUserType userType) {
        String salt = AuthHelper.createSalt();
        String password = AuthHelper.encryptMD5(sysUser.getPassword(), salt, true);
        sysUser.setSalt(salt);
        sysUser.setDepartmentId(0L);
        sysUser.setUserType(userType.getType());
        sysUser.setPassword(password);
        return sysUser;
    }
}
