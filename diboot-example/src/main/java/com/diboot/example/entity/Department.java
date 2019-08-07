package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/**
 * 部门
 * @author Mazhicheng
 * @version v2.0
 * @date 2018/12/27
 */
@Data
@TableName(value = "if_department")
public class Department extends BaseEntity {
    private static final long serialVersionUID = -4849732665419794547L;

    @TableField
    private Long parentId;//上级部门

    @TableField
    private Long orgId;//所属公司

    @TableField
    private String name;//部门名称

    @TableField
    private String shortName;//部门简称

    @TableField
    private String number;//部门编号

    @TableField
    private String charger;//负责人

    @TableField
    private String telephone;//联系电话

    @TableField
    private String email;//电子邮箱

    @TableField
    private String fax;//传真

    @TableField
    private String comment;//备注

}