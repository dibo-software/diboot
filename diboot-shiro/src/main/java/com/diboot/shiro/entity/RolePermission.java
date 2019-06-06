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
public class RolePermission extends BaseEntity {

    private static final long serialVersionUID = -710604862356186012L;

    @TableField
    private Long roleId;

    @TableField
    private Long permissionId;

}
