package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/**
 * 单位Entity
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
@Data
public class Organization extends BaseEntity {
    private static final long serialVersionUID = -5889309041570465909L;

    @TableField
    private Long parentId;

    @TableField
    private String name;

    @TableField
    private String telphone;

    @TableField
    private String address;

}
