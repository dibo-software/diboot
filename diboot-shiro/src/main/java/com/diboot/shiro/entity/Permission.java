package com.diboot.shiro.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Data
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 7713768302925692987L;

    @TableField
    private Long menuId;

    @TableField
    private String menuCode;

    @TableField
    private String menuName;

    @TableField
    private String permissionCode;

    @TableField
    private String permissionName;

    /**此处覆盖了父类的属性，初始化权限的时候需要设置该值，父类默认不设置*/
//    @TableField
//    private boolean deleted = false;

}
