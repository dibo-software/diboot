package com.diboot.shiro.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import lombok.Data;

@Data
public class RoleDto {

    @BindQuery(comparison = Comparison.LIKE)
    private String code;

    @BindQuery(comparison = Comparison.EQ)
    private String status;
}
