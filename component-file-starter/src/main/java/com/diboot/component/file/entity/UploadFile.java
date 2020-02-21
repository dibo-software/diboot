package com.diboot.component.file.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * file基础父类
 * @author wangyl@dibo.ltd
 * @version v2.0
 * @date 2019/07/18
 */
@Getter @Setter @Accessors(chain = true)
public class UploadFile extends BaseEntity {
    private static final long serialVersionUID = 201L;

    // 废弃默认主键
    @TableField(exist = false)
    private Long id;
    // 声明新主键uuid
    @TableId(type = IdType.UUID)
    private String uuid;

    @NotNull(message = "关联对象类不能为空！")
    @TableField
    private String relObjType = null;
    @TableField
    @NotNull(message = "关联对象ID不能为空！")
    private Long relObjId;

    @TableField
    @NotNull(message = "文件名不能为空！")
    @Length(max = 100, message = "文件名长度超出了最大限制！")
    private String fileName;

    @TableField
    @JSONField(serialize = false)
    private String storagePath;

    @TableField
    private String fileType;

    /**
     * 文件包含记录数
     */
    @TableField
    private int dataCount = 0;

    @TableField
    @Length(max = 200, message = "备注长度超出了最大限制！")
    private String description;

}
