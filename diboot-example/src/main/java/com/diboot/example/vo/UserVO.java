package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Organization;
import com.diboot.example.entity.User;
import lombok.Data;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Data
public class UserVO extends User {
    private static final long serialVersionUID = 3526115343377985725L;

    // 绑定数据字典枚举
    @BindDict(type="GENDER", field = "gender")
    private String genderLabel;

    // 字段关联，相同条件的entity+condition将合并为一条SQL查询
    @BindField(entity=Department.class, field="name", condition="this.department_id=id AND parent_id IS NOT NULL")
    private String deptName;
    @BindField(entity=Department.class, field="code", condition="this.department_id=id")
    private String deptCode;

    // 支持级联字段关联，相同条件的entity+condition将合并为一条SQL查询
    @BindField(entity = Organization.class, field="name", condition="this.department_id=department.id AND department.org_id=id")
    private String orgName;
    @BindField(entity = Organization.class, field="telphone", condition="this.department_id=department.id AND department.org_id=id")
    private String orgTelphone;

    // 通过中间表关联Entity
    @BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id") // AND is_deleted=0
    private Organization organization;

    // 支持通过中间表的多-多Entity实体关联
    //@BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
    //private List<Role> roleList;

}