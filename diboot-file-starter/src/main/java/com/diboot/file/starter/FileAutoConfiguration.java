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
package com.diboot.file.starter;

import com.diboot.core.config.Cons;
import com.diboot.core.starter.SqlHandler;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Slf4j
@Configuration
@EnableConfigurationProperties(FileProperties.class)
@ComponentScan(basePackages = {"com.diboot.file"})
@MapperScan(basePackages = {"com.diboot.file.mapper"})
@Order(11)
public class FileAutoConfiguration {

    @Autowired
    FileProperties fileProperties;

    @Autowired
    Environment environment;

    @Bean
    @ConditionalOnMissingBean(FilePluginManager.class)
    public FilePluginManager filePluginManager() {
        // 初始化SCHEMA
        SqlHandler.init(environment);
        FilePluginManager pluginManager = new FilePluginManager() {
        };
        // 检查数据库字典是否已存在
        if (fileProperties.isInitSql()) {
            String initDetectSql = "SELECT uuid FROM ${SCHEMA}.upload_file WHERE uuid='xyz'";
            if(SqlHandler.checkSqlExecutable(initDetectSql) == false){
                SqlHandler.initBootstrapSql(pluginManager.getClass(), environment, "file");
                log.info("diboot-file 初始化SQL完成.");
            }
        }
        return pluginManager;
    }

    /**
     * 需要文件上传，开启此配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MultipartResolver.class)
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver bean = new CommonsMultipartResolver();
        bean.setDefaultEncoding(Cons.CHARSET_UTF8);
        bean.setMaxUploadSize(fileProperties.getMaxUploadSize());
        return bean;
    }
}
