/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamUserFormDTO;
import com.diboot.iam.entity.*;
import com.diboot.iam.service.*;
import com.diboot.tenant.entity.IamTenant;
import com.diboot.tenant.entity.IamTenantResource;
import com.diboot.tenant.mapper.IamTenantMapper;
import com.diboot.tenant.service.IamTenantResourceService;
import com.diboot.tenant.service.IamTenantService;
import com.diboot.tenant.vo.TenantAdminUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 租户相关Service实现
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Service
@Slf4j
public class IamTenantServiceImpl extends BaseServiceImpl<IamTenantMapper, IamTenant> implements IamTenantService {

    @Autowired
    private IamOrgService iamOrgService;
    @Autowired
    private IamUserService iamUserService;
    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private IamTenantResourceService iamTenantResourceService;
    @Autowired
    private IamUserRoleService iamUserRoleService;
    @Autowired
    private IamAccountService iamAccountService;
    @Autowired
    private IamCustomize iamCustomize;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createIamTenantAndTenantOrgAndTenantAdminRole(IamTenant iamTenant) throws Exception {
        boolean success = this.createEntity(iamTenant);
        if (!success) {
            log.error("创建租户失败！");
            throw new BusinessException(Status.FAIL_EXCEPTION, "创建租户信息失败！");
        }
        // 创建组织机构
        IamOrg iamOrg = buildRootOrgByTenant(iamTenant);
        success = iamOrgService.createEntity(iamOrg);
        if (!success) {
            log.error("创建租户默认IamOrg失败！");
            throw new BusinessException(Status.FAIL_EXCEPTION, "创建租户默认IamOrg失败！");
        }
        // 将顶级id更新为自身的id
        iamOrgService.updateEntity(iamOrg.setRootOrgId(iamOrg.getId()));
        return true;
    }

    @Override
    public TenantAdminUserVO getTenantAdminUserVO(String tenantId) throws Exception {
        // 获取当前租户管理员的角色id
        IamRole iamRole = iamRoleService.getSingleEntity(Wrappers.<IamRole>lambdaQuery().eq(IamRole::getCode, Cons.ROLE_TENANT_ADMIN));
        if (iamRole == null) {
            // 如果不存在租户管理员则自动创建
            iamRoleService.createEntity(new IamRole().setCode(Cons.ROLE_TENANT_ADMIN).setName("租户管理员"));
        } else {
            // 获取绑定租户管理员的用户ID
            String userId = iamUserRoleService.getValueOfField(
                    Wrappers.<IamUserRole>lambdaQuery()
                            .select(IamUserRole::getUserId)
                            .eq(IamUserRole::getTenantId, tenantId)
                            .eq(IamUserRole::getRoleId, iamRole.getId())
                    , IamUserRole::getUserId);
            // 存在绑定关系，获取用户并返回
            if (userId != null) {
                IamUser iamUser = iamUserService.getEntity(userId);
                TenantAdminUserVO tenantAdminUserVO = BeanUtils.convert(iamUser, TenantAdminUserVO.class);
                IamAccount iamAccount = iamAccountService.getSingleEntity(
                        Wrappers.<IamAccount>lambdaQuery()
                                .eq(IamAccount::getTenantId, tenantId)
                                .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                                .eq(IamAccount::getUserId, userId)
                );
                tenantAdminUserVO.setUsername(iamAccount.getAuthAccount())
                        .setAccountId(iamAccount.getId())
                        .setAccountStatus(iamAccount.getStatus());
                return tenantAdminUserVO;
            }
        }
        return new TenantAdminUserVO();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean createOrUpdateTenantAdminUser(IamUserFormDTO iamUserFormDTO) throws Exception {
        IamAccount iamAccount = new IamAccount();
        if (V.notEmpty(iamUserFormDTO.getPassword())) {
            iamAccount.setAuthSecret(iamUserFormDTO.getPassword());
            iamCustomize.encryptPwd(iamAccount);
        }
        iamAccount.setTenantId(iamUserFormDTO.getTenantId())
                .setUserType(IamUser.class.getSimpleName())
                .setUserId(iamUserFormDTO.getId())
                .setAuthAccount(iamUserFormDTO.getUsername())
                .setStatus(iamUserFormDTO.getStatus())
                .setUpdateTime(LocalDateTime.now())
                .setId(iamUserFormDTO.getAccountId());
        if (V.isEmpty(iamUserFormDTO.getId())) {
            // 创建管理员信息
            iamUserService.createEntity(iamUserFormDTO);
            // 创建租户管理员角色和用户的关系
            createIamRole(iamUserFormDTO);
            // 创建管理员账号信息
            iamAccount.setUserId(iamUserFormDTO.getId());
            iamAccountService.createEntity(iamAccount);
        } else {
            // 更新管理员信息
            iamUserService.updateEntity(iamUserFormDTO);
            // 更新管理员账号信息
            iamAccountService.updateEntity(iamAccount);
        }
        return true;
    }

    @Override
    public List<String> getResourceIds(String tenantId) {
        LambdaQueryWrapper<IamTenantResource> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(IamTenantResource::getTenantId, tenantId);
        return iamTenantResourceService.getValuesOfField(queryWrapper, IamTenantResource::getResourceId);
    }

    /**
     * 创建用户角色信息
     *
     * @param iamUserFormDTO
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createIamRole(IamUserFormDTO iamUserFormDTO) {
        IamRole iamRole = iamRoleService.getSingleEntity(Wrappers.<IamRole>lambdaQuery().eq(IamRole::getCode, Cons.ROLE_TENANT_ADMIN));
        IamUserRole iamUserRole = new IamUserRole()
                .setRoleId(iamRole.getId())
                .setUserId(iamUserFormDTO.getId())
                .setTenantId(iamUserFormDTO.getTenantId())
                .setUserType(IamUser.class.getSimpleName());
        iamUserRoleService.createEntity(iamUserRole);
    }

    /**
     * 构建根组织机构
     *
     * @param tenant
     * @return
     */
    private IamOrg buildRootOrgByTenant(IamTenant tenant) {
        return new IamOrg().setCode(tenant.getCode())
                .setTenantId(tenant.getId())
                .setName("我的公司")
                .setParentId(IamOrg.VIRTUAL_ROOT_ID)
                .setRootOrgId(IamOrg.VIRTUAL_ROOT_ID)
                .setType(Cons.DICTCODE_ORG_TYPE.COMP.name());
    }

}
