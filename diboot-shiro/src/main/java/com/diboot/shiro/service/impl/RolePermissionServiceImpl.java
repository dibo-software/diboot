package com.diboot.shiro.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.shiro.entity.RolePermission;
import com.diboot.shiro.mapper.RolePermissionMapper;
import com.diboot.shiro.service.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 角色授权相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

}
