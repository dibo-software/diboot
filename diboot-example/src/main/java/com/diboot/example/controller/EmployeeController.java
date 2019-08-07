package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Employee;
import com.diboot.example.entity.Organization;
import com.diboot.example.service.*;
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
    private OrganizationService organizationService;


    @RequestMapping("/list")
    public JsonResult list(Employee employee, Pagination pagination, HttpServletRequest request) throws Exception{
        QueryWrapper<Employee> queryWrapper = super.buildQueryWrapper(employee);
        List<Employee> entityList =  getService().getEntityList(queryWrapper, pagination);
        List<EmployeeVO> voList = RelationsBinder.convertAndBind(entityList, EmployeeVO.class);
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    @GetMapping("/{id}")
    public JsonResult get(@PathVariable Long id) throws Exception{
        EmployeeVO vo =  employeeService.getViewObject(id, EmployeeVO.class);
        return new JsonResult(vo);
    }

    @PostMapping("/")
    public JsonResult createModel(@RequestBody Employee entity, HttpServletRequest request) throws Exception{
        boolean success = employeeService.createEntity(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable Long id, @RequestBody Employee entity, HttpServletRequest request) throws Exception{
        entity.setId(id);
        boolean success = employeeService.updateEntity(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        boolean success = employeeService.deleteEntity(id);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap){
        Wrapper wrapper = null;
        //获取组织机构KV
        wrapper = new QueryWrapper<Organization>()
                .lambda()
                .select(Organization::getName, Organization::getId);
        List<KeyValue> orgKvList = organizationService.getKeyValueList(wrapper);
        modelMap.put("orgKvList", orgKvList);

        return new JsonResult(modelMap);
    }

    @Override
    protected BaseService getService() {
        return employeeService;
    }
}
