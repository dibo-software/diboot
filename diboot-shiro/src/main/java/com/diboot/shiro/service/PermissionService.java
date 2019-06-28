package com.diboot.shiro.service;

import com.diboot.core.service.BaseService;
import com.diboot.shiro.entity.Permission;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * 许可授权相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface PermissionService extends BaseService<Permission> {

    /**
     * 批量创建或更新或删除entity（entity.id存在：【如果deleted = 1表示逻辑删除，=0表示更新】，若entity.id不存在否则新建）
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    boolean createOrUpdateOrDeleteEntities(Collection<Permission> entityList, int batchSize);
}
