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
package com.diboot.iam.cache;

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.AnnotationUtils;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.process.ApiPermissionExtractor;
import com.diboot.iam.annotation.process.ApiPermissionWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IAM缓存manager (已废弃，请替换为 IamPermissionCacheManager)
 * @see com.diboot.iam.cache.IamPermissionCacheManager
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/22
 * Copyright © diboot.com
 */
@Deprecated
@Slf4j
public class IamCacheManager extends IamPermissionCacheManager {

}