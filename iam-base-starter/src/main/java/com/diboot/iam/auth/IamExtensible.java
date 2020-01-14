package com.diboot.iam.auth;

import com.diboot.iam.entity.IamRole;

import java.util.List;

/**
 * IAM扩展接口
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/01/04
 */
public interface IamExtensible {
    /**
     * 获取可扩展的角色
     * @param userType
     * @param userId
     * @return
     */
    List<IamRole> getExtentionRoles(String userType, Long userId);

}
