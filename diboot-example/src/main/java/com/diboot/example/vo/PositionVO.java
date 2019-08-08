package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Position;
import lombok.Data;

import java.util.List;

/**
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
@Data
public class PositionVO extends Position {

    @BindField(entity = Position.class, field = "name", condition = "this.parent_id = id")
    private String parentName;

    @BindDict(type = "POSITION_LEVEL", field = "level")
    private String levelLabel;

    @BindEntity(entity = Position.class, condition = "this.parent_id = id")
    private Position parentPosition;

    @BindEntityList(entity = Department.class, condition = "this.id = if_position_department.position_id AND if_position_department.department_id = id")
    private List<Department> departmentList;

    private List<PositionVO> children;

}
