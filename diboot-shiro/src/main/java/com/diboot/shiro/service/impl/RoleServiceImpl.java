package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.entity.UserRole;
import com.diboot.shiro.mapper.RoleMapper;
import com.diboot.shiro.service.RoleService;
import com.diboot.shiro.service.UserRoleService;
import com.diboot.shiro.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public List<RoleVO> getRelatedRoleAndPermissionListByUser(String userType, Long userId) {
        // 根据用户类型与用户id获取roleList
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda()
                .eq(UserRole::getUserType, userType)
                .eq(UserRole::getUserId, userId);
        List<UserRole> userRoleList = userRoleService.getEntityList(query);
        if (V.isEmpty(userRoleList)){
            return Collections.emptyList();
        }
        List<Long> roleIdList = userRoleList.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        if (V.isEmpty(roleIdList)){
            return Collections.emptyList();
        }

        // 获取角色列表，并使用VO自动多对多关联permission
        QueryWrapper<Role> roleQuery = new QueryWrapper<>();
        roleQuery
                .lambda()
                .in(Role::getId, roleIdList);
        List<RoleVO> roleVOList = this.getViewObjectList(roleQuery, null, RoleVO.class);
        return roleVOList;
    }
}
