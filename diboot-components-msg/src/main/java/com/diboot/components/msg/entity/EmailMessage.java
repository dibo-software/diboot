package com.diboot.components.msg.entity;

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
