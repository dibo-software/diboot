package com.diboot.component.file.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author diboot
 */
@Data
@ConfigurationProperties(prefix = "diboot.component.file")
public class FileProperties {

    /**
     * 是否初始化，默认true自动安装SQL
     */
    private boolean initSql = true;
}
