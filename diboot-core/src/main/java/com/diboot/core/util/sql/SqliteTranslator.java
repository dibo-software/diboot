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
import com.diboot.core.util.V;

import java.util.List;

/**
 * Sqlite SQL翻译器
 * @author mazc@dibo.ltd
 * @version v3.3.0
 * @date 2024/03/09
 */
public class SqliteTranslator extends BaseTranslator {

    public SqliteTranslator(){}
    public SqliteTranslator(List<String> keywords) {
        ESCAPE_KEYWORDS.addAll(keywords);
    }

    @Override
    protected String translateColDefineSql(String colDefineSql) {
        // boolean 类型
        colDefineSql = S.replaceEach(colDefineSql,
            new String[]{" smallint ", " int ", " tinyint(1) ", " tinyint ", " datetime ", " bigint ", " json ", " JSON ", " text", " VARCHAR("},
            new String[]{" INTEGER ", " INTEGER ", " INTEGER ", " INTEGER ", " TEXT "," INTEGER ", " TEXT ", " TEXT ", " TEXT", " varchar("}
        );
        if(S.contains(colDefineSql, "varchar(")) {
            colDefineSql = S.substringBefore(colDefineSql, "varchar(") + " TEXT " + S.substringAfter(colDefineSql, ") ");
        }
        return escapeKeyword(colDefineSql);
    }

    @Override
    protected String escapeKeyword(String input) {
        if(input.contains("`")) {
            String key = S.substringBetween(input, "`", "`");
            if(ESCAPE_KEYWORDS.contains(key)) {
                return S.replace(input, "`"+key+"`", key);
            }
            else {
                return S.replace(input, "`", "");
            }
        }
        return input;
    }

    @Override
    protected String buildColumnCommentSql(String table, String colName, String comment) {
        return null;
    }

    @Override
    protected String buildTableCommentSql(String table, String comment) {
        return null;
    }

    @Override
    protected Object translateValue(String colDefine, String value) {
        if(S.containsIgnoreCase(colDefine," BOOLEAN ")) {
            return V.isTrue(value);
        }
        return value;
    }

}
