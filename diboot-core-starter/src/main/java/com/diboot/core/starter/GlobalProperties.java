package com.diboot.core.starter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * diboot全局配置文件类
 * <p>
 * 优先级高于其他模块配置
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/26
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "diboot.global")
public class GlobalProperties {

    /**
     * 全局初始化SQL，默认true自动安装SQL
     */
    private boolean initSql = true;

}
