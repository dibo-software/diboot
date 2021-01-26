/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.binding.query.dynamic.DynamicJoinQueryWrapper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamUserAccountDTO;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.entity.IamUserRole;
import com.diboot.iam.mapper.IamUserMapper;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamResourcePermissionService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.util.IamHelper;
import com.diboot.iam.vo.IamRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
    private IamResourcePermissionService iamResourcePermissionService;

    @Autowired
    private IamAccountService iamAccountService;

    @Autowired(required = false)
    private IamCustomize iamCustomize;

    @Override
    public List<IamUser> getEntityListSortByOrg(QueryWrapper queryWrapper, Pagination pagination) {
        // 如果是动态join，则调用JoinsBinder
        if(queryWrapper instanceof DynamicJoinQueryWrapper){
            return Binder.joinQueryList((DynamicJoinQueryWrapper)queryWrapper, entityClass, pagination);
        }
        // 否则，调用MP默认实现
        if(pagination != null){
            IPage<IamUser> page = convertToIPage(queryWrapper, pagination);
            page = super.getBaseMapper().selectPageSortByOrg(page, queryWrapper);
            // 如果重新执行了count进行查询，则更新pagination中的总数
            if(page.isSearchCount()){
                pagination.setTotalCount(page.getTotal());
            }
            return page.getRecords();
        }
        else{
            List<IamUser> list = super.list(queryWrapper);
            if(list == null){
                list = Collections.emptyList();
            }
            else if(list.size() > BaseConfig.getBatchSize()){
                log.warn("单次查询记录数量过大，返回结果数={}", list.size());
            }
            return list;
        }
    }

    @Override
    public <VO> List<VO> getViewObjectListSortByOrg(QueryWrapper queryWrapper, Pagination pagination, Class<VO> voClass) {
        List<IamUser> entityList = this.getEntityListSortByOrg(queryWrapper, pagination);
        List<VO> voList = RelationsBinder.convertAndBind(entityList, voClass);
        return voList;
    }

    @Override
    public IamRoleVO buildRoleVo4FrontEnd(IamUser iamUser) {
        List<IamRoleVO> roleVOList = getAllRoleVOList(iamUser);
        if (V.isEmpty(roleVOList)){
            return null;
        }
        // 附加额外的一些权限给与特性的角色
        attachExtraPermissions(roleVOList);
        // 组合为前端格式
        return IamHelper.buildRoleVo4FrontEnd(roleVOList);
    }

    @Override
    public List<IamRoleVO> getAllRoleVOList(IamUser iamUser) {
        return iamUserRoleService.getAllRoleVOList(iamUser);
    }

    @Override
    public void attachExtraPermissions(List<IamRoleVO> roleVOList) {
        if (V.isEmpty(roleVOList)){
            return;
        }
        for (IamRoleVO roleVO : roleVOList){
            if (Cons.ROLE_SUPER_ADMIN.equalsIgnoreCase(roleVO.getCode())){
                List<IamResourcePermission> iamPermissions = iamResourcePermissionService.getAllResourcePermissions(Cons.APPLICATION);
                roleVO.setPermissionList(iamPermissions);
                return;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserAndAccount(IamUserAccountDTO userAccountDTO) throws Exception {
        // 创建用户信息
        this.createEntity(userAccountDTO);
        // 如果提交的有账号信息，则新建账号信息
        if (V.notEmpty(userAccountDTO.getUsername())) {
            // 新建account账号
            this.createAccount(userAccountDTO);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserAndAccount(IamUserAccountDTO userAccountDTO) throws Exception {
        // 更新用户信息
        this.updateEntity(userAccountDTO);

        IamAccount iamAccount = iamAccountService.getSingleEntity(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getUserId, userAccountDTO.getId())
        );

        if (iamAccount == null) {
            if (V.isEmpty(userAccountDTO.getUsername())){
                return true;
            } else {
                // 新建account账号
                this.createAccount(userAccountDTO);
            }
        } else {
            if (V.isEmpty(userAccountDTO.getUsername())) {
                // 删除账号
                this.deleteAccount(userAccountDTO.getId());
            } else {
                // 更新账号
                iamAccount.setAuthAccount(userAccountDTO.getUsername())
                        .setStatus(userAccountDTO.getStatus());
                // 设置密码
                if (V.notEmpty(userAccountDTO.getPassword())){
                    iamAccount.setAuthSecret(userAccountDTO.getPassword());
                    iamCustomize.encryptPwd(iamAccount);
                }
                iamAccountService.updateEntity(iamAccount);

                // 批量更新角色关联关系
                iamUserRoleService.updateUserRoleRelations(iamAccount.getUserType(), iamAccount.getUserId(), userAccountDTO.getRoleIdList());
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserAndAccount(Long id) throws Exception {
        if (exists(IamUser::getId, id) == false){
            throw new BusinessException(Status.FAIL_OPERATION, "删除的记录不存在");
        }
        // 删除用户信息
        this.deleteEntity(id);
        // 删除账号信息
        this.deleteAccount(id);

        return true;
    }

    @Override
    public List<String> filterDuplicateUserNums(List<String> userNumList) {
        List<String> batchUserNumList = new ArrayList<>();
        List<String> allDuplicateUserNumList = new ArrayList<>();
            for (int i = 0; i < userNumList.size(); i++) {
            if (i > 0 && i % BaseConfig.getBatchSize() == 0) {
                List<String> duplicateUserNumList = this.checkUserNumDuplicate(batchUserNumList);
                if (V.notEmpty(duplicateUserNumList)) {
                    allDuplicateUserNumList.addAll(duplicateUserNumList);
                }
                batchUserNumList.clear();
            }
            batchUserNumList.add(userNumList.get(i));
        }
        if (V.notEmpty(batchUserNumList)) {
            List<String> duplicateUserNumList = this.checkUserNumDuplicate(batchUserNumList);
            if (V.notEmpty(duplicateUserNumList)) {
                allDuplicateUserNumList.addAll(duplicateUserNumList);
            }
            batchUserNumList.clear();
        }
        return allDuplicateUserNumList;
    }

    /***
     * 检查重复用户编号
     * @param userNumList
     * @return
     */
    private List<String> checkUserNumDuplicate(List<String> userNumList) {
        if (V.isEmpty(userNumList)) {
            return Collections.emptyList();
        }
        List<String> iamUserNums = getValuesOfField(
                Wrappers.<IamUser>lambdaQuery().in(IamUser::getUserNum, userNumList),
                IamUser::getUserNum
        );
        return iamUserNums;
    }

    private void createAccount(IamUserAccountDTO userAccountDTO) throws Exception{
        // 创建账号信息
        IamAccount iamAccount = new IamAccount();
        iamAccount
                .setUserType(IamUser.class.getSimpleName())
                .setUserId(userAccountDTO.getId())
                .setAuthAccount(userAccountDTO.getUsername())
                .setAuthSecret(userAccountDTO.getPassword())
                .setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name())
                .setStatus(userAccountDTO.getStatus());
        // 保存账号
        iamAccountService.createEntity(iamAccount);

        // 批量创建角色关联关系
        iamUserRoleService.createUserRoleRelations(iamAccount.getUserType(), iamAccount.getUserId(), userAccountDTO.getRoleIdList());
    }

    private void deleteAccount(Long userId) throws Exception {
        // 删除账号信息
        iamAccountService.deleteEntities(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getUserId, userId)
        );
        // 删除用户角色关联关系列表
        iamUserRoleService.deleteEntities(
                Wrappers.<IamUserRole>lambdaQuery()
                        .eq(IamUserRole::getUserType, IamUser.class.getSimpleName())
                        .eq(IamUserRole::getUserId, userId)
        );
    }



}
