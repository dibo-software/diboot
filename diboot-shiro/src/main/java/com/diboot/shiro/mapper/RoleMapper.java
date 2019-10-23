package com.diboot.shiro.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.vo.RoleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface RoleMapper extends BaseCrudMapper<Role> {

    /**
     * 根据用户id获取所有角色
     * @param userIdList
     * @return
     */
    List<RoleVO> getRoleByUserIdList(@Param("list") List<Long> userIdList);

}

