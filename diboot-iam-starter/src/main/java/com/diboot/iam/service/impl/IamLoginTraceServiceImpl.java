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

import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.mapper.IamLoginTraceMapper;
import com.diboot.iam.service.BaseIamService;
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
public class IamLoginTraceServiceImpl extends BaseIamServiceImpl<IamLoginTraceMapper, IamLoginTrace> implements com.diboot.core.service.BaseService<IamLoginTrace>, BaseIamService<IamLoginTrace> {

}
