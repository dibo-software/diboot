package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Organization;
import lombok.Data;

import java.util.List;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
@Data
public class DepartmentVO extends Department {
    private static final long serialVersionUID = -362116388664907913L;

    // 直接关联Entity中的某字段
    @BindField(entity = Organization.class, field = "name", condition = "this.org_id=id")
    private String orgName;

    @BindField(entity = Department.class, field = "name", condition = "this.parent_id=id")
    private String parentName;

    // 直接关联Entity
    /*@BindEntity(entity = Organization.class, condition="this.org_id=id")
    private Organization organization;*/

    // 直接关联多个Entity
    /*@BindEntityList(entity = Department.class, condition = "this.id=parent_id")
    private List<Department> children;*/

}