package com.diboot.shiro.authz.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限入库配置文件
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-27  10:16
 */
@Data
@ConfigurationProperties(prefix = "diboot.shiro.auth")
public class AuthorizationProperties {

    /**设置权限存储的环境：其中开发环境权限不会替换删除，测试和生产会替换删除*/
    private EnvEnum env = EnvEnum.DEV;

    /**是否开启存储权限*/
    private boolean storage = false;

    /**具有所有权限的角色*/
    private List<String> hasAllPermissionsRoleList;

    @Getter
    @AllArgsConstructor
    public enum  EnvEnum {

        /**生产环境*/
        PROD("prod"),

        /**测试环境*/
        TEST("test"),

        /**开发环境*/
        DEV("dev");

        private String env;
    }
}
