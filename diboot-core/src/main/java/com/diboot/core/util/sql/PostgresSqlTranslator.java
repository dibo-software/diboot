/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.util.sql;

import com.diboot.core.util.S;

/**
 * PostgresSql SQL翻译器
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/28
 */
public class PostgresSqlTranslator extends BaseTranslator {

    @Override
    protected String translateColDefineSql(String colDefineSql) {
        // boolean 类型
        if(S.containsIgnoreCase(colDefineSql, "tinyint(1)")) {
            return S.replaceEach(colDefineSql,
                    new String[]{"tinyint(1)", "DEFAULT '1'", "DEFAULT 1", "default 1", "DEFAULT '0'", "default 0", "DEFAULT 0"},
                    new String[]{"BOOLEAN", "DEFAULT TRUE", "DEFAULT TRUE", "DEFAULT TRUE", "DEFAULT FALSE", "DEFAULT FALSE", "DEFAULT FALSE"}
            );
        }
        if(S.containsIgnoreCase(colDefineSql, "datetime")) {
            return S.replaceEach(colDefineSql, new String[] {"datetime", " on update CURRENT_TIMESTAMP", "ON UPDATE CURRENT_TIMESTAMP"}, new String[]{"timestamp", "", ""});
        }
        return colDefineSql;
    }

}
