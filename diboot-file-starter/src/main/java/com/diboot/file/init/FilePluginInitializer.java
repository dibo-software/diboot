/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.file.init;

import com.diboot.core.util.SqlFileInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 组件初始化
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/28
 */
@Order(921)
@Slf4j
@Component
@ConditionalOnProperty(prefix = "diboot", name = "init-sql", havingValue = "true")
public class FilePluginInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库文件记录表是否已存在
        String initDetectSql = "SELECT id FROM dbt_file_record WHERE id='0'";
        if(!SqlFileInitializer.checkSqlExecutable(initDetectSql)){
            SqlFileInitializer.initBootstrapSql(this.getClass(), "file");
            log.info("diboot-file 初始化SQL完成.");
        }
    }
}
