package com.diboot.shiro.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 * 认证方式
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Getter
@AllArgsConstructor
public enum AuthType {

    USERNAME_PASSWORD(1, true, "账号密码"),
    WX_MP(2, false, "公众号"),
    WX_CP(3, false, "企业微信");

    private int code;
    private boolean requirePassword;
    private String label;
}
