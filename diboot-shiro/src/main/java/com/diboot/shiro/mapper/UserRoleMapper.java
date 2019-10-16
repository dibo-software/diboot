package com.diboot.shiro.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.shiro.entity.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 用户角色Mapper
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface UserRoleMapper extends BaseCrudMapper<UserRole> {

    /**
     * 物理删除
     * @param wrapper
     * @return
     */
    int deletePhysics(@Param("ew")Map<String, Object> criteria);
}

