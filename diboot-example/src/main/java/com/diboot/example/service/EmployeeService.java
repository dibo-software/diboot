package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Employee;
import com.diboot.example.vo.EmployeeVO;
import com.diboot.example.vo.PositionVO;

import java.util.List;

/**
 * 员工相关Service
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
public interface EmployeeService extends BaseService<Employee> {

    //获取列表页数据
    List<EmployeeVO> getEmployeeList(QueryWrapper<Employee> wrapper, Pagination pagination, Long orgId);

    //获取某公司下的所有员工
    List<Employee> getEmployeeList(Long orgId);

    boolean createEmployee(EmployeeVO employeeVO);

    boolean updateEmployee(EmployeeVO employeeVO);

    boolean deleteEmployee(Long id);

}
