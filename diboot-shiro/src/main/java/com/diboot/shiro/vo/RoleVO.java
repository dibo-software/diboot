package com.diboot.shiro.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.entity.Role;
import lombok.Data;

import java.util.List;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Data
public class RoleVO extends Role {

    private static final long serialVersionUID = 860775286174387052L;

    @BindDict(type="ROLE_STATUS", field="status")
    private String statusLabel;

    /**支持通过中间表的多-多Entity实体关联*/
    @BindEntityList(entity = Permission.class, condition="this.id=role_permission.role_id AND role_permission.permission_id=id AND role_permission.is_deleted=0")
    private List<Permission> permissionList;

    @TableField(exist = false)
    private List<Permission> menuList;

}