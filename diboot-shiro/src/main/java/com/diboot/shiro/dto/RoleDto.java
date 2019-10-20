package com.diboot.shiro.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import lombok.Data;

@Data
public class RoleDto {

    /**
     * 角色编码
     */
    @BindQuery(comparison = Comparison.LIKE)
    private String code;

    /**
     * 角色状态
     */
    @BindQuery(comparison = Comparison.EQ)
    private String status;

    /**
     * 角色类型
     */
    @BindQuery(comparison = Comparison.EQ)
    private String userType;
}
