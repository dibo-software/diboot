package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.iam.auth.IamExtensible;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUserRole;
import com.diboot.iam.exception.PermissionException;
import com.diboot.iam.mapper.IamUserRoleMapper;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
* 用户角色关联相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Service
@Slf4j
public class IamUserRoleServiceImpl extends BaseIamServiceImpl<IamUserRoleMapper, IamUserRole> implements IamUserRoleService {

    @Autowired
    private IamUserRoleMapper iamUserRoleMapper;
    @Autowired
    private IamRoleService iamRoleService;

    private IamExtensible iamExtensible;
    private boolean iamExtensibleImplChecked = false;

    /**
     * 超级管理员的角色ID
     */
    private static Long ROLE_ID_SUPER_ADMIN = null;

    @Override
    public List<IamRole> getUserRoleList(String userType, Long userId) {
        List<IamRole> roles = iamUserRoleMapper.getUserRoleList(userType, userId);
        if(!iamExtensibleImplChecked){
            try{
                iamExtensible = ContextHelper.getBean(IamExtensible.class);
            }
            catch (Exception e){}
            iamExtensibleImplChecked = true;
        }
        if(iamExtensible != null){
            List<IamRole> extRoles = iamExtensible.getExtentionRoles(userType, userId);
            if(V.notEmpty(extRoles)){
                roles.addAll(extRoles);
                roles = BeanUtils.distinctByKey(roles, IamRole::getId);
            }
        }
        return roles;
    }

    @Override
    public boolean createEntity(IamUserRole entity){
        Long superAdminRoleId = getSuperAdminRoleId();
        if(superAdminRoleId != null && superAdminRoleId.equals(entity.getRoleId())){
            checkSuperAdminIdentity();
        }
        return super.createEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean createEntities(Collection entityList) {
        if (V.notEmpty(entityList)) {
            Long superAdminRoleId = getSuperAdminRoleId();
            boolean hasSuperAdmin = false;
            for(Object entity : entityList){
                IamUserRole iamUserRole = (IamUserRole)entity;
                if(superAdminRoleId != null && superAdminRoleId.equals(iamUserRole.getRoleId())){
                    hasSuperAdmin = true;
                }
            }
            if(hasSuperAdmin){
                checkSuperAdminIdentity();
            }
        }
        return super.createEntities(entityList);
    }

    @Override
    public boolean createUserRoleRelations(String userType, Long userId, List<Long> roleIds) {
        Long superAdminRoleId = getSuperAdminRoleId();
        // 给用户赋予了超级管理员，需确保当前用户为超级管理员权限
        if(superAdminRoleId != null && roleIds.contains(superAdminRoleId)){
            checkSuperAdminIdentity();
        }
        return false;
    }

    /**
     * 获取超级管理员角色ID
     * @return
     */
    private Long getSuperAdminRoleId(){
        if(ROLE_ID_SUPER_ADMIN == null){
            LambdaQueryWrapper<IamRole> queryWrapper = new LambdaQueryWrapper<IamRole>()
                    .select(IamRole::getId)
                    .eq(IamRole::getCode, Cons.ROLE_SUPER_ADMIN);
            IamRole admin = iamRoleService.getSingleEntity(queryWrapper);
            if(admin != null){
                ROLE_ID_SUPER_ADMIN = admin.getId();
            }
        }
        return ROLE_ID_SUPER_ADMIN;
    }

    /**
     * 检查超级管理员身份
     */
    private void checkSuperAdminIdentity(){
        if(!IamSecurityUtils.getSubject().hasRole(Cons.ROLE_SUPER_ADMIN)){
            throw new PermissionException("非超级管理员用户不可授予其他用户超级管理员权限！");
        }
    }
}
