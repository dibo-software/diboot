package com.diboot.shiro.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.shiro.entity.UserRole;
import com.diboot.shiro.mapper.UserRoleMapper;
import com.diboot.shiro.service.PermissionService;
import com.diboot.shiro.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户角色相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class UserRoleServiceImpl extends BaseServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
