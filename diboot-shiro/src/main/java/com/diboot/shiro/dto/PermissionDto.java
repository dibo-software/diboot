package com.diboot.shiro.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import lombok.Data;

@Data
public class PermissionDto {

    @BindQuery(comparison = Comparison.LIKE)
    private String menuCode;

    @BindQuery(comparison = Comparison.LIKE)
    private String menuName;
}
