package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Data
public class User extends BaseEntity {
    private static final long serialVersionUID = 3050761344045195972L;

    @TableField
    private Long departmentId;

    @TableField
    private String username;

    @TableField
    private String gender;

}
