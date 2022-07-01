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
package diboot.message.test.config;

import com.diboot.message.channel.SimpleEmailChannel;
import com.diboot.message.entity.BaseUserVariables;
import com.diboot.message.service.MessageService;
import com.diboot.message.service.impl.MessageServiceImpl;
import diboot.message.test.channel.SMSChannel;
import diboot.message.test.variable.MyVariableObj;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/***
 * Spring配置文件
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/6/10
 */
@TestConfiguration
@ComponentScan(basePackages={"com.diboot.message"})
@MapperScan({"com.diboot.message.mapper"})
public class SpringMvcConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(SpringMvcConfig.class);

    @Bean
    public MessageService messageService() {
        return new MessageServiceImpl(
                Arrays.asList(new SimpleEmailChannel(), new SMSChannel()),
                Arrays.asList(BaseUserVariables.class, MyVariableObj.class)
        );
    }

}