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
package com.diboot.iam.auth;

import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.exception.PermissionException;

/**
 * IAM自定义接口
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/04
 */
public interface IamCustomize {

    /**
     * 获取当前登录用户
     * @return
     */
    BaseLoginUser getCurrentUser();

    /***
     * 对用户密码加密
     * @param iamAccount
     */
    void encryptPwd(IamAccount iamAccount);

    /**
     * 加密
     * @param password
     * @param salt
     * @return
     */
    String encryptPwd(String password, String salt);

    /**
     * 检查权限码
     * @param permissionCode
     * @throws PermissionException
     */
    void checkPermission(String permissionCode) throws PermissionException;

    /**
     * 检查当前用户是否拥有某角色
     * @param role
     * @return
     */
    boolean checkCurrentUserHasRole(String role);

    /**
     * 清空缓存
     * @param userType
     * @param userId
     */
    void clearAuthorizationCache(String userType, String userId);

}
