package com.diboot.example.service;

import com.diboot.core.service.BaseService;
import com.diboot.example.entity.Employee;
import com.diboot.example.vo.EmployeeVO;
import com.diboot.example.vo.PositionVO;

/**
 * 员工相关Service
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
public interface EmployeeService extends BaseService<Employee> {

    boolean createEmployee(EmployeeVO employeeVO);

    boolean updateEmployee(EmployeeVO employeeVO);

    boolean deleteEmployee(Long id);

}
