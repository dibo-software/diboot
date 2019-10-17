package com.diboot.component.excel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/**
* Excel列定义
* @author Mazc
* @version 2017-09-18
* Copyright @ www.com.ltd
*/
@Data
public class ExcelColumn extends BaseEntity {
	private static final long serialVersionUID = -1539079350889067812L;

	@TableField
    private String modelClass; // Java对象类

    @TableField
    private String modelField; // Java对象属性

    @TableField
    private String colName; // 列标题

    @TableField
    private Integer colIndex; // 列索引

    @TableField
    private String dataType; // 数据类型

    @TableField
    private String validation; // 校验
}