package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.shiro.entity.RolePermission;
import com.diboot.shiro.entity.UserRole;
import com.diboot.shiro.mapper.RolePermissionMapper;
import com.diboot.shiro.service.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 角色授权相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
    @Override
    public boolean createOrUpdateOrDeleteEntities(List<RolePermission> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        Class<?> cls = currentModelClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            for (RolePermission entity : entityList) {
                LambdaQueryWrapper<RolePermission> queryWrapper = Wrappers.<RolePermission>lambdaQuery()
                        .eq(RolePermission::getRoleId, entity.getRoleId())
                        .eq(RolePermission::getPermissionId, entity.getPermissionId())
                        ;
                if (Objects.isNull(getOne(queryWrapper))) {
                    batchSqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), entity);
                }
                //此处物理删除
                else if (entity.isDeleted()){
                    Map<String, Object> criteria = new HashMap(){{
                        put("roleId", entity.getRoleId());
                        put("permissionId", entity.getId());
                    }};
                    deletePhysics(criteria);
                }
                //更新数据
                else {
                    MapperMethod.ParamMap<RolePermission> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    batchSqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID), param);
                }
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    @Override
    public boolean deletePhysics(Map<String, Object> criteria) {
        return getBaseMapper().deletePhysics(criteria) > 0;
    }

}
