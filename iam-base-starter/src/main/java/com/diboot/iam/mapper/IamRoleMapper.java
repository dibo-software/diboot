package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamRole;
import org.apache.ibatis.annotations.Mapper;

/**
* 角色Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Mapper
public interface IamRoleMapper extends BaseCrudMapper<IamRole> {

}

