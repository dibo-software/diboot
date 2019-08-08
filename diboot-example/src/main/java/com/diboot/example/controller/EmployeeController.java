package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Employee;
import com.diboot.example.service.EmployeeService;
import com.diboot.example.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/*
 * 员工 controller
 * */
@RestController
@RequestMapping("/employee")
public class EmployeeController extends BaseCrudRestController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DictionaryService dictionaryService;


    @RequestMapping("/list")
    public JsonResult list(Long orgId, Employee employee, Pagination pagination, HttpServletRequest request) throws Exception{
        if(V.isEmpty(orgId)){
            return new JsonResult(Status.FAIL_OPERATION, "请先选择所属公司");
        }
        QueryWrapper<Employee> queryWrapper = super.buildQueryWrapper(employee);
        List<EmployeeVO> voList = employeeService.getEmployeeList(queryWrapper, pagination, orgId);
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    @GetMapping("/{id}")
    public JsonResult get(@PathVariable Long id) throws Exception{
        EmployeeVO vo =  employeeService.getViewObject(id, EmployeeVO.class);
        return new JsonResult(vo);
    }

    @PostMapping("/")
    public JsonResult createModel(@RequestBody EmployeeVO entity, HttpServletRequest request) throws Exception{
        boolean success = employeeService.createEmployee(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable Long id, @RequestBody EmployeeVO entity, HttpServletRequest request) throws Exception{
        entity.setId(id);
        boolean success = employeeService.updateEmployee(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        boolean success = employeeService.deleteEmployee(id);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap){
        Wrapper wrapper = null;

        //性别元数据
        List<KeyValue> genderKvList = dictionaryService.getKeyValueList(Employee.DICT_GENDER);
        modelMap.put("genderKvList", genderKvList);

        return new JsonResult(modelMap);
    }

    @Override
    protected BaseService getService() {
        return employeeService;
    }
}
