package com.diboot.component.msg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseExtEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Message extends BaseExtEntity {

    @TableField
    private String type;

    @TableField
    private Long templateId;

    @TableField
    private String businessType;

    @TableField
    private Long businessId;

    @TableField
    private String sender;

    @TableField
    private String receiver;

    @TableField
    private String title;

    @TableField
    private String content;

    @TableField
    private String url;

    @TableField
    private String status;

    @TableField
    private Date scheduleTime;

    @TableField
    private String response;

    @TableField
    private Long createBy;

}
