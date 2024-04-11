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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.dto.IamUserFormDTO;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.vo.IamUserVO;

import java.util.List;
import java.util.Map;

/**
* 系统用户相关Service
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
public interface IamUserService extends BaseIamService<IamUser> {

    /**
     * 添加用户及相关信息
     *
     * @param userAccountDTO
     * @return
     */
    boolean createUserRelatedInfo(IamUserFormDTO userAccountDTO) throws Exception;

    /**
     * 更新用户及相关信息
     *
     * @param userAccountDTO
     * @return
     */
    boolean updateUserRelatedInfo(IamUserFormDTO userAccountDTO) throws Exception;

    /***
     * 删除用户和关联信息（账号、岗位、角色等）
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteUserAndRelatedInfo(String id) throws Exception;

    /**
     * 过滤重复的员工号
     * @param userNumList
     * @return
     */
    List<String> filterDuplicateUserNums(List<String> userNumList);

    /**
     * 用户编号是否存在
     * @param id
     * @param userNum
     * @return
     */
    boolean isUserNumExists(String id, String userNum);

    /**
     * 获取指定管理者的下属人员
     * @param managerId
     * @return
     */
    List<String> getUserIdsByManagerId(String managerId);

    /**
     * 获取指定用户的上级id
     * @param userId
     * @return
     */
    String getUserLeaderId(String userId);

    /**
     * 获取指定角色下的用户
     *
     * @param roleIds
     * @return
     */
    List<IamUser> getUsersByRoleIds(List<String> roleIds);

    /**
     * 获取指定角色code下的用户
     *
     * @param roleCode
     * @return
     */
    List<IamUser> getUsersByRoleCode(String roleCode);

    /**
     * 获取指定角色code下的用户
     *
     * @param roleCodes
     * @return
     */
    List<IamUser> getUsersByRoleCodes(List<String> roleCodes);

    /**
     * 获取id值-选项的映射Map
     * @param ids
     * @return
     */
    Map<String, LabelValue> getLabelValueMap(List<String> ids);

    /**
     * 获取用户VO列表
     *
     * @param queryWrapper
     * @param pagination
     * @param orgId
     * @return
     */
    List<IamUserVO> getUserViewList(QueryWrapper<IamUser> queryWrapper, Pagination pagination, String orgId);

    /**
     * 刷新用户电话邮箱头像等信息
     * @return
     */
    void refreshUserInfo(IamUser currentUser);
}
