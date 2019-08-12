package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Employee;
import com.diboot.example.entity.EmployeePositionDepartment;
import com.diboot.example.entity.Position;
import lombok.Data;

/**
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
@Data
public class EmployeeVO extends Employee {

    @BindDict(type="GENDER", field="gender")
    private String genderLabel;

    @BindEntity(entity = Department.class, condition = "this.id = if_employee_position_department.employee_id AND if_employee_position_department.department_id = id AND if_employee_position_department.deleted = 0")
    private Department department;

    @BindEntity(entity = Position.class, condition = "this.id = if_employee_position_department.employee_id AND if_employee_position_department.position_id = id AND if_employee_position_department.deleted = 0")
    private Position position;

    private EmployeePositionDepartment empPosiDept;

}
