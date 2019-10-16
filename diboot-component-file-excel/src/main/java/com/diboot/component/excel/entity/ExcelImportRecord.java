package com.diboot.component.excel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/**
* Excel导入记录
* @author Mazc
* @version 2017-09-18
* Copyright @ www.com.ltd
*/
@Data
public class ExcelImportRecord extends BaseEntity {
    private static final long serialVersionUID = 7628990901469833759L;

    @TableField
    private String fileUuid; // 文件ID

    @TableField
    private String relObjType; // 关联类型

    @TableField
    private Long relObjId; // 关联ID

    @TableField
    private String relObjUid; // 关联UUID

}