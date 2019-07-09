package com.diboot.components.msg.entity;

import com.diboot.components.msg.config.MsgCons;
import lombok.Data;

/*
*
* */
@Data
public class EmailMessage extends BaseMessage {

    private String receiverName;

    private String[] ccEmails;

    private String title;

    private String[] filePaths;

}
