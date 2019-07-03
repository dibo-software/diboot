package com.diboot.example.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SysUserListVO {

    private Long id;

    private String username;

    private String phone;

    private String statusLabel;

    private Date createTime;

    private List<String> roleNameList;
}
