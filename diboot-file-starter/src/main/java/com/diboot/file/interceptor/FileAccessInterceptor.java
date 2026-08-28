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
package com.diboot.file.interceptor;

import com.diboot.file.entity.FileRecord;

/**
 * 文件访问拦截器：统一处理文件读/写的权限检查。
 * <p>
 * 默认实现为 {@link DefaultFileAccessInterceptor}，业务方可自定义 Bean 整体替换，
 * 或继承默认实现覆写局部检查逻辑。
 *
 * @author diboot
 * @version v3.9.1
 */
public interface FileAccessInterceptor {

    /**
     * 文件读取（下载）前的权限检查，不通过时抛出权限异常。
     *
     * @param fileRecord 文件记录
     */
    void checkRead(FileRecord fileRecord);

    /**
     * 文件写入（上传）前的权限检查，不通过时抛出权限异常。
     *
     * @param businessType 业务类型
     */
    void checkWrite(String businessType);

}
