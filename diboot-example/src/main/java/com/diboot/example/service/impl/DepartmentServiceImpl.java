package com.diboot.example.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.example.entity.Department;
import com.diboot.example.mapper.DepartmentMapper;
import com.diboot.example.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 部门相关Service实现
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Service
@Slf4j
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentMapper, Department> implements DepartmentService {

}
