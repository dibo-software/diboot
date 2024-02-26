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
import java.util.stream.Collectors;

/**
 * Oracle SQL翻译器
 * @author mazc@dibo.ltd
 * @version v3.2.0
 * @date 2023/12/28
 */
public class OracleTranslator extends BaseTranslator {

    @Override
    protected List<String> formatStatements(List<String> otherStatements) {
        return otherStatements.stream().map(s -> S.substringBefore(s, ";")).collect(Collectors.toList());
    }

    @Override
    protected String translateColDefineSql(String colDefineSql) {
        colDefineSql = S.replaceEach(colDefineSql,
                new String[]{" tinyint(1)", " tinyint", "varchar(", " datetime ", " bigint ", " json ", " JSON "},
                new String[]{" NUMBER(1)", " NUMBER(1)", "VARCHAR2(", " TIMESTAMP ", " NUMBER(20) ", " VARCHAR2(1000) ", " VARCHAR2(1000) "}
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
                return S.substringBefore(prefix, " NOT NULL ") + " DEFAULT " + defValue + " NOT NULL " + S.substringAfter(prefix, " NOT NULL ")
                        + suffix;
            }
            else if(S.contains(prefix, " NULL ")) {
                return S.substringBefore(prefix, " NULL ") + " DEFAULT " + defValue + " NULL " + S.substringAfter(prefix, " NULL ")
                        + suffix;
            }
        }
        return escapeKeyword(colDefineSql);
    }

    @Override
    protected String escapeKeyword(String input) {
        return S.replace(input, "`", "");
    }

}
