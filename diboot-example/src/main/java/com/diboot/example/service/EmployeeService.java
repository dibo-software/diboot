package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Employee;
import com.diboot.example.vo.EmployeeVO;

import java.util.List;

/**
 * 员工相关Service
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
public interface EmployeeService extends BaseService<Employee> {

    /***
     * 获取列表页数据
     * @param wrapper
     * @param pagination
     * @param orgId
     * @return
     */
    List<EmployeeVO> getEmployeeList(QueryWrapper<Employee> wrapper, Pagination pagination, Long orgId);

    /***
     * 获取某公司下的所有员工
     * @param orgId
     * @return
     */
    List<Employee> getEmployeeList(Long orgId);

    /***
     * 新建
     * @param employeeVO
     * @return
     */
    boolean createEmployee(EmployeeVO employeeVO);

    /***
     * 更新
     * @param employeeVO
     * @return
     */
    boolean updateEmployee(EmployeeVO employeeVO);

    /***
     * 删除
     * @param id
     * @return
     */
    boolean deleteEmployee(Long id);

}
