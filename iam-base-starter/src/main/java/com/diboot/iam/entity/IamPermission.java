package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseExtEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
* 权限 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Getter @Setter @Accessors(chain = true)
@Deprecated
public class IamPermission extends BaseExtEntity {
    private static final long serialVersionUID = -1234249053749049729L;

    public IamPermission(){}
    public IamPermission(String application, String type){
        this.application = application;
        this.type = type;
    }

    // 上级ID
    @NotNull(message = "上级ID不能为空")
    @TableField()
    private Long parentId;

    // 所属应用
    @NotNull(message = "所属应用不能为空")
    @TableField()
    private String application;

    // 权限类别
    @NotNull(message = "权限类别不能为空")
    @TableField()
    private String type;

    // 权限类别
    @NotNull(message = "名称类别不能为空")
    @TableField()
    private String name;

    @TableField()
    private String code;

    // 操作名称
    @TableField()
    private String operationName;

    // 操作许可编码
    @TableField()
    private String operationCode;

    @TableField()
    private Integer sortId;

    // 更新时间
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Date updateTime;

}
