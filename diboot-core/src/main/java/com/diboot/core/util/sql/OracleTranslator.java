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
import com.diboot.core.util.SqlFileInitializer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Oracle SQL翻译器
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/28
 */
public class OracleTranslator extends BaseTranslator {

    @Override
    protected String translateColDefineSql(String colDefineSql) {
        colDefineSql = S.replaceEach(colDefineSql,
                new String[]{" tinyint(1)", " tinyint", "varchar(", " datetime ", " bigint "},
                new String[]{" NUMBER(1)", " NUMBER(1)", "VARCHAR2(", " TIMESTAMP ", " NUMBER(20) "}
        );
        colDefineSql = S.replaceEach(colDefineSql, new String[] {"datetime"}, new String[]{"timestamp"});
        return colDefineSql;
    }

}
