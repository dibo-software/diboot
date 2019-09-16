package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.dto.EmployeeDto;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Employee;
import com.diboot.example.service.EmployeeService;
import com.diboot.example.vo.EmployeeVO;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 员工相关Controller
 * @author wangyonglaing
 * @version 2.0
 * @time 2018/8/5
 */
@RestController
@RequestMapping("/employee")
@AuthorizationPrefix(name = "员工管理", code = "employee", prefix = "employee")
public class EmployeeController extends BaseCrudRestController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DictionaryService dictionaryService;

    /***
     * 获取列表页数据
     * @param orgId
     * @param dto
     * @param pagination
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/list")
    @AuthorizationWrapper(value = @RequiresPermissions("list"), name = "列表")
    public JsonResult list(Long orgId, EmployeeDto dto, Pagination pagination, HttpServletRequest request) throws Exception{
        if(V.isEmpty(orgId)){
            return new JsonResult(Status.FAIL_OPERATION, "请先选择所属公司").bindPagination(pagination);
        }
        QueryWrapper<Employee> queryWrapper = super.buildQueryWrapper(dto);
        List<EmployeeVO> voList = employeeService.getEmployeeList(queryWrapper, pagination, orgId);
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    /***
     * 获取详细数据
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("read"), name = "读取")
    public JsonResult get(@PathVariable Long id) throws Exception{
        EmployeeVO vo =  employeeService.getViewObject(id, EmployeeVO.class);
        return new JsonResult(vo);
    }

    /***
     * 新建
     * @param entity
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    @AuthorizationWrapper(value = @RequiresPermissions("create"), name = "新建")
    public JsonResult createModel(@RequestBody EmployeeVO entity, HttpServletRequest request) throws Exception{
        boolean success = employeeService.createEmployee(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    /***
     * 更新
     * @param id
     * @param entity
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("update"), name = "更新")
    public JsonResult updateModel(@PathVariable Long id, @RequestBody EmployeeVO entity, HttpServletRequest request) throws Exception{
        entity.setId(id);
        boolean success = employeeService.updateEmployee(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    /***
     * 删除
     * @param id
     * @param request
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("delete"), name = "删除")
    public JsonResult deleteModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        boolean success = employeeService.deleteEmployee(id);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    /***
     * 加载更多数据
     * @param request
     * @param modelMap
     * @return
     */
    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap){
        Wrapper wrapper = null;

        //性别元数据
        List<KeyValue> genderKvList = dictionaryService.getKeyValueList(Employee.DICT_GENDER);
        modelMap.put("genderKvList", genderKvList);

        return new JsonResult(modelMap);
    }

    /***
     * 校验员工编号唯一性
     * @param orgId
     * @param id
     * @param number
     * @return
     */
    @GetMapping("/checkNumberRepeat")
    public JsonResult checkNumberRepeat(@RequestParam Long orgId, @RequestParam(required = false) Long id, @RequestParam String number){
        if(V.isEmpty(number)){
            return new JsonResult(Status.OK);
        }
        List<Employee> empList = employeeService.getEmployeeList(orgId);
        if(V.isEmpty(empList)){
            return new JsonResult(Status.OK);
        }
        int count = 0;
        for(Employee emp : empList){
            if(number.equals(emp.getNumber())){
                if(V.isEmpty(id)){
                    count++;
                }else{
                    if(V.notEquals(id, emp.getId())){
                        count++;
                    }
                }
            }
        }
        if(count == 0){
            return new JsonResult(Status.OK);
        }

        return new JsonResult(Status.FAIL_OPERATION, "员工工号已存在");
    }

    /***
     * 校验用户名唯一性
     * @param orgId
     * @param id
     * @param account
     * @return
     */
    @GetMapping("/checkAccountRepeat")
    public JsonResult checkAccountRepeat(@RequestParam Long orgId, @RequestParam(required = false) Long id, @RequestParam String account){
        if(V.isEmpty(account)){
            return new JsonResult(Status.OK);
        }
        List<Employee> empList = employeeService.getEmployeeList(orgId);
        if(V.isEmpty(empList)){
            return new JsonResult(Status.OK);
        }
        int count = 0;
        for(Employee emp : empList){
            if(account.equals(emp.getAccount())){
                if(V.isEmpty(id)){
                    count++;
                }else{
                    if(V.notEquals(id, emp.getId())){
                        count++;
                    }
                }
            }
        }
        if(count == 0){
            return new JsonResult(Status.OK);
        }

        return new JsonResult(Status.FAIL_OPERATION, "用户名已存在");
    }

    @Override
    protected BaseService getService() {
        return employeeService;
    }
}
