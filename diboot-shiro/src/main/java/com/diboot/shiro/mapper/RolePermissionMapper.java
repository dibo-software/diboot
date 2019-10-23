package com.diboot.shiro.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.shiro.entity.RolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 角色授权Mapper
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface RolePermissionMapper extends BaseCrudMapper<RolePermission> {

    /**
     * 物理删除
     * @param criteria
     * @return
     */
    int deletePhysics(@Param("ew")Map<String, Object> criteria);
}

