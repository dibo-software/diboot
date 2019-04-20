package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 员工Entity
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Data
public class Employee extends BaseEntity {
    private static final long serialVersionUID = 8980226078305249367L;

    @TableField
    private Long departmentId;

    @TableField
    private String realname;

    @TableField
    private Date birthdate;

    @TableField
    private String gender;

    @TableField
    private String status;

}
