package com.diboot.example.vo;

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

    @BindField(entity = Organization.class, field = "name", condition = "org_id=id")
    private String orgName;

    @BindField(entity = Department.class, field = "name", condition = "parent_id=id")
    private String parentName;

    @BindEntityList(entity = Department.class, condition = "id=parent_id")
    private List<Department> children;

}