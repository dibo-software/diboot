package com.diboot.shiro.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import lombok.Data;

@Data
public class SysUserDto {

    @BindQuery(comparison = Comparison.LIKE)
    private String username;

    @BindQuery(comparison = Comparison.EQ)
    private String status;

}
