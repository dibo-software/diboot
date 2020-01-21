package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamAccount;
import org.apache.ibatis.annotations.Mapper;

/**
* 认证用户Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Mapper
public interface IamAccountMapper extends BaseCrudMapper<IamAccount> {

}

