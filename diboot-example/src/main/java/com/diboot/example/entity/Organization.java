package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 组织Entity
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
@Data
@TableName(value = "if_organization")
public class Organization extends BaseEntity {
    private static final long serialVersionUID = -5889309041570465909L;

    // industry字段的关联元数据
    public static final String DICT_INDUSTRY = "INDUSTRY";

    @TableField
    private Long parentId;//上级组织

    @TableField
    private String industry;//所属行业

    @TableField
    private String name;//组织名称

    @TableField
    private String shortName;//组织简称

    @TableField
    private String code;//组织编码

    @TableField
    private Date establishTime;//成立时间

    @TableField
    private String charger;//负责人

    @TableField
    private String telephone;//联系电话

    @TableField
    private String email;//电子邮件

    @TableField
    private String fax;//传真

    @TableField
    private String area;//省市区

    @TableField
    private String address;//详细地址

    @TableField
    private String postalCode;//邮编

    @TableField
    private String officialWebsite;//官网

    @TableField
    private String businessScope;//经营范围

}
