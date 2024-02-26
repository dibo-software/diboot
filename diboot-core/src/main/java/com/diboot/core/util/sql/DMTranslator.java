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
 * DM SQL翻译器
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/28
 */
public final class DMTranslator extends BaseTranslator {

    @Override
    protected String translateColDefineSql(String colDefineSql) {
        colDefineSql = S.replaceEach(colDefineSql,
            new String[]{" tinyint(1) ", " tinyint", " bigint ", " smallint ", " int "},
            new String[]{" BIT ", " BIT", " NUMBER(20) ", " NUMBER(6) ", " NUMBER(9) "}
        );
        return escapeKeyword(colDefineSql);
    }

    @Override
    protected String escapeKeyword(String input) {
        return S.replace(input, "`", "");
    }

}
