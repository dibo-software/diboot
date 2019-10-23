package com.diboot.shiro.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.shiro.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 授权Mapper
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface PermissionMapper extends BaseCrudMapper<Permission> {

    /**
     * 获取角色下的所有权限
     * @param roleIdList
     * @return
     */
    List<Permission> getPermissionListByRoleIdList(@Param("list") List<Long> roleIdList);
}

