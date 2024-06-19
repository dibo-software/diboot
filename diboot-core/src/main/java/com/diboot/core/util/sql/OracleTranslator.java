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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Oracle SQL翻译器
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/28
 */
@Slf4j
public class OracleTranslator extends BaseTranslator {

    public OracleTranslator(){}

    public OracleTranslator(List<String> keywords) {
        ESCAPE_KEYWORDS.addAll(keywords);
    }

    @Override
    protected List<String> formatStatements(List<String> otherStatements) {
        return otherStatements.stream().map(s -> S.substringBefore(s, ";")).collect(Collectors.toList());
    }

    @Override
    protected String translateColDefineSql(String colDefineSql) {
        colDefineSql = S.replaceEach(colDefineSql,
                new String[]{" tinyint(1)", " tinyint", "varchar(", " datetime ", " bigint ", " json ", " JSON ", " text"},
                new String[]{" NUMBER(1)", " NUMBER(1)", "VARCHAR2(", " TIMESTAMP ", " NUMBER(20) ", " VARCHAR2(1000) ", " VARCHAR2(1000) ", " CLOB"}
        );
        colDefineSql = S.replaceEach(colDefineSql, new String[] {"datetime"}, new String[]{"timestamp"});
        colDefineSql = S.replace(colDefineSql," default ", " DEFAULT ");
        if(S.contains(colDefineSql, " DEFAULT ")) {
            String prefix = S.substringBefore(colDefineSql, " DEFAULT ") + " ";
            String suffix = S.substringAfter(colDefineSql, " DEFAULT ");
            String defValue = S.substringBefore(suffix.trim(), " ");
            suffix = S.substringAfter(suffix.trim(), " ");
            prefix = S.replaceEach(prefix, new String[]{" not null ", " null "}, new String[]{" NOT NULL ", " NULL "});
            if(S.contains(prefix, " NOT NULL ")) {
                colDefineSql = S.substringBefore(prefix, " NOT NULL ") + " DEFAULT " + defValue + " NOT NULL " + S.substringAfter(prefix, " NOT NULL ")
                        + suffix;
            }
            else if(S.contains(prefix, " NULL ")) {
                colDefineSql = S.substringBefore(prefix, " NULL ") + " DEFAULT " + defValue + " NULL " + S.substringAfter(prefix, " NULL ")
                        + suffix;
            }
        }
        return escapeKeyword(colDefineSql);
    }

    @Override
    protected List<String> translateInsertValues(String insertSql) {
        List<String> batchInsertSqls = new ArrayList<>();
        insertSql = S.removeDuplicateBlank(insertSql).trim();
        String insertIntoPrefix = S.substringBefore(insertSql, "VALUES") + "VALUES";
        if(insertIntoPrefix.contains("`")) {
            for(String key : ESCAPE_KEYWORDS) {
                if(insertIntoPrefix.contains("`"+key+"`")) {
                    insertIntoPrefix = S.replace(insertIntoPrefix, "`"+key+"`", "\"" + key.toUpperCase() + "\"");
                }
            }
        }
        insertIntoPrefix = escapeKeyword(insertIntoPrefix);
        String cols = S.substringBetween(insertIntoPrefix, "(", ")").replace("`", "");
        String[] columns = S.split(cols, ",");
        String table = S.substringBetween(insertIntoPrefix, " INTO ", "(").trim().replace("`", "");
        Map<String, String> col2TypeMap = table2ColumnTypeMap.get(table);
        String suffix = S.substringAfter(insertSql, "VALUES");
        while (S.contains(suffix, "(")) {
            suffix = S.substringAfter(suffix, "(");
            StringBuilder rowSqlSb = new StringBuilder(insertIntoPrefix).append("(");
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
            rowSqlSb.append(S.join(newValues)).append(")");
            batchInsertSqls.add(rowSqlSb.toString());
        }
        return batchInsertSqls;
    }

    @Override
    protected Object translateValue(String colType, String value) {
        if(S.containsIgnoreCase(colType, " BLOB")) {
            return "rawtohex(" + value + ")";
        }
        else if(S.containsIgnoreCase(colType, " TIMESTAMP")) {
            return "CURRENT_TIMESTAMP";
        }
        return value;
    }

    @Override
    protected String escapeKeyword(String input) {
        if(input.contains("`")) {
            String key = S.substringBetween(input, "`", "`");
            if(ESCAPE_KEYWORDS.contains(key)) {
                return S.replace(input, "`"+key+"`", "\"" + key.toUpperCase() + "\"");
            }
            else {
                return S.replace(input, "`", "");
            }
        }
        return input;
    }

}
