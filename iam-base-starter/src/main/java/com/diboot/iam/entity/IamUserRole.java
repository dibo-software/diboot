package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
* 用户角色关联 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Getter @Setter @Accessors(chain = true)
public class IamUserRole extends BaseEntity {
    private static final long serialVersionUID = 7716603553049083815L;

    public IamUserRole(){}
    public IamUserRole(String userType, Long userId, Long roleId){
        this.userType = userType;
        this.userId = userId;
        this.roleId = roleId;
    }

    // 用户类型
    @NotNull(message = "用户类型不能为空")
    @TableField()
    private String userType;

    // 用户ID
    @NotNull(message = "用户ID不能为空")
    @TableField()
    private Long userId;

    // 角色ID
    @NotNull(message = "角色ID不能为空")
    @TableField()
    private Long roleId;

}
