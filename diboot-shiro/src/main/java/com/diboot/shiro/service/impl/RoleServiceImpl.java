package com.diboot.shiro.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.mapper.RoleMapper;
import com.diboot.shiro.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 角色相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements RoleService {

}
