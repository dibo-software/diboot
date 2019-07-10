package com.diboot.components.msg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseExtEntity;
import lombok.Data;

@Data
public class MessageTemplate extends BaseExtEntity {

    @TableField
    private String type;

    @TableField
    private String code;

    @TableField
    private String msgType;

    @TableField
    private String businessType;

    @TableField
    private Long businessId;

    @TableField
    private String title;

    @TableField
    private String msgTitle;

    @TableField
    private String content;

}
