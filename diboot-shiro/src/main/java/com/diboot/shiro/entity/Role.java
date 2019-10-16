package com.diboot.shiro.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.V;
import lombok.Data;

import java.util.List;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Data
public class Role extends BaseEntity {

    private static final long serialVersionUID = 5433209472424293571L;

    // status字段的关联元数据
    public static final String DICT_STATUS = "ROLE_STATUS";

    @TableField
    private String name;

    @TableField
    private String code;

    @TableField
    private String status;

    @TableField
    private String comment;

    @TableField
    private String userType;

    @TableField(exist = false)
    private List<Permission> permissionList;

    /***
     * 是否是管理员权限
     * @return
     */
    public boolean isAdmin(){
        return V.equals(code, "ADMIN");
    }
}
