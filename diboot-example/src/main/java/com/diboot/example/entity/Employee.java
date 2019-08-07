package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 员工
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/5
 */
@Data
@TableName(value = "if_employee")
public class Employee extends BaseEntity {

    @TableField
    private String number;//工号

    @TableField
    private String account;//用户名（账号）

    @TableField
    private String name;//员工名

    @TableField
    private String gender;//性别

    @TableField
    private Date birthday;//出生日期

    @TableField
    private String cellphone;//手机号

    @TableField
    private String telephone;//办公电话

    @TableField
    private String email;//邮箱

    @TableField
    private String wechat;//微信

    @TableField
    private String comment;//备注
}
