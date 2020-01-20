package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
* 角色 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Getter @Setter @Accessors(chain = true)
public class IamRole extends BaseEntity {
    private static final long serialVersionUID = -1186305888909118267L;

    public IamRole(){}
    public IamRole(String name, String code){
        this.name = name;
        this.code = code;
    }

    // 名称
    @NotNull(message = "名称不能为空")
    @TableField()
    private String name;

    // 编码
    @NotNull(message = "编码不能为空")
    @TableField()
    private String code;

    // 备注
    @TableField()
    private String description;

}
