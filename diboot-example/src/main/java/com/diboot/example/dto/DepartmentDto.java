package com.diboot.example.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import lombok.Data;

@Data
public class DepartmentDto {

    @BindQuery(comparison = Comparison.EQ)
    private Long orgId;

    @BindQuery(comparison = Comparison.LIKE)
    private String name;

    @BindQuery(comparison = Comparison.LIKE)
    private String shortName;

    @BindQuery(comparison = Comparison.LIKE)
    private String code;

    @BindQuery(comparison = Comparison.LIKE)
    private String charger;

}
