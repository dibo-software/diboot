package com.diboot.component.file.starter;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@EnableConfigurationProperties(FileProperties.class)
@ComponentScan(basePackages = {"com.diboot.component.file"})
@MapperScan(basePackages = {"com.diboot.component.file.mapper"})
public class FileAutoConfiguration {

    @Autowired
    FileProperties fileProperties;

    @Autowired
    Environment environment;

    @Bean
    @ConditionalOnMissingBean(FilePluginManager.class)
    public FilePluginManager filePluginManager(){
        // 初始化SCHEMA
        SqlHandler.init(environment);
        FilePluginManager pluginManager = new FilePluginManager() {};
        // 检查数据库字典是否已存在
        if(fileProperties.isInitSql() && SqlHandler.checkIsFileTableExists() == false){
            SqlHandler.initBootstrapSql(pluginManager.getClass(), environment, "file");
        }
        return pluginManager;
    }
}
