package com.diboot.iam.dto;

import com.diboot.iam.entity.IamFrontendPermission;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 前端菜单 DTO定义
 * @author yangzhao
 * @version 2.0.0
 * @date 2020-02-27
 * Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamFrontendPermissionDTO extends IamFrontendPermission {
    private static final long serialVersionUID = -7218371066111984841L;

    // 按钮/权限列表
    private List<IamFrontendPermissionDTO> permissionList;
}
