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

import com.diboot.core.exception.BusinessException;
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

    protected List<String> ESCAPE_KEYWORDS = Arrays.asList("key", "level");

    protected static Map<String, Map<String, String>> table2ColumnTypeMap = new HashMap<>();

    /**
     * 执行ddl翻译
     * @param mysqlStatements
     * @return
     */
    public List<String> translate(List<String> mysqlStatements) {
        if(V.isEmpty(mysqlStatements)) {
            return Collections.emptyList();
        }
        List<String> otherStatements = new ArrayList<>();
        mysqlStatements.forEach(stmt -> {
            if(S.containsIgnoreCase(stmt, "CREATE TABLE ")) {
                List<String> createTableStatements = this.translateCreateTableDDL(stmt);
                otherStatements.addAll(createTableStatements);
            }
            else if(S.containsIgnoreCase(stmt, "CREATE INDEX ")) {
                otherStatements.add(this.translateCreateIndexDDL(stmt));
            }
            else if(S.containsIgnoreCase(stmt, "INSERT INTO ")) {
                otherStatements.addAll(this.translateInsertValues(stmt));
            }
            else if(V.notEmpty(stmt)){
                throw new InvalidUsageException("exception.invalidUsage.baseTranslator.notSupportTranslator", stmt);
            }
        });
        log.debug("转换初始化SQL：{}", otherStatements);
        return formatStatements(otherStatements);
    }

    protected List<String> formatStatements(List<String> otherStatements) {
        return otherStatements;
    }

    /**
     * 翻译建表DDL
     * @param mysqlDDL
     * @return
     */
    private List<String> translateCreateTableDDL(String mysqlDDL) {
        List<String> newSqls = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String newSql = S.removeDuplicateBlank(mysqlDDL).replaceAll(" comment ", " COMMENT ");
        String begin = S.substringBefore(newSql, "(").trim();
        String table = S.substringAfterLast(begin, " ");
        String cleanTableName = table.replace("`", "");
        sb.append(escapeKeyword(begin)).append("(");

        String body = S.substringAfter(newSql, "(");
        body = S.substringBeforeLast(body, ")");

        List<String> newColDefines = new ArrayList<>();
        List<String> newColComments = new ArrayList<>();
        List<String> columns = S.splitToList(body);

        Map<String, String> column2TypeMap = new HashMap<>();
        columns.forEach(col -> {
            col = S.replace(col, "\n", "").trim();
            if(S.containsIgnoreCase(col, "unsigned")) {
                col = S.replaceIgnoreCase(col, " unsigned ", " ");
            }
            if(S.containsIgnoreCase(col, "AUTO_INCREMENT")) {
                col = S.replaceIgnoreCase(col, " AUTO_INCREMENT ", " ");
            }
            col = S.replaceIgnoreCase(col, " ON UPDATE CURRENT_TIMESTAMP", "");
            //\n id varchar(32) NOT NULL COMMENT 'ID'
            String colName = S.substringBefore(col, " ");
            // 提取列备注
            String comment = extractCommentLabel(col);
            col = S.substringBefore(col, "COMMENT").trim();
            String cleanCol = S.removeDuplicateBlank(S.removeDuplicateBlank(col)).replace("`", "");
            if(!S.containsIgnoreCase(cleanCol, "PRIMARY KEY (id)")) {
                String cleanColName = colName.replace("`", "");
                if(cleanColName.equals("id") && !S.containsIgnoreCase(cleanCol, "PRIMARY KEY")) {
                    col += " PRIMARY KEY";
                }
                String colDefineStmt = translateColDefineSql(col);
                newColDefines.add(colDefineStmt);
                if(V.notEmpty(comment)) {
                    String columnCommentSql = buildColumnCommentSql(cleanTableName, colName, comment);
                    if(columnCommentSql != null) {
                        newColComments.add(columnCommentSql);
                    }
                }
                // 数据类型替换
                column2TypeMap.put(cleanColName, colDefineStmt);
            }
        });
        String comment = S.substringAfterLast(newSql, ")");
        comment = extractCommentLabel(comment);
        sb.append(S.join(newColDefines, ", ")).append(");");

        newSqls.add(sb.toString());
        newSqls.addAll(newColComments);
        if(V.notEmpty(comment)) {
            String tableCommentSql = buildTableCommentSql(table, comment);
            if(tableCommentSql != null) {
                newSqls.add(tableCommentSql);
            }
        }
        table2ColumnTypeMap.put(cleanTableName, column2TypeMap);
        return newSqls;
    }

    protected abstract String translateColDefineSql(String colDefineSql);

    /**
     * 翻译建索引DDL
     * @param mysqlDDL
     * @return
     */
    protected String translateCreateIndexDDL(String mysqlDDL) {
        String createIndex = S.removeDuplicateBlank(mysqlDDL).trim();
        if(!createIndex.endsWith(";")) {
            createIndex += ";";
        }
        return escapeKeyword(createIndex);
    }

    protected String escapeKeyword(String input) {
        return input.replace("`", "");
    }

    protected List<String> translateInsertValues(String insertSql) {
        insertSql = S.removeDuplicateBlank(insertSql).trim();
        String prefix = S.substringBefore(insertSql, "VALUES");
        StringBuilder sb = new StringBuilder(escapeKeyword(prefix)).append("VALUES");
        String cols = S.substringBetween(prefix, "(", ")").replace("`", "");
        String[] columns = S.split(cols, ",");

        String table = S.substringBetween(prefix, " INTO ", "(").trim().replace("`", "");

        Map<String, String> col2TypeMap = table2ColumnTypeMap.get(table);
        if(col2TypeMap == null) {
            throw new BusinessException("exception.business.baseTranslator.translateInsertValues.noCache", table);
        }
        String suffix = S.substringAfter(insertSql, "VALUES");
        while (S.contains(suffix, "(")) {
            suffix = S.substringAfter(suffix, "(");
            sb.append("(");
            List<Object> newValues = new ArrayList<>(columns.length);
            List<String> colValues = new ArrayList<>(columns.length);
            String record = S.substringBefore(suffix, ")");
            while (S.contains(record, ",")) {
                record = record.trim();
                String value;
                if(record.startsWith("'")) {
                    value = S.substringBetween(record, "'", "'");
                    value = "'" + value + "'";
                    record = S.substringAfter(record,"'");
                    record = S.substringAfter(record, "'");
                }
                else {
                    value = S.substringBefore(record, ",").trim();
                }
                record = S.substringAfter(record, ",").trim();
                colValues.add(value);
                // last one
                if(!S.contains(record, ",")) {
                    if(record.startsWith("'")) {
                        value = S.substringBetween(record, "'", "'");
                        value = "'" + value + "'";
                    }
                    else {
                        value = record.trim();
                    }
                    colValues.add(value);
                }
            }
            for(int i=0; i<columns.length; i++) {
                String colType = col2TypeMap.get(columns[i].trim());
                newValues.add(translateValue(colType, colValues.get(i)));
            }
            sb.append(S.join(newValues));
            sb.append(")");
            if(S.contains(suffix, "(")) {
                sb.append(",");
            }
            else {
                sb.append(";");
            }
        }
        return Collections.singletonList(sb.toString());
    }

    protected Object translateValue(String colDefine, String value) {
        return value;
    }

    protected String buildColumnCommentSql(String table, String colName, String comment) {
        return "comment on column "+ table +"."+escapeKeyword(colName)+" is '"+comment+"';";
    }

    protected String buildTableCommentSql(String table, String comment) {
        return "comment on table "+ escapeKeyword(table) +" is '"+comment+"';";
    }

    private String extractCommentLabel(String comment) {
        if(!S.containsIgnoreCase(comment, "COMMENT")) {
            return null;
        }
        comment = S.substringAfter(comment, "COMMENT").trim();
        return S.substringBetween(comment, "'", "'").trim();
    }

}
