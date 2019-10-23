package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.shiro.entity.UserRole;
import com.diboot.shiro.mapper.UserRoleMapper;
import com.diboot.shiro.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用户角色相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class UserRoleServiceImpl extends BaseServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    /**
     * 批量创建或更新或删除entity（entity.id存在：【如果is_deleted = 1表示逻辑删除，=0表示更新】，若entity.id不存在否则新建）
     *
     * @param entityList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateOrDeleteEntities(Collection<UserRole> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        Class<?> cls = currentModelClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            for (UserRole entity : entityList) {
                LambdaQueryWrapper<UserRole> queryWrapper = Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getRoleId, entity.getRoleId())
                        .eq(UserRole::getUserId, entity.getUserId())
                        .eq(UserRole::getUserType, entity.getUserType());
                if (Objects.isNull(getOne(queryWrapper))) {
                    batchSqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), entity);
                }
                //此处物理删除
                else if (entity.isDeleted()){
                    Map<String, Object> criteria = new HashMap(){{
                       put("roleId", entity.getRoleId());
                       put("userId", entity.getUserId());
                       put("userType", entity.getUserType());
                    }};
                    deletePhysics(criteria);
                }
                //更新数据
                else {
                    MapperMethod.ParamMap<UserRole> param = new MapperMethod.ParamMap<>();
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
