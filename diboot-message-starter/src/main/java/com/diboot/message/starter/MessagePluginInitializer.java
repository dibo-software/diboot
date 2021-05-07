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
package com.diboot.message.starter;

import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.JSON;
import com.diboot.core.util.SqlFileInitializer;
import com.diboot.core.vo.DictionaryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 组件初始化
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/28
 * @Copyright © diboot.com
 */
@Slf4j
@Component
@Order(950)
public class MessagePluginInitializer implements ApplicationRunner {

    @Autowired
    private MessageProperties messageProperties;

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库字典是否已存在
        if (messageProperties.isInitSql()) {
            // 初始化SCHEMA
            SqlFileInitializer.init(environment);
            String initDetectSql = "SELECT id FROM ${SCHEMA}.message_template WHERE id=0";
            if (SqlFileInitializer.checkSqlExecutable(initDetectSql) == false) {
                SqlFileInitializer.initBootstrapSql(this.getClass(), environment, "message");
                // 插入相关数据：Dict等
                insertInitData();
                log.info("diboot-message 初始化SQL完成.");
            }
        }
    }

    /**
     * 插入初始化数据
     */
    private void insertInitData() {
        // 插入iam组件所需的数据字典
        String[] DICT_INIT_DATA = {
                "{\"type\":\"MESSAGE_STATUS\", \"itemName\":\"消息状态\", \"description\":\"message消息状态\", \"children\":[{\"itemName\":\"发送中\", \"itemValue\":\"SENDING\", \"sortId\":1},{\"itemName\":\"发送异常\", \"itemValue\":\"EXCEPTION\", \"sortId\":2},{\"itemName\":\"已送达\", \"itemValue\":\"DELIVERY\", \"sortId\":3},{\"itemName\":\"未读\", \"itemValue\":\"UNREAD\", \"sortId\":4},{\"itemName\":\"已读\", \"itemValue\":\"READ\", \"sortId\":5}]}",
                "{\"type\":\"MESSAGE_CHANNEL\", \"itemName\":\"发送通道\", \"description\":\"message发送通道\", \"children\":[{\"itemName\":\"站内信\", \"itemValue\":\"WEBSOCKET\", \"sortId\":1},{\"itemName\":\"短信\", \"itemValue\":\"TEXT_MESSAGE\", \"sortId\":2},{\"itemName\":\"邮件\", \"itemValue\":\"EMAIL\", \"sortId\":3}]}"
        };
        // 插入数据字典
        for (String dictJson : DICT_INIT_DATA) {
            DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
            ContextHelper.getBean(DictionaryService.class).createDictAndChildren(dictVo);
        }
        DICT_INIT_DATA = null;
    }
}
