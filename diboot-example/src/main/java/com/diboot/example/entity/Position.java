package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/**
 * 职位
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
@Data
@TableName(value = "if_position")
public class Position extends BaseEntity {

    // industry字段的关联元数据
    public static final String POSITION_LEVEL = "POSITION_LEVEL";

    private Long parentId;//上级职位

    private String number;//职位编号

    private String name;//职位名称

    private String level;//职级

    private String comment;//备注
}
