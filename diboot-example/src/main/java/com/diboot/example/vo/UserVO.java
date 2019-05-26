package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.annotation.BindMetadata;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Organization;
import com.diboot.example.entity.Role;
import com.diboot.example.entity.User;
import lombok.Data;

import java.util.List;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Data
public class UserVO extends User {
    private static final long serialVersionUID = 3526115343377985725L;

    @BindMetadata(type="GENDER", field = "gender")
    private String genderLabel;

    @BindField(entity=Department.class, field="name", condition="department_id=id AND code IS NOT NULL")
    private String deptName;

    @BindEntity(entity = Department.class, condition="department_id=id")
    private Department department;

    // 支持级联字段关联
    @BindField(entity = Organization.class, field="name", condition="this.department_id=department.id AND department.org_id=id")
    private String orgName;

    // 通过中间表关联
    @BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id") // AND deleted=0
    private Organization organization;

    // 支持多-多Entity实体关联
    @BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
    private List<Role> roleList;

}