package com.diboot.shiro.authz.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 系统配置
 * @author : wee
 * @version : v2.0
 * @Date 2019-10-17  15:49
 */
@Getter
@Setter
public class SystemParamConfig {

    /**
     * 应用名称
     */
    private String application;

    /**
     * 系统权限
     */
    private List<String> auth;
}
