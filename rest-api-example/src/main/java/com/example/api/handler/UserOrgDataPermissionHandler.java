package com.example.api.handler;

import com.diboot.iam.data.UserOrgDataAccessScopeManager;
import com.diboot.iam.entity.IamUser;

import java.util.List;

/**
 * 拦截处理器默认实现 - 基于用户组织的实现策略
 * @author JerryMa
 * @version v3.0.0
 * @date 2022/9/9
 * Copyright © diboot.com
 */
//@Component
public class UserOrgDataPermissionHandler extends UserOrgDataAccessScopeManager {

    @Override
    public List<Class<?>> getEntityClasses() {
        return List.of(IamUser.class);
    }

}