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
package com.diboot.iam.service;

import com.diboot.core.service.BaseService;
import com.diboot.iam.dto.ChangePwdDTO;
import com.diboot.iam.entity.IamAccount;

import java.util.List;

/**
 * 认证用户相关Service
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
public interface IamAccountService extends BaseService<IamAccount> {
    /**
     * 保存账号（密码存储前加密）
     *
     * @param iamAccount
     * @return
     */
    @Override
    boolean createEntity(IamAccount iamAccount);

    /**
     * 批量创建Entity
     *
     * @param accountList
     * @return
     */
    boolean createEntities(List<IamAccount> accountList);

    /***
     * 更改密码
     * @param changePwdDTO
     * @param iamAccount
     * @return
     * @throws Exception
     */
    boolean changePwd(ChangePwdDTO changePwdDTO, IamAccount iamAccount) throws Exception;

    /**
     * 获取认证账号username
     *
     * @param userType
     * @param userId
     * @return
     */
    String getAuthAccount(String userType, String userId);

    /**
     * 账号是否已存在
     *
     * @param iamAccount
     * @return
     */
    boolean isAccountExists(IamAccount iamAccount);

    /**
     * 账号是否已存在
     *
     * @param tenantId
     * @param userType
     * @param username
     * @param userId
     * @return
     */
    boolean isAccountExists(String tenantId, String userType, String username, String userId);

    /**
     * 更新账号状态
     *
     * @param accountId
     * @return
     */
    boolean updateAccountStatus(String accountId, String status);

}