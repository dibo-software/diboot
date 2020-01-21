package com.diboot.iam.service;

import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUserRole;

import java.util.List;

/**
* 用户角色关联相关Service
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
public interface IamUserRoleService extends BaseIamService<IamUserRole> {

    /**
     * 获取用户所有的全部角色
     * @param userType
     * @param userId
     * @return
     */
    List<IamRole> getUserRoleList(String userType, Long userId);

    /**
     * 批量创建用户-角色的关系
     * @param userType
     * @param userId
     * @param roleIds
     * @return
     */
    boolean createUserRoleRelations(String userType, Long userId, List<Long> roleIds);

}