package com.diboot.shiro.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
    private String menuCode;

    @TableField
    private String menuName;

    @TableField
    private String permissionCode;

    @TableField
    private String permissionName;

}
