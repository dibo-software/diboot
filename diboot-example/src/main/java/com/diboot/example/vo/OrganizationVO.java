package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.example.entity.Organization;
import lombok.Data;

import java.util.List;

/**
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
@Data
public class OrganizationVO extends Organization {
    private static final long serialVersionUID = -8106510717110090097L;

    // 字段关联
    @BindField(entity=Organization.class, field="name", condition="this.parent_id=id")
    private String parentName;

    // 元数据关联
    @BindDict(type = "INDUSTRY", field = "industry")
    private String industryLabel;

    private List<OrganizationVO> children;

}