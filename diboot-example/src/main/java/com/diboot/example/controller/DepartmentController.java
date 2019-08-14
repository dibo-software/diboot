package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Organization;
import com.diboot.example.entity.Tree;
import com.diboot.example.service.DepartmentService;
import com.diboot.example.vo.DepartmentVO;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 部门相关Controller
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@RestController
@RequestMapping("/department")
@AuthorizationPrefix(name = "部门管理", code = "department", prefix = "department")
public class DepartmentController extends BaseCrudRestController {

    @Autowired
    private DepartmentService departmentService;


    /***
     * 查询ViewObject的分页数据 (此为非继承的自定义使用案例，更简化的调用父类案例请参考UserController)
     * <p>
     * url参数示例: /list?pageSize=20&pageIndex=1&orderBy=id&code=TST
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    @AuthorizationWrapper(value = @RequiresPermissions("list"), name = "列表")
    public JsonResult getVOList(Long orgId, Department department, Pagination pagination, HttpServletRequest request) throws Exception{
        if(V.isEmpty(orgId)){
            return new JsonResult(Status.FAIL_OPERATION, "请先选择所属公司").bindPagination(pagination);
        }
        QueryWrapper<Department> queryWrapper = super.buildQueryWrapper(department);
        queryWrapper.lambda().eq(Department::getParentId, 0);
        // 查询当前页的Entity主表数据
        List<DepartmentVO> voList = departmentService.getDepartmentList(queryWrapper, pagination, department.getOrgId());
        // 返回结果
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    /***
     * 查询ViewObject全部数据 (此为非继承的自定义使用案例，更简化的调用父类案例请参考UserController)
     * <p>
     * url参数示例: /listAll?orderBy=id&code=TST
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/listAll")
    public JsonResult getAllVOList(Department department, HttpServletRequest request) throws Exception{
        QueryWrapper<Department> queryWrapper = super.buildQueryWrapper(department);
        // 查询当前页的Entity主表数据
        List entityList = getService().getEntityList(queryWrapper);
        // 自动转换VO中注解绑定的关联
        List<DepartmentVO> voList = super.convertToVoAndBindRelations(entityList, DepartmentVO.class);
        // 返回结果
        return new JsonResult(Status.OK, voList);
    }

    /**
     * ID-Name的映射键值对，用于前端Select控件筛选等
     * @param request
     * @return
     */
    @GetMapping("/kv")
    public JsonResult getKVPairList(HttpServletRequest request){
        Wrapper wrapper = new QueryWrapper<Department>().lambda()
            .select(Department::getName, Department::getId, Department::getNumber);
        List<KeyValue> list = departmentService.getKeyValueList(wrapper);
        return new JsonResult(list);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    @AuthorizationWrapper(value = @RequiresPermissions("create"), name = "新建")
    public JsonResult createEntity(@RequestBody Department entity, BindingResult result, HttpServletRequest request)
            throws Exception{
        boolean success = departmentService.createEntity(entity);
        if(!success){
            return new JsonResult(Status.FAIL_OPERATION);
        }
        return new JsonResult(Status.OK);
    }

    /***
     * 查询Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("read"), name = "读取")
    public JsonResult getModel(@PathVariable("id")Long id, HttpServletRequest request)
            throws Exception{
        DepartmentVO vo = departmentService.getViewObject(id, DepartmentVO.class);
        return new JsonResult(vo);
    }

    /***
     * 更新Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("update"), name = "更新")
    public JsonResult updateModel(@PathVariable("id")Long id, @RequestBody Department entity, BindingResult result,
                                  HttpServletRequest request) throws Exception{
        entity.setId(id);
        boolean success = departmentService.updateEntity(entity);
        if(!success){
            return new JsonResult(Status.FAIL_OPERATION);
        }
        return new JsonResult(Status.OK);
    }

    /***
     * 删除用户
     * @param id 用户ID
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("delete"), name = "删除")
    public JsonResult deleteModel(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
        return super.deleteEntity(id);
    }

    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap){
        Wrapper wrapper = null;

        return new JsonResult(modelMap);
    }

    @GetMapping("/getViewTreeList")
    public JsonResult getViewTreeList(@RequestParam(required = false) Long orgId) throws Exception{
        List<DepartmentVO> voList = departmentService.getEntityTreeList(orgId);
        List<Tree> treeList = departmentService.getViewTreeList(voList);
        return new JsonResult(treeList);
    }

    @GetMapping("/checkNameRepeat")
    public JsonResult checkNameRepeat(@RequestParam Long orgId, @RequestParam(required = false) Long id, @RequestParam String name){
        if(V.isEmpty(name)){
            return new JsonResult(Status.OK);
        }
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<Department>()
                .eq(Department::getOrgId, orgId)
                .eq(Department::getName, name);
        if(V.notEmpty(id)){
            wrapper.ne(Department::getId, id);
        }
        List<Department> deptList = departmentService.getEntityList(wrapper);
        if(V.isEmpty(deptList)){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION, "部门名称已存在");
    }

    @GetMapping("/checkNumberRepeat")
    public JsonResult checkNumberRepeat(@RequestParam Long orgId, @RequestParam(required = false) Long id, @RequestParam String number){
        if(V.isEmpty(number)){
            return new JsonResult(Status.OK);
        }
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<Department>()
                .eq(Department::getOrgId, orgId)
                .eq(Department::getNumber, number);
        if(V.notEmpty(id)){
            wrapper.ne(Department::getId, id);
        }
        List<Department> deptList = departmentService.getEntityList(wrapper);
        if(V.isEmpty(deptList)){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION, "部门编号已存在");
    }

    @Override
    protected BaseService getService() {
        return departmentService;
    }

}
