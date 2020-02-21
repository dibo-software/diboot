package com.diboot.file.example.custom.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.file.example.custom.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件相关Mapper
 * @author Mazc
 * @version 2017/4/18
 */
@Mapper
public interface DepartmentMapper extends BaseCrudMapper<Department> {

}