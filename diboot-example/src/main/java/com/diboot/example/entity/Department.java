package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseExtEntity;
import lombok.Data;

/**
 * 定时任务
 * @author Mazhicheng
 * @version v2.0
 * @date 2018/12/27
 */
@Data
public class Department extends BaseExtEntity {
    private static final long serialVersionUID = -4849732665419794547L;

    @TableField
    private Long parentId;

    @TableField
    private Long orgId;

    @TableField
    private String name;

    @TableField
    private String code;

}