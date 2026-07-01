/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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

import java.util.Arrays;
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

    @Test
    public void testMysqlTranslate() {
        String createTableSql = "CREATE TABLE `dbtlc_model_event_trigger` (\n" +
                "     `id` varchar(64) NOT NULL COMMENT 'ID',\n" +
                "     `tenant_id` varchar(64) DEFAULT '0' COMMENT '租户',\n" +
                "     `model_id` varchar(64) DEFAULT NULL COMMENT '模型ID',\n" +
                "     `name` varchar(100) NOT NULL COMMENT '名称',\n" +
                "     `event_type` varchar(100) NOT NULL COMMENT '事件类型',\n" +
                "     `conditions` varchar(2000) DEFAULT NULL COMMENT '执行条件',\n" +
                "     `handlers` varchar(1000) DEFAULT NULL COMMENT '动作处理',\n" +
                "     `description` varchar(512) DEFAULT NULL COMMENT '描述',\n" +
                "     `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',\n" +
                "     `create_by` varchar(64) DEFAULT '0' COMMENT '创建人',\n" +
                "     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "     `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                "     PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据触发执行'";

        //String addColumnSql = "ALTER TABLE dbtlc_process_config ADD COLUMN `auth_scope_groups` varchar(512) DEFAULT NULL";

        List<String> sqlStatements = Arrays.asList(createTableSql);

        List<String> newLines = new PostgresSqlTranslator().translate(sqlStatements);
        newLines.forEach(System.out::println);
    }

}