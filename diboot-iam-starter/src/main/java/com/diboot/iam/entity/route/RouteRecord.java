package com.diboot.iam.entity.route;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 路由记录
 * @author : uu
 * @version : v1.0
 * @Date 2022/5/18  11:21
 */
@Getter@Setter@Accessors(chain = true)
public class RouteRecord implements Serializable {
    private static final long serialVersionUID = -339401420633327672L;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 菜单类型
     */
    private String type;

    /**
     * 菜单地址
     */
    private String path;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * meta数据
     */
    private RouteMeta meta;

    /**
     * 子路由
     */
    private List<RouteRecord> children;
}
