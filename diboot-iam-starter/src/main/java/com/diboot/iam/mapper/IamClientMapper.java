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
package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamClient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户端Mapper
 *
 * @author wind
 * @version 3.5.1
 * @date 2025/1/9
 */
@Mapper
public interface IamClientMapper extends BaseCrudMapper<IamClient> {

}

