package com.diboot.shiro.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
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

    /**
     * 用户名
     */
    @TableField
    private String username;

    /**
     * 密码
     */
    @TableField
    private String password;

    /**
     * 性别
     */
    @TableField
    private String gender;

    /**
     * 加密盐
     */
    @TableField
    private String salt;

    /**
     * 用户类型
     */
    @TableField
    private String userType;

    /**
     * 用户id
     */
    @TableField
    private Long userId;

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

    @TableField(exist = false)
    private Boolean admin;
}
