package com.diboot.shiro.service;

import com.diboot.core.service.BaseService;
import com.diboot.shiro.entity.UserRole;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

/**
 * 用户角色Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface UserRoleService extends BaseService<UserRole> {

    /**
     * 批量创建或更新或删除entity（entity.id存在：【如果deleted = 1表示逻辑删除，=0表示更新】，若entity.id不存在否则新建）
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    boolean createOrUpdateOrDeleteEntities(Collection<UserRole> entityList, int batchSize);

    /**
     * 根据条件物理删除 必传选项(userId、userType、roleId)
     * @param criteria
     * @return
     */
    boolean deletePhysics(Map<String, Object> criteria);

}
