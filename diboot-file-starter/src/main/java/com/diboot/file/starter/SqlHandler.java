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
package com.diboot.file.starter;

import lombok.extern.slf4j.Slf4j;

/**
 * SQL处理类
 * @author Yangzhao
 * @version v2.0
 * @date 2019/08/01
 */
@Slf4j
public class SqlHandler extends com.diboot.core.starter.SqlHandler {
    // 文件SQL
    private static final String FILE_SQL = "SELECT uuid FROM ${SCHEMA}.upload_file WHERE uuid='xyz'";

    /**
     * 检查file表是否已存在
     * @return
     */
    public static boolean checkIsFileTableExists(){
        return checkIsTableExists(FILE_SQL);
    }
}