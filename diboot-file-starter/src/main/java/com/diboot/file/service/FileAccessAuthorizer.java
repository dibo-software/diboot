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
package com.diboot.file.service;

import com.diboot.file.entity.FileRecord;

/**
 * 文件业务行级访问授权扩展。
 *
 * @author diboot
 * @version v3.9.1
 */
public interface FileAccessAuthorizer {

    /**
     * 支持的业务类型。
     */
    String getBusinessType();

    /**
     * 当前请求是否可以读取文件关联的业务对象。
     */
    boolean canRead(FileRecord fileRecord);
}
