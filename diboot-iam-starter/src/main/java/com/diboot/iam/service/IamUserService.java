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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.dto.IamUserAccountDTO;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.vo.IamUserVO;

import java.util.List;

/**
* 系统用户相关Service
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
public interface IamUserService extends BaseIamService<IamUser> {

    /***
     * 添加用户和账号
     * @param userAccountDTO
     * @return
     */
    boolean createUserAndAccount(IamUserAccountDTO userAccountDTO) throws Exception;

    /***
     * 更新用户和账号
     * @param userAccountDTO
     * @return
     */
    boolean updateUserAndAccount(IamUserAccountDTO userAccountDTO) throws Exception;

    /***
     * 删除用户和账号
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteUserAndAccount(Long id) throws Exception;

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
    boolean isUserNumExists(Long id, String userNum);

    /**
     * 获取指定管理者的下属人员
     * @param managerId
     * @return
     */
    List<Long> getUserIdsByManagerId(Long managerId);

    /**
     * 获取用户VO列表
     *
     * @param queryWrapper
     * @param pagination
     * @param orgId
     * @return
     */
    List<IamUserVO> getUserViewList(LambdaQueryWrapper<IamUser> queryWrapper, Pagination pagination, Long orgId);

}
