package com.diboot.example.enums;

import com.diboot.shiro.enums.IUserType;

/**
 * 系统用户枚举类
 *
 * @author : wee
 * @version : v2.0
 * @Date 2019-10-14  18:15
 */
public enum  UserTypeEnum implements IUserType {

    SYS_USER("MS_USER", "系统用户"),
    WX_MP_USER("WX_MP", "服务号用户"),
    ;

    /**
     * 用户类型
     */
    private String type;

    /**
     * 描述
     */
    private String description;

    UserTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }


    @Override
    public String getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }}
