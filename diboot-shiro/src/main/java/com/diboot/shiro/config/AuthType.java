package com.diboot.shiro.config;

import lombok.Data;

/***
 * 认证方式
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public enum AuthType {

    USERNAME_PASSWORD(1, true, "账号密码"),
    WX_MP(2, false, "公众号"),
    WX_CP(3, false, "企业微信");

    private AuthType(int code, boolean requirePassword, String label){
        this.code = code;
        this.requirePassword = requirePassword;
        this.label = label;
    }

    private int code;
    private boolean requirePassword;
    private String label;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isRequirePassword() {
        return requirePassword;
    }

    public void setRequirePassword(boolean requirePassword) {
        this.requirePassword = requirePassword;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
