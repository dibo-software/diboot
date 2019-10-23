package com.diboot.shiro.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Data
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 7713768302925692987L;

    @TableField
    private String menuCode;

    @TableField
    private String menuName;

    @TableField
    private String permissionCode;

    @TableField
    private String permissionName;

    /**
     * 权限所属应用
     */
    @TableField
    private String application;

    //菜单下的各种权限资源
    @TableField(exist = false)
    private List<Permission> permissionList;

    //某角色是否拥有该权限
    @TableField(exist = false)
    private boolean own = false;

    /**
     * 指定当前是否选中，更新时显示菜单选择状态会用到
     */
    @TableField(exist = false)
    private boolean checked = false;

    /**
     * 是否全选，更新时显示菜单选择状态会用到
     */
    @TableField(exist = false)
    private boolean indeterminate = false;

    /**
     * 角色id
     */
    @TableField(exist = false)
    private Long roleId;

}
