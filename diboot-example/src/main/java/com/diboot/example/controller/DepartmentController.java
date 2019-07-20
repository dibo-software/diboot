package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Organization;
import com.diboot.example.service.DepartmentService;
import com.diboot.example.vo.DepartmentVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Organization相关Controller
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@RestController
@RequestMapping("/department")
public class DepartmentController extends BaseCrudRestController {

    @Autowired
    private DepartmentService departmentService;

    /***
     * 查询ViewObject的分页数据 (此为非继承的自定义使用案例，更简化的调用父类案例请参考UserController)
     * <p>
     * url参数示例: /list?_pageSize=20&_pageIndex=1&_orderBy=id&code=TST
     * </p>
     * @return
     * @throws Exception
     */
    @RequiresPermissions("department:list")
    @GetMapping("/list")
    public JsonResult getVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<Department> queryWrapper = buildQuery(request);
        // 构建分页
        Pagination pagination = buildPagination(request);
        // 查询当前页的Entity主表数据
        List entityList = getService().getEntityList(queryWrapper, pagination);
        // 自动转换VO中注解绑定的关联
        List<DepartmentVO> voList = super.convertToVoAndBindRelations(entityList, DepartmentVO.class);
        // 返回结果
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    /***
     * 查询ViewObject全部数据 (此为非继承的自定义使用案例，更简化的调用父类案例请参考UserController)
     * <p>
     * url参数示例: /listAll?_orderBy=id&code=TST
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/listAll")
    public JsonResult getAllVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<Department> queryWrapper = buildQuery(request);
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
            .select(Department::getName, Department::getId, Department::getCode);
        List<KeyValue> list = departmentService.getKeyValueList(wrapper);
        return new JsonResult(list);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    public JsonResult createEntity(@ModelAttribute DepartmentVO viewObject, BindingResult result, HttpServletRequest request)
            throws Exception{
        // 转换
        Department entity = BeanUtils.convert(viewObject, Department.class);
        // 创建
        return super.createEntity(entity, result);
    }

    /***
     * 查询Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
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
    public JsonResult updateModel(@PathVariable("id")Long id, @ModelAttribute Organization entity, BindingResult result,
                                  HttpServletRequest request) throws Exception{
        return super.updateEntity(entity, result);
    }

    /***
     * 删除用户
     * @param id 用户ID
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
        return super.deleteEntity(id);
    }

    @Override
    protected BaseService getService() {
        return departmentService;
    }

}
