package com.diboot.shiro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.service.PermissionService;
import com.diboot.shiro.vo.PermissionVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/permission")
public class PermissionController extends BaseCrudRestController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private PermissionService permissionService;

    /***
     * 查询ViewObject的分页数据 (此为非继承的自定义使用案例，更简化的调用父类案例请参考UserController)
     * <p>
     * url参数示例: /list?_pageSize=20&_pageIndex=1&_orderBy=id&code=TST
     * </p>
     * @return
     * @throws Exception
     */
    @RequiresPermissions("permission:list")
    @GetMapping("/list")
    public JsonResult getVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<Permission> queryWrapper = buildQuery(request);
        // 构建分页
        Pagination pagination = buildPagination(request);
        // 查询当前页的Entity主表数据
        List entityList = getService().getEntityList(queryWrapper, pagination);
        // 自动转换VO中注解绑定的关联
        List<PermissionVO> voList = super.convertToVoAndBindRelations(entityList, PermissionVO.class);

        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @RequiresPermissions("permission:add")
    @PostMapping("/")
    public JsonResult createEntity(@ModelAttribute PermissionVO viewObject, BindingResult result, HttpServletRequest request, ModelMap modelMap)
            throws Exception{
        // 转换
        Permission entity = BeanUtils.convert(viewObject, Permission.class);
        // 创建
        return super.createEntity(entity, result, modelMap);
    }

    /***
     * 查询Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @RequiresPermissions("permission:get")
    @GetMapping("/{id}")
    public JsonResult getModel(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap)
            throws Exception{
        PermissionVO vo = permissionService.getViewObject(id, PermissionVO.class);
        return new JsonResult(vo);
    }

    /***
     * 更新Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @RequiresPermissions("permission:update")
    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable("id")Long id, @ModelAttribute Permission entity, BindingResult result,
                                  HttpServletRequest request, ModelMap modelMap) throws Exception{
        return super.updateEntity(entity, result, modelMap);
    }

    /***
     * 删除用户
     * @param id 用户ID
     * @return
     * @throws Exception
     */
    @RequiresPermissions("permission:delete")
    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
        return super.deleteEntity(id);
    }

    @Override
    protected BaseService getService() {
        return permissionService;
    }

}
