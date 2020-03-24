package com.diboot.file.example.custom.service;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.file.example.custom.Department;
import com.diboot.file.example.custom.mapper.DepartmentMapper;
import org.springframework.stereotype.Service;

/**
 * 文件下载实现类
 * @author Lishuaifei@dibo.ltd
 * @date 2019-07-18
 */
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentMapper, Department> implements DepartmentService {

}
