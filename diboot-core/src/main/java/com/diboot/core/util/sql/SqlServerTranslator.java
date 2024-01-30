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
 * SqlServer SQL翻译器
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/28
 */
public class SqlServerTranslator extends BaseTranslator {

    @Override
    protected String translateColDefineSql(String colDefineSql) {
        colDefineSql = S.replaceEach(colDefineSql,
            new String[]{"tinyint(1)"},
            new String[]{"tinyint"}
        );
        return colDefineSql;
    }

    @Override
    protected String translateCreateIndexDDL(String mysqlDDL) {
        String createIndex = super.translateCreateIndexDDL(mysqlDDL);
        return createIndex.replace(" index ", " nonclustered index ");
    }

    @Override
    protected String buildColumnCommentSql(String table, String colName, String comment) {
        return "execute sp_addextendedproperty 'MS_Description', N'"+comment.trim()+"', 'SCHEMA', '${SCHEMA}', 'table', "+table+", 'column', '"+colName+"';";
    }

    @Override
    protected String buildTableCommentSql(String table, String comment) {
        return "execute sp_addextendedproperty 'MS_Description', N'"+comment.trim()+"','SCHEMA', '${SCHEMA}', 'table', "+table+", null, null;";
    }

}
