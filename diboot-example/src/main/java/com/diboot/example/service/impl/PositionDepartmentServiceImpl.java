package com.diboot.example.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.example.entity.PositionDepartment;
import com.diboot.example.mapper.PositionDepartmentMapper;
import com.diboot.example.service.PositionDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PositionDepartmentServiceImpl extends BaseServiceImpl<PositionDepartmentMapper, PositionDepartment> implements PositionDepartmentService {

}
