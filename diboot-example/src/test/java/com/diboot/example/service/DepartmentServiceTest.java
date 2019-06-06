package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.example.ApplicationTest;
import com.diboot.example.entity.Department;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.junit.Assert.*;

@Component
public class DepartmentServiceTest extends ApplicationTest {

    @Autowired
    private DepartmentService departmentService;

    @Test
    public void getEntityListTest() throws Exception{
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.eq(BeanUtils.convertToFieldName(Department::getName), "研发部");
        List<Department> departmentList = departmentService.getEntityList(query);
        Assert.assertTrue(V.notEmpty(departmentList));
    }

}