package com.diboot.shiro.authz.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限存储环境
 * @author : wee
 * @version : v todo
 * @Date 2019-06-25  17:08
 */
@Getter
@AllArgsConstructor
public enum  EnvEnum {

    PROD("prod", "生产环境"),

    DEV("dev","开发环境");

    private String env;

    private String desc;

}
