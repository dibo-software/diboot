package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Employee;
import com.diboot.example.entity.EmployeePositionDepartment;
import com.diboot.example.entity.PositionDepartment;
import com.diboot.example.mapper.EmployeeMapper;
import com.diboot.example.service.DepartmentService;
import com.diboot.example.service.EmployeePositionDepartmentService;
import com.diboot.example.service.EmployeeService;
import com.diboot.example.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 员工相关Service实现
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/1
 */
@Service
@Slf4j
public class EmployeeServiceImpl extends BaseServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeePositionDepartmentService employeePositionDepartmentService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public List<EmployeeVO> getEmployeeList(QueryWrapper<Employee> wrapper, Pagination pagination, Long orgId) {
        Wrapper queryWrapper = null;
        queryWrapper = new LambdaQueryWrapper<Department>()
                .eq(Department::getOrgId, orgId);
        List<Department> deptList = departmentService.getEntityList(queryWrapper);
        List<Long> deptIdList = getIdList(deptList);
        if(V.isEmpty(deptIdList)){
            return null;
        }
        queryWrapper = new LambdaQueryWrapper<EmployeePositionDepartment>()
                .in(EmployeePositionDepartment::getDepartmentId, deptIdList);
        List<EmployeePositionDepartment> epdList = employeePositionDepartmentService.getEntityList(queryWrapper);
        List<Long> empIdList = getIdList(epdList, "getEmployeeId");
        if(V.isEmpty(empIdList)){
            return null;
        }
        wrapper.lambda().in(Employee::getId, empIdList);
        List<Employee> empList = super.getEntityList(wrapper, pagination);
        List<EmployeeVO> voList = RelationsBinder.convertAndBind(empList, EmployeeVO.class);
        return voList;
    }

    @Override
    public List<Employee> getEmployeeList(Long orgId) {
        List<Employee> empList = new ArrayList<>();
        Wrapper wrapper = new LambdaQueryWrapper<Department>().eq(Department::getOrgId, orgId);
        List<Department> deptList = departmentService.getEntityList(wrapper);
        if(V.notEmpty(deptList)){
            List<Long> deptIdList = new ArrayList<>();
            for(Department dept : deptList){
                deptIdList.add(dept.getId());
            }
            if(V.notEmpty(deptIdList)){
                wrapper = new LambdaQueryWrapper<EmployeePositionDepartment>().in(EmployeePositionDepartment::getDepartmentId, deptIdList);
                List<EmployeePositionDepartment> epdList = employeePositionDepartmentService.getEntityList(wrapper);
                if(V.notEmpty(epdList)){
                    Set<Long> empIdSet = new HashSet<>();
                    for(EmployeePositionDepartment epd : epdList){
                        empIdSet.add(epd.getEmployeeId());
                    }
                    if(V.notEmpty(empIdSet)){
                        empList = super.getEntityListByIds(new ArrayList(empIdSet));
                    }
                }
            }
        }

        return empList;
    }

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
                empPosiDept.setEmployeeId(employeeVO.getId());
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

    //默认获取Long类型主键ID list，若idType参数有值，则取相应的ID  list
    private <T extends BaseEntity> List<Long> getIdList(List<T> entityList, String... idGetMethod){
        List<Long> idList = new ArrayList<>();
        try {
            if(V.notEmpty(entityList)){
                if(V.notEmpty(idGetMethod)){
                    String getMethod = idGetMethod[0];
                    for(T entity : entityList){
                        Class clazz = entity.getClass();
                        Method method = clazz.getMethod(getMethod);
                        Object obj = method.invoke(entity);
                        if(V.notEmpty(obj)){
                            idList.add((Long)obj);
                        }
                    }
                }else{
                    for(T entity : entityList){
                        idList.add(entity.getId());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("获取idList失败");
           return null;
        }

        return idList;
    }
}
