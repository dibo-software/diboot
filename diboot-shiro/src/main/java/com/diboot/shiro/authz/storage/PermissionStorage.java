package com.diboot.shiro.authz.storage;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.diboot.core.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Data
@Builder
public class PermissionStorage implements Serializable {

    private static final long serialVersionUID = 147840093814049689L;

    /***
     * 默认逻辑删除标记，deleted=0有效
     */
    private boolean deleted = false;

    /**菜单Id*/
    private Long menuId;

    /**菜单编码*/
    private String menuCode;

    /**菜单名称*/
    private String menuName;

    /**权限编码*/
    private String permissionCode;

    /**权限名称*/
    private String permissionName;

    /**是否高优先级：方法上的优先级高于类上，同时出现以方法为准*/
    private boolean highPriority;

    private Long id;

}
