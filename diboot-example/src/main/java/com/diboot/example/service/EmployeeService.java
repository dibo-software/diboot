package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.BaseService;
import com.diboot.example.entity.Employee;

import java.util.List;
import java.util.Map;

/**
 * 员工相关Service
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
public interface EmployeeService extends BaseService<Employee> {

    List<Map<String, Object>> testCustomQueryWrapper(QueryWrapper wrapper);

}
