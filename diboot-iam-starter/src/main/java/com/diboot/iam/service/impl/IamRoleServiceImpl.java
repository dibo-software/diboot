/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
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
public class IamRoleServiceImpl extends BaseServiceImpl<IamRoleMapper, IamRole> implements IamRoleService {

}
