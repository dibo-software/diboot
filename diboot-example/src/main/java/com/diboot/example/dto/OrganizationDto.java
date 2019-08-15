package com.diboot.example.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import lombok.Data;

@Data
public class OrganizationDto {

    @BindQuery(comparison = Comparison.LIKE)
    private String name;

    @BindQuery(comparison = Comparison.LIKE)
    private String code;

}
