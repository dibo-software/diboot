package com.diboot.core.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * diboot-core配置文件类
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/08/06
 */
@ConfigurationProperties(prefix = "diboot.core")
public class CoreProperties {

    /**
     * 是否初始化，默认true自动安装SQL
     */
    private boolean initSql = true;

    public boolean isInitSql() {
        return initSql;
    }

    public void setInitSql(boolean initSql) {
        this.initSql = initSql;
    }
}
