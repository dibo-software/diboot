package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.example.entity.SysUser;
import com.diboot.example.mapper.SysUserMapper;
import com.diboot.example.service.SysUserService;
import com.diboot.example.vo.SysUserVO;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.entity.UserRole;
import com.diboot.shiro.service.UserRoleService;
import com.diboot.shiro.util.AuthHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service("sysUserService")
@Slf4j
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    private static final Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public SysUserVO getSysUser(Long id) {
        SysUserVO sysUserVO = super.getViewObject(id, SysUserVO.class);
        if(V.notEmpty(sysUserVO)){
            List<Role> roleList = sysUserVO.getRoleList();
            if(V.notEmpty(roleList)){
                List<Long> roleIdList = new ArrayList();
                for(Role role : roleList){
                    roleIdList.add(role.getId());
                }
                sysUserVO.setRoleIdList(roleIdList);
            }
        }
        return sysUserVO;
    }

    @Override
    @Transactional
    public boolean createSysUser(SysUser user) {
        // 对密码进行处理
        if (V.notEmpty(user.getPassword())){
            this.encryptPassword(user);
        }
        //新建用户信息
        boolean success = super.createEntity(user);
        if(!success){
            return false;
        }
        //新建用户-角色信息
        List<Role> roleList = user.getRoleList();
        if(V.notEmpty(roleList)){
            List<UserRole> urList = new ArrayList<>();
            for(Role role : roleList){
                UserRole userRole = new UserRole();
                userRole.setRoleId(role.getId());
                userRole.setUserId(user.getId());
                userRole.setUserType(user.getClass().getSimpleName());
                urList.add(userRole);
            }
            try {
                if(V.notEmpty(urList)){
                    userRoleService.createEntities(urList);
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updateSysUser(SysUser user) {
        // 对密码进行处理
        if (V.notEmpty(user.getPassword())){
            this.encryptPassword(user);
        }
        //更新用户信息
        boolean success = super.updateEntity(user);
        if(!success){
            return false;
        }
        //获取用户原来拥有的角色信息
        QueryWrapper<UserRole> wrapper = new QueryWrapper();
        wrapper.lambda().eq(UserRole::getUserId, user.getId())
                        .eq(UserRole::getUserType, user.getClass().getSimpleName());
        List<UserRole> oldUserRoleList = userRoleService.getEntityList(wrapper);

        List<Role> newRolelist = user.getRoleList();

        StringBuffer oldBuffer = new StringBuffer();
        StringBuffer newBuffer = new StringBuffer();
        if(V.notEmpty(oldUserRoleList)){
            for(UserRole ur : oldUserRoleList){
                oldBuffer.append(ur.getRoleId()).append(",");
            }
        }
        if(V.notEmpty(newRolelist)){
            for(Role r : newRolelist){
                newBuffer.append(r.getId()).append(",");
            }
        }
        //删除页面取消选择的用户角色
        if(V.notEmpty(oldUserRoleList)){
            List<Long> idList = new ArrayList<>();
            for(UserRole ur : oldUserRoleList){
                if(!(newBuffer.toString().contains(ur.getRoleId().toString()))){
                    idList.add(ur.getId());
                }
            }
            try {
                if(V.notEmpty(idList)){
                    wrapper = new QueryWrapper();
                    wrapper.lambda().in(UserRole::getId, idList);
                    userRoleService.deleteEntities(wrapper);
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        //新增页面选择的用户角色
        if(V.notEmpty(newRolelist)){
            List<UserRole> urList = new ArrayList<>();
            for(Role role : newRolelist){
                if(!(oldBuffer.toString().contains(role.getId().toString()))){
                    UserRole entity = new UserRole();
                    entity.setRoleId(role.getId());
                    entity.setUserId(user.getId());
                    entity.setUserType(user.getClass().getSimpleName());
                    urList.add(entity);
                }
            }
            try {
                if(V.notEmpty(urList)){
                    userRoleService.createEntities(urList);
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteSysUser(Long id) {
        //删除用户信息
        boolean success = super.deleteEntity(id);
        if(!success){
            return false;
        }

        //删除用户原来拥有的角色信息
        QueryWrapper<UserRole> wrapper = new QueryWrapper();
        wrapper.lambda().eq(UserRole::getUserId, id)
                        .eq(UserRole::getUserType, SysUser.class.getSimpleName());
        try {
            userRoleService.deleteEntities(wrapper);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }

    /***
     * 设置加密密码相关的数据
     * @param sysUser
     */
    private void encryptPassword(SysUser sysUser) {
        String salt = AuthHelper.createSalt();
        String password = AuthHelper.encryptMD5(sysUser.getPassword(), salt, true);
        sysUser.setSalt(salt);
        sysUser.setPassword(password);
    }
}
