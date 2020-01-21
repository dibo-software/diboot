package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseExtEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
* 登录记录 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Getter @Setter @Accessors(chain = true)
public class IamLoginTrace extends BaseExtEntity {
    private static final long serialVersionUID = -6166037224391478085L;

    // 用户类型
    @NotNull(message = "用户类型不能为空")
    @Length(max=100, message="用户类型长度应小于100")
    @TableField()
    private String userType;

    // 用户ID
    @NotNull(message = "用户ID不能为空")
    @TableField()
    private Long userId = 0L;

    // 认证方式
    @NotNull(message = "认证方式不能为空")
    @Length(max=20, message="认证方式长度应小于20")
    @TableField()
    private String authType;

    // 用户名
    @NotNull(message = "用户名不能为空")
    @Length(max=100, message="用户名长度应小于100")
    @TableField()
    private String authAccount;

    // 是否成功
    @TableField("is_success")
    private boolean success = false;

    @Length(max=50, message="IP长度应小于50")
    @TableField()
    private String ipAddress;

    @Length(max=200, message="User-Agent长度应小于200")
    @TableField()
    private String userAgent;

    @TableField(exist = false)
    private boolean deleted;

}
