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
package diboot.core.test.util;

import com.diboot.core.util.SqlFileInitializer;
import com.diboot.core.util.sql.PostgresSqlTranslator;
import org.junit.Test;

import java.util.List;

/**
 * SQL方言转换工具类测试
 * @author mazc@dibo.ltd
 * @version 3.2.0
 * @date 2024/01/02
 */
public class SqlTranslateTest {

    @Test
    public void testPostgresTranslate() {
        String mysqlFilePath = "unittest-mysql.sql";
        List<String> sqlStatements = SqlFileInitializer.readLinesFromResource(this.getClass(), mysqlFilePath);
        sqlStatements = SqlFileInitializer.extractSqlStatements(sqlStatements);
        List<String> newLines = new PostgresSqlTranslator().translate(sqlStatements);
        newLines.forEach(System.out::println);
    }

}