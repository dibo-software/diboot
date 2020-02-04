package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamUserAccountDTO;
import com.diboot.iam.entity.*;
import com.diboot.iam.mapper.IamUserMapper;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamPermissionService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* 系统用户相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Service
@Slf4j
public class IamUserServiceImpl extends BaseIamServiceImpl<IamUserMapper, IamUser> implements IamUserService {

    @Autowired
    private IamUserRoleService iamUserRoleService;

    @Autowired
    private IamPermissionService iamPermissionService;

    @Autowired
    private IamAccountService iamAccountService;

    @Override
    public IamRoleVO buildRoleVo4FrontEnd(IamUser iamUser) {
        List<IamRoleVO> roleVOList = getAllRoleVOList(iamUser);
        if (V.isEmpty(roleVOList)){
            return null;
        }
        // 附加额外的一些权限给与特性的角色
        attachExtraPermissions(roleVOList);
        // 对RoleList做聚合处理，以适配前端
        List<String> nameList = new ArrayList<>();
        List<IamPermission> allPermissionList = new ArrayList<>();
        roleVOList.forEach(vo -> {
            nameList.add(vo.getName());
            if (V.notEmpty(vo.getPermissionList())){
                allPermissionList.addAll(vo.getPermissionList());
            }
        });
        // 对permissionList进行去重
        List permissionList = BeanUtils.distinctByKey(allPermissionList, IamPermission::getId);
        IamRoleVO roleVO = new IamRoleVO();
        roleVO.setName(S.join(nameList));
        roleVO.setPermissionList(permissionList);

        return roleVO;
    }

    @Override
    public List<IamRoleVO> getAllRoleVOList(IamUser iamUser) {
        List<IamRole> roleList = iamUserRoleService.getUserRoleList(IamUser.class.getSimpleName(), iamUser.getId());
        if (V.isEmpty(roleList)){
            return null;
        }
        return RelationsBinder.convertAndBind(roleList, IamRoleVO.class);
    }

    @Override
    public void attachExtraPermissions(List<IamRoleVO> roleVOList) {
        if (V.isEmpty(roleVOList)){
            return;
        }
        for (IamRoleVO roleVO : roleVOList){
            if (Cons.ROLE_SUPER_ADMIN.equalsIgnoreCase(roleVO.getCode())){
                List<IamPermission> iamPermissions = iamPermissionService.getAllPermissions(Cons.APPLICATION);
                roleVO.setPermissionList(iamPermissions);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserAndAccount(IamUserAccountDTO userAccountDTO) {
        // 创建用户信息
        boolean userSuccess = this.createEntity(userAccountDTO);
        // 创建账号信息
        IamAccount iamAccount = new IamAccount();
        iamAccount
                .setUserType(IamUser.class.getSimpleName())
                .setUserId(userAccountDTO.getId())
                .setAuthAccount(userAccountDTO.getUsername())
                .setAuthSecret(userAccountDTO.getPassword())
                .setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name())
                .setStatus(userAccountDTO.getStatus());
        // 设置密码
        IamSecurityUtils.encryptPwd(iamAccount);
        boolean accountSuccess = iamAccountService.createEntity(iamAccount);

        // 批量创建角色关联关系
        boolean relationsSuccess = iamUserRoleService.createUserRoleRelations(iamAccount.getUserType(), iamAccount.getUserId(), userAccountDTO.getRoleIdList());

        if (!userSuccess || !accountSuccess || !relationsSuccess){
            throw new BusinessException(Status.FAIL_OPERATION, "创建用户失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserAndAccount(IamUserAccountDTO userAccountDTO) {
        // 更新用户信息
        boolean userSuccess = this.updateEntity(userAccountDTO);

        // 更新账号信息
        IamAccount iamAccount = iamAccountService.getSingleEntity(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getUserId, userAccountDTO.getId())
        );
        iamAccount.setAuthAccount(userAccountDTO.getUsername())
                .setStatus(userAccountDTO.getStatus());
        // 设置密码
        if (V.notEmpty(userAccountDTO.getPassword())){
            iamAccount.setAuthSecret(userAccountDTO.getPassword());
            IamSecurityUtils.encryptPwd(iamAccount);
        }
        boolean accountSuccess = iamAccountService.updateEntity(iamAccount);

        // 批量更新角色关联关系
        boolean relationsSuccess = iamUserRoleService.updateUserRoleRelations(iamAccount.getUserType(), iamAccount.getUserId(), userAccountDTO.getRoleIdList());

        if (!userSuccess || !accountSuccess || !relationsSuccess){
            throw new BusinessException(Status.FAIL_OPERATION, "更新用户失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserAndAccount(Long id) throws Exception {
        IamUser iamUser = this.getEntity(id);
        if (iamUser == null){
            throw new BusinessException(Status.FAIL_OPERATION, "删除的记录不存在");
        }
        // 删除用户信息
        boolean userSuccess = this.deleteEntity(id);
        // 删除账号信息
        boolean accountSuccess = iamAccountService.deleteEntities(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getUserId, id)
        );
        // 删除用户角色关联关系列表
        boolean relationsSuccess = iamUserRoleService.deleteEntities(
                Wrappers.<IamUserRole>lambdaQuery()
                        .eq(IamUserRole::getUserType, IamUser.class.getSimpleName())
                        .eq(IamUserRole::getUserId, id)
        );

        if (!userSuccess || !accountSuccess || !relationsSuccess){
            throw new BusinessException(Status.FAIL_OPERATION, "删除用户失败");
        }
        return true;
    }

}
