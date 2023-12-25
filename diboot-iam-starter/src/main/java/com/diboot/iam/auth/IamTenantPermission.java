package com.diboot.iam.auth;

import java.util.List;

/**
 * 租户权限过滤
 *
 * @author : uu
 * @version : v1.0
 * @Date 2023/12/21  21:19
 */
public interface IamTenantPermission {

    /**
     * 过滤出当前租户的所有权限id
     *
     * @param tenantId
     * @return
     */
    List<String> findAllPermissions(String tenantId);

    /**
     * 过滤出当前租户的所有权限code
     *
     * @param tenantId
     * @return
     */
    List<String> findAllPermissionCodes(String tenantId);
}
