package com.diboot.example.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;

/**
 * <Description>
 *
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/08/06
 */
public class UserDto {

    @BindQuery(comparison = Comparison.EQ)
    private Long id;

    @BindQuery(comparison = Comparison.EQ)
    private Long departmentId;

    @BindQuery(comparison = Comparison.LIKE)
    private String username;

    @BindQuery(comparison = Comparison.EQ)
    private String gender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
