package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamUser;
import org.apache.ibatis.annotations.Mapper;

/**
* 系统用户Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Mapper
public interface IamUserMapper extends BaseCrudMapper<IamUser> {

}

