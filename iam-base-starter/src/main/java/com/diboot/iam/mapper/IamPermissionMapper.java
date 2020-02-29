package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamPermission;
import org.apache.ibatis.annotations.Mapper;

/**
* 权限Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Mapper
@Deprecated
public interface IamPermissionMapper extends BaseCrudMapper<IamPermission> {

}

