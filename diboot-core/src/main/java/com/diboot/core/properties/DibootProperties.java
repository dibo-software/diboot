package com.diboot.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 核心配置
 *
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  14:12
 */
@EnableConfigurationProperties(DibootProperties.class)
@ConfigurationProperties(prefix = "diboot.web")
@Component
public class DibootProperties {

    /**
     * 异常页面配置
     */
    private ExceptionProperties exception = new ExceptionProperties();

    public ExceptionProperties getException() {
        return exception;
    }

    public void setException(ExceptionProperties exception) {
        this.exception = exception;
    }

    /**
     * 错误页面配置
     *
     * @author : wee
     * @version : v1.0
     * @Date 2019-07-11  14:16
     */
    public static class ExceptionProperties {

        /**
         * 响应状态指定页面
         */
        private Map<HttpStatus, String> page;

        public Map<HttpStatus, String> getPage() {
            return page;
        }

        public void setPage(Map<HttpStatus, String> page) {
            this.page = page;
        }
    }
}
