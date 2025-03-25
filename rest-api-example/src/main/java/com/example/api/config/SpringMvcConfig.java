package com.example.api.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.List;

/**
 * Spring 相关配置
 *
 * @author www.dibo.ltd
 * @version v1.0
 * @date 2020/10/23
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.example"})
@MapperScan(basePackages = {"com.example.api.mapper"})
public class SpringMvcConfig {

}