package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
* 角色权限关联 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Getter @Setter @Accessors(chain = true)
public class IamRolePermission extends BaseEntity {
    private static final long serialVersionUID = -8228772361638435896L;

    public IamRolePermission(){
    }
    public IamRolePermission(Long roleId, Long permissionId){
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    // 角色ID
    @NotNull(message = "角色ID不能为空")
    @TableField()
    private Long roleId;

    // 权限ID
    @NotNull(message = "权限ID不能为空")
    @TableField()
    private Long permissionId;

}
