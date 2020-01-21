package com.diboot.iam.config;

import com.diboot.core.config.BaseConfig;

/**
 * IAM数据字典等常量定义
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/19
 */
public class Cons extends com.diboot.core.config.Cons {

    /**
     * 数据字典类型定义
     */
    public static enum DICTTYPE{
        DATA_PERMISSION_TYPE,
        AUTH_TYPE,
        ACCOUNT_STATUS,
        USER_STATUS,
        ORG_TYPE,
        PERMISSION_TYPE,
        POSITION_GRADE,
        GENDER
    }

    /**
     * 字典编码 - 数据权限类型
     */
    public static enum DICTCODE_DATA_PERMISSION_TYPE{
        INDIVIDUAL,
        DEPT,
        DEPT_MEMS,
        ALL
    }

    /**
     * 字典编码 - 认证方式
     */
    public static enum DICTCODE_AUTH_TYPE{
        PWD,
        WX_MP,
        WX_CP
    }

    /**
     * 字典编码 - 账号状态
     */
    public static enum DICTCODE_ACCOUNT_STATUS{
        A,
        L,
        I
    }

    /**
     * 字典编码 - 用户状态
     */
    public static enum DICTCODE_USER_STATUS{
        A,
        L,
        I
    }

    /**
     * 字典编码 - 组织类型
     */
    public static enum DICTCODE_ORG_TYPE{
        COMP,
        DEPT
    }

    /**
     * 字典编码 - 权限类型
     */
    public static enum DICTCODE_PERMISSION_TYPE{
        MENU,
        OPERATION,
        OTHER
    }

    /**
     * 应用
     */
    public static final String APPLICATION = BaseConfig.getProperty("diboot.iam.application", "MS");

    /**
     * 超级管理员
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
}