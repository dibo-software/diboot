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
package com.diboot.core.starter;

import com.diboot.core.util.SqlFileInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 组件初始化
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/28
 */
@Slf4j
@Component
@Order(910)
@ConditionalOnProperty(prefix = "diboot.global", name = "init-sql", havingValue = "true", matchIfMissing = true)
public class CorePluginInitializer implements ApplicationRunner {
    @Autowired
    private Environment environment;

    @Autowired
    private CoreProperties coreProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化SCHEMA
        SqlFileInitializer.init(environment);
        // 检查数据库字典是否已存在
        if (coreProperties.isInitSql()) {
            String initDetectSql = "SELECT id FROM dictionary WHERE id=0";
            if (SqlFileInitializer.checkSqlExecutable(initDetectSql) == false) {
                SqlFileInitializer.initBootstrapSql(this.getClass(), environment, "core");
                log.info("diboot-core 初始化SQL完成.");
            }
        }
    }
}
