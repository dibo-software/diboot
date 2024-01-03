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

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * SQL翻译器基础类
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/28
 */
@Slf4j
public abstract class BaseTranslator {

    /**
     * 执行ddl翻译
     * @param mysqlStatements
     * @return
     */
    public List<String> translate(List<String> mysqlStatements) {
        if(V.isEmpty(mysqlStatements)) {
            return Collections.emptyList();
        }
        List<String> postgresStatements = new ArrayList<>();
        mysqlStatements.forEach(stmt -> {
            if(S.containsIgnoreCase(stmt, "CREATE TABLE ")) {
                List<String> createTableStatements = this.translateCreateTableDDL(stmt);
                postgresStatements.addAll(createTableStatements);
            }
            else if(S.containsIgnoreCase(stmt, "CREATE INDEX ")) {
                postgresStatements.add(this.translateCreateIndexDDL(stmt));
            }
            else {
                throw new InvalidUsageException("暂不支持该SQL翻译：{}", stmt);
            }
        });
        log.debug("转换初始化SQL：{}", postgresStatements);
        return postgresStatements;
    }

    /**
     * 翻译建表DDL
     * @param mysqlDDL
     * @return
     */
    private List<String> translateCreateTableDDL(String mysqlDDL) {
        List<String> newSqls = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String newSql = S.removeDuplicateBlank(mysqlDDL).replace("`", "").replaceAll(" comment ", " COMMENT ");
        String begin = S.substringBefore(newSql, "(").trim();
        String table = S.substringAfterLast(begin, " ");
        sb.append(begin).append("(");

        String body = S.substringAfter(newSql, "(");
        body = S.substringBeforeLast(body, ")");

        List<String> newColDefines = new ArrayList<>();
        List<String> newColComments = new ArrayList<>();
        List<String> columns = S.splitToList(body);
        columns.forEach(col -> {
            col = S.replace(col, "\n", "").trim();
            //\n id varchar(32) NOT NULL COMMENT 'ID'
            String colName = S.substringBefore(col, " ");
            // 提取列备注
            String comment = extractCommentLabel(col);
            col = S.substringBefore(col, "COMMENT").trim();
            if(colName.equals("id")) {
                col += " PRIMARY KEY";
            }
            if(S.containsIgnoreCase(S.removeDuplicateBlank(col), "PRIMARY KEY (`id`)")
            || S.containsIgnoreCase(S.removeDuplicateBlank(col), "PRIMARY KEY (id)")) {
            }
            else {
                // 数据类型替换
                newColDefines.add(translateColDefineSql(col));
                newColComments.add(buildColumnCommentSql(table, colName, comment));
            }
        });
        String comment = S.substringAfterLast(newSql, ")");
        comment = extractCommentLabel(comment);
        sb.append(S.join(newColDefines, ", ")).append(");");

        newSqls.add(sb.toString());
        newSqls.addAll(newColComments);
        newSqls.add(buildTableCommentSql(table, comment));

        return newSqls;
    }

    protected abstract String translateColDefineSql(String colDefineSql);

    /**
     * 翻译建索引DDL
     * @param mysqlDDL
     * @return
     */
    protected String translateCreateIndexDDL(String mysqlDDL) {
        String createIndex = S.removeDuplicateBlank(mysqlDDL).replace("`", "");
        if(!createIndex.endsWith(";")) {
            createIndex += ";";
        }
        return createIndex;
    }

    protected String buildColumnCommentSql(String table, String colName, String comment) {
        return "comment on column "+ table +"."+colName+" is '"+comment+"';";
    }

    protected String buildTableCommentSql(String table, String comment) {
        return "comment on table "+ table +" is '"+comment+"';";
    }

    private String extractCommentLabel(String comment) {
        if(!S.containsIgnoreCase(comment, "COMMENT")) {
            return null;
        }
        comment = S.substringAfter(comment, "COMMENT").trim();
        return S.substringBetween(comment, "'", "'").trim();
    }

}
