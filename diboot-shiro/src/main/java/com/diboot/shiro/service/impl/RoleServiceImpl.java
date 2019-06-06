package com.diboot.shiro.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.mapper.RoleMapper;
import com.diboot.shiro.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 员工相关Service
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@Service
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements RoleService {

}
