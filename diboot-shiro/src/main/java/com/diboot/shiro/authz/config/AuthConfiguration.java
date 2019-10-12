package com.diboot.shiro.authz.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 权限配置
 * @author : wee
 * @version : v2.0
 * @Date 2019-06-27  10:30
 */
@Getter
@Setter
public class AuthConfiguration {

    /**
     * 缓存相关
     */
    private AuthConfiguration.Cache cache = new Cache();

    /**
     * 权限相关
     */
    private AuthConfiguration.Auth auth = new Auth();

    /**
     * 忽略认证的url,使用，分割
     */
    private String ignoreAuthUrls;

    /**
     * 错误统一跳转路径
     */
    private String errorUrl = "/error";


    /**
     * 权限缓存相关
     */
    @Getter
    @Setter
    public static class Cache {
        /**
         * 是否开启权限缓存：默认false
         */
        private boolean permissionCachingEnabled = false;

        /**
         * 缓存方式：默认内存缓存
         */
        private CacheWay cacheWay = CacheWay.MEMORY;

        /**
         * 缓存方式
         * <p>
         *  当前提供本地缓存
         * </p>
         */
        @Getter
        @AllArgsConstructor
        public enum CacheWay {
            /**
             * 内存缓存
             */
            MEMORY,
            /**
             * redis缓存： TODO 尚未实现，暂不可用
             */
            @Deprecated
            REDIS;
        }
    }

    /**
     * 权限相关
     *
     */
    @Getter
    @Setter
    public static class Auth {

        /**
         * 设置权限存储的环境：其中开发环境权限不会替换删除，测试和生产会替换删除
         */
        private EnvEnum env = EnvEnum.DEV;

        /**
         * 是否开启存储权限
         */
        private boolean storage = false;

        /**
         * 具有所有权限的角色
         */
        private List<String> hasAllPermissionsRoleList;

        @Getter
        @AllArgsConstructor
        public enum EnvEnum {

            /**
             * 生产环境
             */
            PROD("prod"),

            /**
             * 测试环境
             */
            TEST("test"),

            /**
             * 开发环境
             */
            DEV("dev");

            private String env;
        }
    }



}
