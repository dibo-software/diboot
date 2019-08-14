package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.shiro.dto.PermissionDto;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.mapper.PermissionMapper;
import com.diboot.shiro.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 许可授权相关Service
 * @author Wuyue
 * @version v2.0
 * @date 2019/6/20
 */
@Service
@Slf4j
public class PermissionServiceImpl extends BaseServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public List<Permission> getPermissionList(QueryWrapper queryWrapper, Pagination pagination) {
        queryWrapper.groupBy("menu_code");
        List<Permission> parentPermissionList = super.getEntityList(queryWrapper, pagination);
        if (V.isEmpty(parentPermissionList)){
            return parentPermissionList;
        }

        // 获取权限编码列表
        List<String> permissionMenuCodeList = parentPermissionList.stream()
                .map(Permission::getMenuCode)
                .distinct()
                .collect(Collectors.toList());

        if (V.isEmpty(permissionMenuCodeList)){
            return parentPermissionList;
        }

        LambdaQueryWrapper<Permission> allSubListQueryWrapper = new LambdaQueryWrapper<>();
        allSubListQueryWrapper.in(Permission::getMenuCode, permissionMenuCodeList);

        // 获取所有子级权限列表
        List<Permission> allSubPermissionList = super.getEntityList(allSubListQueryWrapper);
        // 整理出每一个父级下的所有子级权限列表
        Map<String, List<Permission>> subPermissionListMap = BeanUtils.convertToStringKeyObjectListMap(allSubPermissionList,
                BeanUtils.convertToFieldName(Permission::getMenuCode));

        for (Permission permission : parentPermissionList){
            List<Permission> subPermissionList = subPermissionListMap.get(permission.getMenuCode());
            if (V.notEmpty(subPermissionList)){
                permission.setPermissionList(subPermissionList);
            }
        }

        return parentPermissionList;
    }

    /**
     * 批量创建或更新或删除entity（entity.id存在：【如果deleted = 1表示逻辑删除，=0表示更新】，若entity.id不存在否则新建）
     *
     * @param entityList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateOrDeleteEntities(Collection<Permission> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        Class<?> cls = currentModelClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            for (Permission entity : entityList) {
                Object idVal = ReflectionKit.getMethodValue(cls, entity, keyProperty);
                if (StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal))) {
                    batchSqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), entity);
                }
                //如果 需要删除，那么调用逻辑删除更新语句
                else if (entity.isDeleted()){
                    batchSqlSession.delete(sqlStatement(SqlMethod.DELETE_BY_ID), entity.getId());
                }
                //更新数据
                else {
                    MapperMethod.ParamMap<Permission> param = new MapperMethod.ParamMap<>();
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
}
