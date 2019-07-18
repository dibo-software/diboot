package com.diboot.core.properties;

import com.diboot.core.enumerate.ErrorPageEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 核心配置
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  14:12
 */
//@Configuration
@EnableConfigurationProperties(DibootProperties.class)
@ConfigurationProperties(prefix = "diboot.web")
@Component
public class DibootProperties {

    private ErrorProperties error = new ErrorProperties();

    public ErrorProperties getError() {
        return error;
    }

    public void setError(ErrorProperties error) {
        this.error = error;
    }

    /**
     * 错误页面配置
     * @author : wee
     * @version : v1.0
     * @Date 2019-07-11  14:16
     */
    public static class ErrorProperties {

        private Map<ErrorPageEnum, String> page;

        public Map<ErrorPageEnum, String> getPage() {
            return page;
        }

        public void setPage(Map<ErrorPageEnum, String> page) {
            this.page = page;
        }
    }
}
