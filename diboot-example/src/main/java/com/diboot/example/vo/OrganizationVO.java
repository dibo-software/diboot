package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.example.entity.Organization;
import lombok.Data;

import java.util.List;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
@Data
public class OrganizationVO extends Organization {
    private static final long serialVersionUID = 9056449207962546696L;

    @BindField(entity = Organization.class, field = "name", condition = "this.parentId=id")
    private String parentName;

    @BindEntityList(entity = Organization.class, condition = "this.id=parentId")
    private List<Organization> children;

}
