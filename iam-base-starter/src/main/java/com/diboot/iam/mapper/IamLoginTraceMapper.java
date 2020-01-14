package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamLoginTrace;
import org.apache.ibatis.annotations.Mapper;

/**
* 登录记录Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Mapper
public interface IamLoginTraceMapper extends BaseCrudMapper<IamLoginTrace> {

}

