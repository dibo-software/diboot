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

import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 组件初始化
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/11/28
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
@ComponentScan(basePackages = {"com.diboot.file"})
@MapperScan(basePackages = {"com.diboot.file.mapper"})
public class FileAutoConfiguration {

    @Autowired
    private FileProperties fileProperties;

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
        long maxUploadSize = fileProperties.getMaxUploadSize();
        // 兼容 servlet 配置参数
        String servletMaxUploadSize = BaseConfig.getProperty("spring.servlet.multipart.max-request-size");
        if(V.notEmpty(servletMaxUploadSize)){
            if(S.endsWithIgnoreCase(servletMaxUploadSize, "MB")){
                maxUploadSize = Long.parseLong(S.removeIgnoreCase(servletMaxUploadSize, "MB"));
                maxUploadSize = maxUploadSize * 1024 * 1024;
            }
            else if(S.endsWithIgnoreCase(servletMaxUploadSize, "KB")){
                maxUploadSize = Long.parseLong(S.removeIgnoreCase(servletMaxUploadSize, "KB"));
                maxUploadSize = maxUploadSize * 1024;
            }
            else{
                maxUploadSize = Long.parseLong(servletMaxUploadSize);
            }
        }
        bean.setMaxUploadSize(maxUploadSize);
        return bean;
    }
}
