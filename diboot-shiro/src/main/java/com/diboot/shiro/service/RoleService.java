package com.diboot.shiro.service;

import com.diboot.core.service.BaseService;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.vo.RoleVO;

import java.util.List;
import java.util.Set;

/**
 * 角色相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface RoleService extends BaseService<Role> {

    /***
     * 根据用户类型和用户id获取角色关联权限列表
     * @param userType
     * @param userId
     * @return
     */
    List<RoleVO> getRelatedRoleAndPermissionListByUser(String userType, Long userId);

}
