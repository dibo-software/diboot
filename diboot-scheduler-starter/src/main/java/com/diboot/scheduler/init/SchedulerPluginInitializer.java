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
package com.diboot.scheduler.init;

import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.SqlFileInitializer;
import com.diboot.core.vo.DictionaryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 定时任务插件
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2020-11-27
 */
@Slf4j
@Order(951)
@Component
@ConditionalOnProperty(prefix = "diboot", name = "init-sql", havingValue = "true")
public class SchedulerPluginInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库表是否已存在
        String initDetectSql = "SELECT id FROM dbt_schedule_job WHERE id='0'";
        if(!SqlFileInitializer.checkSqlExecutable(initDetectSql)){
            log.info("diboot-scheduler 初始化SQL ...");
            // 执行初始化SQL
            SqlFileInitializer.initBootstrapSql(this.getClass(), "scheduler");
            // 插入相关数据：Dict等
            insertInitData();
            log.info("diboot-scheduler 初始化SQL完成.");
        }
    }

    /**
     * 插入初始化数据
     */
    private void insertInitData() {
        // 插入scheduler组件所需的数据字典
        DictionaryService dictionaryService = ContextHolder.getBean(DictionaryService.class);
        if (dictionaryService != null && !dictionaryService.exists(Dictionary::getType, "INIT_STRATEGY")) {
            final String[] DICT_INIT_DATA = {
                    "{\"type\":\"INIT_STRATEGY\", \"itemName\":\"定时任务初始化策略\", \"description\":\"定时任务初始化策略定义\", \"isEditable\":true, \"children\":[{\"itemName\":\"周期执行\", \"itemNameI18n\":\"Dictionary.INIT_STRATEGY.DO_NOTHING\", \"itemValue\":\"DO_NOTHING\", \"sortId\":1},{\"itemName\":\"立即执行一次，并周期执行\", \"itemNameI18n\":\"Dictionary.INIT_STRATEGY.EFIRE_AND_PROCEED2\", \"itemValue\":\"EFIRE_AND_PROCEED2\", \"sortId\":2},{\"itemName\":\"超期立即执行，并周期执行\", \"itemNameI18n\":\"Dictionary.INIT_STRATEGY.IGNORE_MISFIRES\", \"itemValue\":\"IGNORE_MISFIRES\", \"sortId\":3}]}"
            };
            // 插入数据字典
            for (String dictJson : DICT_INIT_DATA) {
                DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
                dictionaryService.createDictAndChildren(dictVo);
            }
        }
    }
}
