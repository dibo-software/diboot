package com.diboot.iam.service.impl;

import com.diboot.iam.entity.IamRole;
import com.diboot.iam.mapper.IamRoleMapper;
import com.diboot.iam.service.IamRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* 角色相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Service
@Slf4j
public class IamRoleServiceImpl extends BaseIamServiceImpl<IamRoleMapper, IamRole> implements IamRoleService {

}
