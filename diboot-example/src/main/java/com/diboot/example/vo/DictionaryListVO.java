package com.diboot.example.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DictionaryListVO {

    private Long id;

    private String type;

    private String itemName;

    private String itemValue;

    private String comment;

    private Date createTime;
}
