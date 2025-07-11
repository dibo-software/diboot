/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.iam.entity.IamClient;
import com.diboot.iam.mapper.IamClientMapper;
import com.diboot.iam.service.IamClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 客户端相关Service实现
 *
 * @author wind
 * @version 3.5.1
 * @date 2025/1/9
 */
@Service
@Slf4j
public class IamClientServiceImpl extends BaseServiceImpl<IamClientMapper, IamClient> implements IamClientService {

}
