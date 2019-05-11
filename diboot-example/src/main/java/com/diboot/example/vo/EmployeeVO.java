package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.annotation.BindMetadata;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Employee;
import com.diboot.example.entity.Organization;
import lombok.Data;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
@Data
public class EmployeeVO extends Employee {
    private static final long serialVersionUID = 2956966168209358800L;

    @BindEntity(entity = Department.class, condition="department_id=id")
    private Department department;

    @BindMetadata(type="GENDER", field="gender")
    private String genderLabel;

    @BindField(entity=Department.class, field="name", condition="this.department_id=id AND code IS NOT NULL")
    private String deptName;

    // 通过中间表关联
    @BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id AND department.deleted=0") // AND deleted=0
    private Organization organization;
}