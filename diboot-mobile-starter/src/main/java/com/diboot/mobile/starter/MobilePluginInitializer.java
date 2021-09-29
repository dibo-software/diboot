/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.mobile.starter;

import com.diboot.core.util.SqlFileInitializer;
import com.diboot.iam.starter.IamAutoConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 移动端组件相关的初始化
 * @author mazc@dibo.ltd
 * @version v2.3.1
 * @date 2021/09/14
 */
@Slf4j
@Component
@Order(930)
@AutoConfigureAfter({IamAutoConfig.class})
@ConditionalOnProperty(prefix = "diboot.global", name = "init-sql", havingValue = "true", matchIfMissing = true)
public class MobilePluginInitializer implements ApplicationRunner {
    @Autowired
    private MobileProperties mobileProperties;
    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库字典是否已存在
        if(mobileProperties.isInitSql()){
            SqlFileInitializer.init(environment);
            // 验证SQL
            String initDetectSql = "SELECT id FROM ${SCHEMA}.iam_member WHERE id=0";
            if(SqlFileInitializer.checkSqlExecutable(initDetectSql) == false){
                log.info("diboot-mobile 初始化SQL ...");
                // 执行初始化SQL
                SqlFileInitializer.initBootstrapSql(this.getClass(), environment, "mobile");
                log.info("diboot-mobile 初始化SQL完成.");
            }
        }
    }
}
