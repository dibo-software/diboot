package com.diboot.iam.entity.route;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * meta扩展
 * @author : uu
 * @version : v1.0
 * @Date 2022/5/18  11:22
 */
@Getter@Setter@Accessors(chain = true)
public class RouteMeta implements Serializable {

    private static final long serialVersionUID = -1923794974320513969L;
    /**
     * 标题
     */
    private String title;
    /**
     * icon图标
     */
    private String icon;
    /**
     * 模型
     */
    private String model;
    /**
     * 模块
     */
    private String module;
    /**
     * 组件地址
     */
    private String componentPath;
    /**
     * 路由重定向地址
     */
    private String redirectPath;
    /**
     * 外链或iframe地址
     */
    private String url;

    /**
     * 是否是iframe
     */
    private Boolean iframe;
    /**
     * 权限
     */
    private List<String> permissions;
    /**
     * 排序
     */
    private Long sort;
    /**
     * tab是否保存固定路由
     */
    private Boolean affixTab;
    /**
     * 是否忽略认证
     */
    private Boolean ignoreAuth;
    /**
     * 是否缓存
     */
    private Boolean keepAlive;
    /**
     * 当前路由是否在菜单中隐藏
     */
    private Boolean hidden;
    /**
     * 是否显示背景色
     */
    private Boolean hollow;
    /**
     * 是否隐藏页面底部
     */
    private Boolean hideFooter;
    /**
     * 是否显示边框
     */
    private Boolean borderless;
    /**
     * 是否打开新窗口
     */
    private Boolean openNewWindow;
    /**
     * 是否隐藏面包屑
     */
    private Boolean hideBreadcrumb;
}
