package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.shiro.authz.config.SystemParamConfig;
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
    private SystemParamConfig systemParamConfig;

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
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, user.getUsername())
                .eq(SysUser::getUserType, iUserType.getType());
        SysUser dbSysUser = super.getOne(wrapper);
        user.setId(dbSysUser.getId());
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
    public boolean deleteSysUser(Long userId, IUserType iUserType) throws Exception {
        SysUser sysUser = getSysUser(userId, iUserType);
        if(V.isEmpty(sysUser)){
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "用户不存在，删除失败！");
        }
        //删除账户信息
        boolean success = super.deleteEntity(sysUser.getId());
        if(!success){
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "删除用户失败！");
        }
        //删除账户绑定的角色信息
        Map<String, Object> criteria = new HashMap(){{
            put("userId", sysUser.getId());
            put("userType", iUserType.getType());
        }};

        if (!userRoleService.deletePhysics(criteria)) {
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
        List<SysUser> sysUserList = getEntityList(sysUserLambdaQueryWrapper);
        if (V.isEmpty(sysUserList)){
            throw new ShiroCustomException(Status.FAIL_OPERATION, "获取数据失败!");
        }
        //构建关系
        Map<Long, SysUser> sysUserMap = buildSysUserAndRoleAndPermissionRelation(sysUserList);
        SysUser sysUser = sysUserMap.get(sysUserList.get(0).getUserId());
        if (V.isEmpty(sysUser.getRoleVOList())) {
            throw new ShiroCustomException(Status.FAIL_OPERATION, "未配置角色，获取数据失败");
        }
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

    @Override
    public SysUser getSysUser(Long userId, IUserType iUserType) {
        //1、获取账户信息
        LambdaQueryWrapper<SysUser> sysUserQueryWrapper = Wrappers.<SysUser>lambdaQuery()
                .in(SysUser::getUserId, userId)
                .eq(SysUser::getUserType, iUserType.getType());
        List<SysUser> sysUserList = getEntityList(sysUserQueryWrapper);
        if(V.notEmpty(sysUserList)) {
            Map<Long, SysUser> sysUserMap = buildSysUserAndRoleAndPermissionRelation(sysUserList);
            SysUserVO sysUserVO = BeanUtils.convert(sysUserMap.get(userId), SysUserVO.class);
            if(V.notEmpty(sysUserVO) && V.notEmpty(sysUserVO.getRoleVOList())){
                List<Long> roleIdList = new ArrayList();
                sysUserVO.getRoleVOList()
                        .stream()
                        .forEach(role -> roleIdList.add(role.getId()));
                sysUserVO.setRoleIdList(roleIdList);
            }
            return sysUserVO;
        }
        return null;
    }

    /**
     * 组装账户 1-n 角色 1-n 权限关系
     * @param sysUserList
     * @return
     */
    private Map<Long, SysUser> buildSysUserAndRoleAndPermissionRelation(List<SysUser> sysUserList) {
        Map<Long, SysUser> userIdSysUserMap = new HashMap<>(32);
        List<Long> sysUserIdList = sysUserList.stream().map(SysUser::getId).collect(Collectors.toList());

        //绑定角色和权限，构建账户和角色关系
        Map<Long, List<RoleVO>> userIdRoleVoListMap = new HashMap<>(16);
        //判断用户是不是管理员
        Map<Long, Boolean> userIdIsAdminMap = new HashMap<>(16);
        //获取账户权限信息
        List<RoleVO> roleVOList = roleService.getSysUserRelRole(sysUserIdList);
        //绑定角色和权限，构建账户和角色关系
        //1、判断用户是不是管理员
        roleVOList.stream().forEach(roleVO -> {
            //构建角色和账户关系
            List<RoleVO> userIdRoleVoList = userIdRoleVoListMap.get(roleVO.getUserId());
            if (V.isEmpty(userIdRoleVoList)) {
                userIdRoleVoList = new ArrayList<>();
            }
            userIdRoleVoList.add(roleVO);
            userIdRoleVoListMap.put(roleVO.getUserId(), userIdRoleVoList);
            //设置权限
            userIdIsAdminMap.put(roleVO.getUserId(), isAdmin(userIdIsAdminMap, roleVO));
        });
        //2 绑定账户和角色关系， 构建用户和账户关系
        sysUserList.stream().forEach(sysUser -> {
            //2.1 绑定账户和角色关系
            sysUser.setRoleVOList(userIdRoleVoListMap.get(sysUser.getId()));
            sysUser.setAdmin(userIdIsAdminMap.get(sysUser.getId()));
            //2.2 构建用户和账户关系
            userIdSysUserMap.put(sysUser.getUserId(), sysUser);
        });
        return userIdSysUserMap;
    }

    /**
     * 设置当前用户是否是系统管理员
     *
     * @param userIdIsAdminMap
     * @param roleVO
     * @return
     */
    private Boolean isAdmin(Map<Long, Boolean> userIdIsAdminMap, RoleVO roleVO) {
        Boolean isAdmin = userIdIsAdminMap.get(roleVO.getUserId());
        if (V.notEmpty(isAdmin)) {
            if (!isAdmin) {
                if (V.notEmpty(systemParamConfig.getAuth())) {
                    isAdmin = systemParamConfig.getAuth().contains(roleVO.getCode());
                } else {
                    isAdmin = "admin".equalsIgnoreCase(roleVO.getCode());
                }
            }
        } else {
            if (V.notEmpty(systemParamConfig.getAuth())) {
                isAdmin = systemParamConfig.getAuth().contains(roleVO.getCode());
            } else {
                isAdmin = "admin".equalsIgnoreCase(roleVO.getCode());
            }
        }
        return isAdmin;
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
