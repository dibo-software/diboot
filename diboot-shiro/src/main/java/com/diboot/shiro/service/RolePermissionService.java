package com.diboot.shiro.service;

import com.diboot.core.service.BaseService;
import com.diboot.shiro.entity.RolePermission;

import java.util.List;
import java.util.Map;

/**
 * 角色授权相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface RolePermissionService extends BaseService<RolePermission> {

    /**
     * 创建 or 更新 or  删除
     *
     * 批量创建或更新或删除entity（entity.id存在：【如果is_deleted = 1表示逻辑删除，=0表示更新】，若entity.id不存在否则新建）
     * @param entityList
     * @param batchSize
     * @return
     */
    boolean createOrUpdateOrDeleteEntities(List<RolePermission> entityList, int batchSize);

    /**
     * 物理删除
     * @param criteria
     * @return
     */
    boolean deletePhysics(Map<String, Object> criteria);
}
