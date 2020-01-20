package com.diboot.iam.starter;

import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 认证相关的配置参数
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Getter @Setter
@ConfigurationProperties(prefix = "diboot.iam")
public class IamBaseProperties {
    /**
     * 应用程序
     */
    private String application;

    /**
     * jwt header key
     */
    private String jwtHeaderKey = "authtoken";

    /**
     * jwt 签名key
     */
    private String jwtSignkey = "Diboot";

    /**
     * jwt token过期分钟数
     */
    private int jwtTokenExpiresMinutes = 60;

    /**
     * 匿名的url，以,逗号分隔
     */
    private String anonUrls;
    /**
     * 是否初始化SQL
     */
    private boolean initSql = true;
    /**
     * 是否开启权限自动更新
     */
    private boolean enablePermissionUpdate = true;

    /**
     * 缓存Manager类
     */
    private String cacheManagerClass = MemoryConstrainedCacheManager.class.getName();

}
