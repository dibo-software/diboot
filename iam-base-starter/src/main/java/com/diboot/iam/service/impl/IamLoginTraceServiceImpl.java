package com.diboot.iam.service.impl;

import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.mapper.IamLoginTraceMapper;
import com.diboot.iam.service.IamLoginTraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* 登录记录相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Service
@Slf4j
public class IamLoginTraceServiceImpl extends BaseIamServiceImpl<IamLoginTraceMapper, IamLoginTrace> implements IamLoginTraceService, com.diboot.iam.service.BaseIamService<IamLoginTrace> {

}
