package com.diboot.file.example;

import com.diboot.component.file.starter.FileProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Servlet初始化
 * @author mazc@dibo.ltd
 */
@EnableAutoConfiguration
@EnableConfigurationProperties(FileProperties.class)
@Configuration
@ComponentScan(basePackages = {"com.diboot.file"})
public class ServletInitializer{

}
