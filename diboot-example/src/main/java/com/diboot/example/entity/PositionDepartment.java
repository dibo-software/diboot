package com.diboot.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

@Data
@TableName(value = "if_position_department")
public class PositionDepartment extends BaseEntity {

    private Long positionId;//职位ID

    private Long departmentId;//部门ID

}
