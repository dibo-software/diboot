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
package com.diboot.mobile.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.mobile.entity.IamMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 移动端会员 Mapper
 *
 * @author uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @date 2020-08-31
 */
@Mapper
public interface IamMemberMapper extends BaseCrudMapper<IamMember> {

}

