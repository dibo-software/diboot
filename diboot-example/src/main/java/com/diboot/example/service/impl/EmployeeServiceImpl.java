package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Employee;
import com.diboot.example.entity.EmployeePositionDepartment;
import com.diboot.example.entity.PositionDepartment;
import com.diboot.example.mapper.EmployeeMapper;
import com.diboot.example.service.EmployeePositionDepartmentService;
import com.diboot.example.service.EmployeeService;
import com.diboot.example.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 员工相关Service实现
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
@Service
@Slf4j
public class EmployeeServiceImpl extends BaseServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeePositionDepartmentService employeePositionDepartmentService;

    @Override
    @Transactional
    public boolean createEmployee(EmployeeVO employeeVO) {
        Employee employee = BeanUtils.convert(employeeVO, Employee.class);
        boolean success = super.createEntity(employee);
        if(!success){
            return false;
        }
       EmployeePositionDepartment empPosiDept = employeeVO.getEmpPosiDept();
        try {
            if(V.notEmpty(empPosiDept)){
                empPosiDept.setEmployeeId(employee.getId());
                success = employeePositionDepartmentService.createEntity(empPosiDept);
                if(!success){
                    throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updateEmployee(EmployeeVO employeeVO) {
        Employee employee = BeanUtils.convert(employeeVO, Employee.class);
        boolean success = super.updateEntity(employee);
        if(!success){
            return false;
        }

        try {
            //获取员工-职位-部门对应信息
            EmployeePositionDepartment empPosiDept = employeeVO.getEmpPosiDept();
            QueryWrapper<EmployeePositionDepartment> query = new QueryWrapper();
            query.lambda()
                 .eq(EmployeePositionDepartment::getEmployeeId, employeeVO.getId())
                 .eq(EmployeePositionDepartment::getDepartmentId, empPosiDept.getDepartmentId())
                 .eq(EmployeePositionDepartment::getPositionId, empPosiDept.getPositionId());
            List<EmployeePositionDepartment> oldList = employeePositionDepartmentService.getEntityList(query);

            if(V.isEmpty(oldList)){
                query = new QueryWrapper();
                query.lambda()
                     .eq(EmployeePositionDepartment::getEmployeeId, employeeVO.getId());
                employeePositionDepartmentService.deleteEntities(query);
                employeePositionDepartmentService.createEntity(empPosiDept);
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteEmployee(Long id) {
        boolean success = super.deleteEntity(id);
        if(!success){
            return false;
        }

        QueryWrapper<EmployeePositionDepartment> query = new QueryWrapper();
        query.lambda().eq(EmployeePositionDepartment::getEmployeeId, id);
        try {
            employeePositionDepartmentService.deleteEntities(query);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }
}
