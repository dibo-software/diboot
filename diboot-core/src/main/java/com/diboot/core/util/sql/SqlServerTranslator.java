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

import java.util.List;

/**
 * SqlServer SQL翻译器
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/28
 */
public final class SqlServerTranslator extends BaseTranslator {

    public SqlServerTranslator(){}
    public SqlServerTranslator(List<String> keywords) {
        ESCAPE_KEYWORDS.addAll(keywords);
    }

    @Override
    protected String translateColDefineSql(String colDefineSql) {
        colDefineSql = S.replaceEach(colDefineSql,
            new String[]{"tinyint(1)"},
            new String[]{"tinyint"}
        );
        return escapeKeyword(colDefineSql);
    }

    @Override
    protected String translateCreateIndexDDL(String mysqlDDL) {
        String createIndex = super.translateCreateIndexDDL(mysqlDDL);
        return S.replaceIgnoreCase(createIndex, " index ", " nonclustered index ");
    }

    @Override
    protected String buildColumnCommentSql(String table, String colName, String comment) {
        table = S.replace(table, "`", "");
        colName = S.replace(colName, "`", "");
        return "execute sp_addextendedproperty 'MS_Description', N'"+comment.trim()+"', 'SCHEMA', 'dbo', 'table', '"+table+"', 'column', '"+colName+"';";
    }

    @Override
    protected String buildTableCommentSql(String table, String comment) {
        table = S.replace(table, "`", "");
        return "execute sp_addextendedproperty 'MS_Description', N'"+comment.trim()+"','SCHEMA', 'dbo', 'table', '"+table+"', null, null;";
    }

    @Override
    protected String escapeKeyword(String input) {
        if(input.contains("`")) {
            String key = S.substringBetween(input, "`", "`");
            if(ESCAPE_KEYWORDS.contains(key)) {
                return S.replace(input, "`"+key+"`", "[" + key + "]");
            }
            else {
                return S.replace(input, "`", "");
            }
        }
        return input;
    }

}
