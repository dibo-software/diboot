/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.mobile.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.mobile.entity.IamMember;
import com.diboot.mobile.mapper.IamMemberMapper;
import com.diboot.mobile.service.IamMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 移动端用户service是实现类
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/8/31  14:08
 */
@Service
@Slf4j
public class IamMemberServiceImpl extends BaseServiceImpl<IamMemberMapper, IamMember> implements IamMemberService {
}
