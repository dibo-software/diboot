package com.diboot.shiro.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/**
 * @author Yangz
 * @version v2.0
 * @date 2019/6/6
 */
@Data
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 6415425761451054775L;

    @TableField
    private String userType;

    @TableField
    private Long userId;

    @TableField
    private Long roleId;

}
