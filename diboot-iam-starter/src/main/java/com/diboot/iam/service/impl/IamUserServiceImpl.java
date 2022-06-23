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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.dto.IamUserAccountDTO;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.mapper.IamUserMapper;
import com.diboot.iam.service.*;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
* 系统用户相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@SuppressWarnings("JavaDoc")
@Service
@Slf4j
public class IamUserServiceImpl extends BaseIamServiceImpl<IamUserMapper, IamUser> implements IamUserService {

    @Autowired
    private IamUserRoleService iamUserRoleService;

    @Autowired
    private IamAccountService iamAccountService;

    @Autowired
    private IamOrgService iamOrgService;

    @Autowired
    private IamUserPositionService iamUserPositionService;

    @Autowired(required = false)
    private IamCustomize iamCustomize;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserAndAccount(IamUserAccountDTO userAccountDTO) {
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
    public boolean updateUserAndAccount(IamUserAccountDTO userAccountDTO) {
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
    public boolean deleteUserAndAccount(Long id) {
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
        if (V.isEmpty(userNumList)) {
            return Collections.emptyList();
        }
        int totalSize = userNumList.size(), batchSize = BaseConfig.getBatchSize();
        Set<String> uniqueUserNumSet = new HashSet<>(totalSize);
        int startInx = 0;
        while (startInx < totalSize) {
            List<String> existUserNumList = this.checkUserNumDuplicate(
                    // 截取子列表：[0, batchSize)、[batchSize, 2*batchSize)...[n*batchSize, totalSize)
                    userNumList.subList(startInx, Math.min(startInx + batchSize, totalSize))
            );
            if (V.notEmpty(existUserNumList)) {
                uniqueUserNumSet.addAll(existUserNumList);
            }
            startInx += batchSize;
        }
        return new ArrayList<>(uniqueUserNumSet);
    }

    @Override
    public boolean isUserNumExists(Long id, String userNum) {
        if(V.isEmpty(userNum)){
            return true;
        }
        LambdaQueryWrapper<IamUser> wrapper = Wrappers.<IamUser>lambdaQuery()
                .select(IamUser::getUserNum)
                .eq(IamUser::getUserNum, userNum);
        if (V.notEmpty(id)){
            wrapper.ne(IamUser::getId, id);
        }
        return exists(wrapper);
    }

    @Override
    public List<Long> getUserIdsByManagerId(Long managerId) {
        if(managerId == null){
            return null;
        }
        List<Long> orgIds = iamOrgService.getOrgIdsByManagerId(managerId);
        if(V.isEmpty(orgIds)){
            return Collections.emptyList();
        }
        List<Long> iamUserIds = getValuesOfField(
                Wrappers.<IamUser>lambdaQuery().in(IamUser::getOrgId, orgIds),
                IamUser::getId
        );
        return iamUserIds;
    }

    @Override
    public List<IamUserVO> getUserViewList(LambdaQueryWrapper<IamUser> queryWrapper, Pagination pagination, Long orgId) {
        List<Long> orgIds = new ArrayList<>();
        // 获取当前部门及所有下属部门的人员列表
        if (V.notEmpty(orgId) && V.notEquals(orgId, 0L)) {
            orgIds.add(orgId);
            // 获取所有下级部门列表
            orgIds.addAll(iamOrgService.getChildOrgIds(orgId));
            queryWrapper.in(IamUser::getOrgId, orgIds);
            // 相应部门下岗位相关用户
            LambdaQueryWrapper<IamUserPosition> queryUserIds = Wrappers.<IamUserPosition>lambdaQuery()
                    .eq(IamUserPosition::getUserType, IamUser.class.getSimpleName())
                    .in(IamUserPosition::getOrgId, orgIds);
            List<Long> userIds = iamUserPositionService.getValuesOfField(queryUserIds, IamUserPosition::getUserId);
            queryWrapper.or().in(V.notEmpty(userIds), IamUser::getId, userIds);
        }
        // 查询指定页的数据
        List<IamUserVO> voList = getViewObjectList(queryWrapper, pagination, IamUserVO.class);
        if (V.isEmpty(orgIds)) {
            return voList;
        }
        for (IamUserVO user : voList) {
            List<IamUserPosition> userPositionList = user.getUserPositionList();
            if (V.notEmpty(userPositionList)) {
                user.setUserPositionList(userPositionList.stream().filter(p -> orgIds.contains(p.getOrgId())).collect(Collectors.toList()));
            }
        }
        return voList;
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

    protected void createAccount(IamUserAccountDTO userAccountDTO) {
        // 创建账号信息
        IamAccount iamAccount = new IamAccount();
        iamAccount
                .setTenantId(userAccountDTO.getTenantId())
                .setUserType(IamUser.class.getSimpleName())
                .setUserId(userAccountDTO.getId())
                .setAuthAccount(userAccountDTO.getUsername())
                .setAuthSecret(userAccountDTO.getPassword())
                .setAuthType(userAccountDTO.getAuthType())
                .setStatus(userAccountDTO.getStatus());
        // 保存账号
        iamAccountService.createEntity(iamAccount);

        // 批量创建角色关联关系
        iamUserRoleService.createUserRoleRelations(iamAccount.getUserType(), iamAccount.getUserId(), userAccountDTO.getRoleIdList());
    }

    private void deleteAccount(Long userId) {
        if (V.equals(userId, IamSecurityUtils.getCurrentUserId())) {
            throw new BusinessException("不可删除自己的账号");
        }
        // 删除账号信息
        iamAccountService.deleteEntities(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getUserId, userId)
        );
        // 删除用户角色关联关系列表
        iamUserRoleService.deleteUserRoleRelations(IamUser.class.getSimpleName(), userId);
    }

    /**
     * 判断员工编号是否存在
     * @param iamUser
     * @return
     */
    @Override
    protected void beforeCreateEntity(IamUser iamUser){
        if(isUserNumExists(null, iamUser.getUserNum())){
            String errorMsg = "员工编号 "+ iamUser.getUserNum() +" 已存在，请重新设置！";
            log.warn("保存用户异常:{}", errorMsg);
            throw new BusinessException(Status.FAIL_VALIDATION, errorMsg);
        }
    }

    /**
     * 判断员工编号是否存在
     * @param iamUser
     * @return
     */
    @Override
    protected void beforeUpdateEntity(IamUser iamUser){
        if(isUserNumExists(iamUser.getId(), iamUser.getUserNum())){
            String errorMsg = "员工编号 "+ iamUser.getUserNum() +" 已存在，请重新设置！";
            log.warn("保存用户异常:{}", errorMsg);
            throw new BusinessException(Status.FAIL_VALIDATION, errorMsg);
        }
    }

}
