package com.diboot.iam.starter;

import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Plus配置
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/19
 */
@Configuration
@MapperScan(basePackages={"com.diboot.iam.mapper"})
public class IamMybatisPlusConfig {

    /**
     * Mybatis-plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        return paginationInterceptor;
    }

    /***
     * 逻辑删除注入
     * @return
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new DefaultSqlInjector();
    }

}
