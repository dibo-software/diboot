package com.diboot.shiro.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.shiro.entity.Permission;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 许可授权相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface PermissionService extends BaseService<Permission> {

    /***
     * 获取权限资源列表
     * @param queryWrapper
     * @param pagination
     * @return
     */
    List<Permission> getPermissionList(QueryWrapper queryWrapper, Pagination pagination);

    /**
     * 批量创建或更新或删除entity（entity.id存在：【如果deleted = 1表示逻辑删除，=0表示更新】，若entity.id不存在否则新建）
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    boolean createOrUpdateOrDeleteEntities(Collection<Permission> entityList, int batchSize);

    /**
     * 获取权限和角色之间的关系
     * @param roleIdList
     * @return
     */
    List<Permission> getPermissionListByRoleIdList(List<Long> roleIdList);

    /**
     * 获取应用下的所有权限
     * @return
     */
    List<Permission> getApplicationAllPermissionList();
}
