package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamFrontendPermission;
import com.diboot.iam.entity.IamRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* 角色权限关联Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Mapper
public interface IamRolePermissionMapper extends BaseCrudMapper<IamRolePermission> {
    /**
     * 根据指定的角色ID返回权限集
     * @param roleIds
     * @return
     */
    List<IamFrontendPermission> getPermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 获取角色相关的权限集合
     * @param roleIds
     * @return
     */
    List<String> getApiUrlList(@Param("roleIds") List<Long> roleIds);
}

