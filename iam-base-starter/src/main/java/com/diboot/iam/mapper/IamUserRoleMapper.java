package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
* 用户角色关联Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Mapper
public interface IamUserRoleMapper extends BaseCrudMapper<IamUserRole> {

}

