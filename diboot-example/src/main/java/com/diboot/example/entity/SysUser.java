package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.vo.RoleVO;
import lombok.Data;

import java.util.List;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Data
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 466801280426981780L;

    // status字段的关联元数据
    public static final String USER_STATUS = "USER_STATUS";
    // gender字段的关联元数据
    public static final String GENDER = "GENDER";

    @TableField
    private Long departmentId;

    @TableField
    private String username;

    @TableField
    private String password;

    @TableField
    private String gender;

    @TableField
    private String phone;

    @TableField
    private String email;

    @TableField
    private String status;

    @TableField
    private String comment;

    @TableField(exist = false)
    private List<Role> roleList;

    @TableField(exist = false)
    private List<RoleVO> roleVOList;

    @TableField(exist = false)
    private List<Permission> permissionList;
}
