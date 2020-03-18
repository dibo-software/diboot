package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamRolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* 角色权限关联Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Mapper
public interface IamRolePermissionMapper extends BaseCrudMapper<IamRolePermission> {

}

