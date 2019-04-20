package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.example.entity.Employee;
import com.diboot.example.mapper.EmployeeMapper;
import com.diboot.example.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 员工相关Service
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@Service
@Slf4j
public class EmployeeServiceImpl extends BaseServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public List<Map<String, Object>> testCustomQueryWrapper(QueryWrapper wrapper){
        return getBaseMapper().selectMaps(wrapper);
    }
}
