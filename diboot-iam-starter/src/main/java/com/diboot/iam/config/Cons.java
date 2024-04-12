/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.config;

import com.diboot.core.config.BaseConfig;
import com.diboot.iam.shiro.IamAuthorizingRealm;

import java.util.Arrays;
import java.util.List;

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
    public enum DICTTYPE{
        DATA_PERMISSION_TYPE,
        AUTH_TYPE,
        ACCOUNT_STATUS,
        USER_STATUS,
        ORG_TYPE,
        RESOURCE_TYPE,
        POSITION_GRADE,
        GENDER,
        RESOURCE_CODE
    }

    /**
     * 字典编码 - 数据权限类型
     */
    public enum DICTCODE_DATA_PERMISSION_TYPE{
        SELF,
        SELF_AND_SUB,
        DEPT,
        DEPT_AND_SUB,
        ALL
    }

    /**
     * 字典编码 - 认证方式
     */
    public enum DICTCODE_AUTH_TYPE{
        PWD,
        SSO,
        OAuth2,
        CAS_SERVER,
        WX_MP,
        WX_CP,
        OTHER
    }

    /**
     * 字典编码 - 账号状态
     */
    public enum DICTCODE_ACCOUNT_STATUS{
        A,
        L,
        I
    }

    /**
     * 字典编码 - 用户状态
     */
    public enum DICTCODE_USER_STATUS{
        A,
        L,
        I
    }

    /**
     * 字典编码 - 权限状态
     */
    public enum DICTCODE_RESOURCE_STATUS{
        A,
        I
    }

    /**
     * 字典编码 - 组织类型
     */
    public enum DICTCODE_ORG_TYPE{
        COMP,
        DEPT
    }

    /**
     * 字典编码 - 权限类型
     */
    public enum DICTCODE_PERMISSION_TYPE{
        MENU,
        OPERATION,
        OTHER
    }

    /**
     * 前端权限类型
     */
    public enum RESOURCE_PERMISSION_DISPLAY_TYPE{
        MODULE,
        MENU,
        PERMISSION,
        IFRAME,
        OUTSIDE_URL,
        CATALOGUE
    }

    /**
     * 菜单类别列表
     */
    public static final List<String> MENU_CATEGORY_LIST = Arrays.asList(
            RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name(),
            RESOURCE_PERMISSION_DISPLAY_TYPE.IFRAME.name(),
            RESOURCE_PERMISSION_DISPLAY_TYPE.OUTSIDE_URL.name(),
            RESOURCE_PERMISSION_DISPLAY_TYPE.CATALOGUE.name()
    );

    /**
     * 应用
     */
    public static final String APPLICATION = BaseConfig.getProperty("diboot.iam.application", "MS");

    /**
     * 超级管理员
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * 租户管理员
     */
    public static final String ROLE_TENANT_ADMIN = "TENANT_ADMIN";

    public static final String AUTHENTICATION_CAHCE_NAME = IamAuthorizingRealm.class.getName() + ".authenticationCache";

    public static final String AUTHORIZATION_CAHCE_NAME = IamAuthorizingRealm.class.getName() + ".authorizationCache";

    /**
     * 验证码
     */
    public static final String CACHE_CAPTCHA = "CAPTCHA";

    /**
     * token-用户信息 缓存
     */
    public static final String CACHE_TOKEN_USERINFO = "TOKEN_USERINFO";

}