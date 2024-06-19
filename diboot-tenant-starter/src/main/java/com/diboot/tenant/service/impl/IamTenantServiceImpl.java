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
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamUserFormDTO;
import com.diboot.iam.entity.*;
import com.diboot.iam.mapper.IamAccountMapper;
import com.diboot.iam.mapper.IamRoleMapper;
import com.diboot.iam.mapper.IamUserMapper;
import com.diboot.iam.mapper.IamUserRoleMapper;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.service.IamRoleService;
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
    private IamUserMapper iamUserMapper;
    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private IamRoleMapper iamRoleMapper;
    @Autowired
    private IamTenantResourceService iamTenantResourceService;
    @Autowired
    private IamUserRoleMapper iamUserRoleMapper;
    @Autowired
    private IamAccountMapper iamAccountMapper;
    @Autowired
    private IamCustomize iamCustomize;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTenantAndInitData(IamTenant iamTenant) throws Exception {
        boolean success = this.createEntity(iamTenant);
        if (!success) {
            log.error("创建租户失败！");
            throw new BusinessException(Status.FAIL_EXCEPTION, "exception.business.tenantService.createFailed");
        }
        // 创建组织机构
        IamOrg iamOrg = buildRootOrgByTenant(iamTenant);
        success = iamOrgService.createEntity(iamOrg);
        if (!success) {
            log.error("创建租户默认IamOrg失败！");
            throw new BusinessException(Status.FAIL_EXCEPTION, "exception.business.tenantService.createDefaultIamOrgFailed");
        }
        // 将顶级id更新为自身的id
        iamOrgService.updateEntity(iamOrg.setRootOrgId(iamOrg.getId()));
        return true;
    }

    @Override
    public TenantAdminUserVO getTenantAdminUserVO(String tenantId) throws Exception {
        // 获取当前租户管理员的角色id
        List<IamRole> iamRoles = iamRoleMapper.findByCode(Cons.ROLE_TENANT_ADMIN, BaseConfig.getActiveFlagValue());
        if (V.isEmpty(iamRoles)) {
            // 如果不存在租户管理员则自动创建
            iamRoleService.createEntity(new IamRole().setCode(Cons.ROLE_TENANT_ADMIN).setName("租户管理员"));
        } else {
            IamRole iamRole = iamRoles.get(0);
            // 获取绑定租户管理员的用户ID
            List<String> userIds = iamUserRoleMapper.findUserIdByTenantIdAndRoleId(tenantId, iamRole.getId(), BaseConfig.getActiveFlagValue());
            // 存在绑定关系，获取用户并返回
            if (V.notEmpty(userIds)) {
                String userId = userIds.get(0);
                IamUser iamUser = iamUserMapper.selectById(userId);
                TenantAdminUserVO tenantAdminUserVO = BeanUtils.convert(iamUser, TenantAdminUserVO.class);

                List<IamAccount> iamAccounts = iamAccountMapper.findByExplicitTenant(tenantId, userId, IamUser.class.getSimpleName(), BaseConfig.getActiveFlagValue());
                IamAccount iamAccount = iamAccounts.get(0);
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
            iamUserMapper.insert(iamUserFormDTO);
            // 创建租户管理员角色和用户的关系
            createIamRole(iamUserFormDTO);
            // 创建管理员账号信息
            iamAccount.setUserId(iamUserFormDTO.getId());
            iamAccountMapper.insert(iamAccount);
        } else {
            // 更新管理员信息
            iamUserMapper.updateTenantAdmin(iamUserFormDTO, BaseConfig.getActiveFlagValue());
            // 更新管理员账号信息
            iamAccountMapper.updateTenantAccount(iamAccount, BaseConfig.getActiveFlagValue());
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
        List<IamRole> iamRoles = iamRoleMapper.findByCode(Cons.ROLE_TENANT_ADMIN, BaseConfig.getActiveFlagValue());
        IamRole iamRole = iamRoles.get(0);
        IamUserRole iamUserRole = new IamUserRole()
                .setRoleId(iamRole.getId())
                .setUserId(iamUserFormDTO.getId())
                .setTenantId(iamUserFormDTO.getTenantId())
                .setUserType(IamUser.class.getSimpleName());
        iamUserRoleMapper.insert(iamUserRole);
    }

    /**
     * 构建根组织机构
     *
     * @param tenant
     * @return
     */
    private IamOrg buildRootOrgByTenant(IamTenant tenant) {
        IamOrg iamOrg = new IamOrg();
        iamOrg.setParentId(IamOrg.VIRTUAL_ROOT_ID);
        iamOrg.setCode(tenant.getCode())
                .setTenantId(tenant.getId())
                .setName(tenant.getName())
                .setRootOrgId(IamOrg.VIRTUAL_ROOT_ID)
                .setType(Cons.DICTCODE_ORG_TYPE.COMP.name());
        return iamOrg;
    }

}
