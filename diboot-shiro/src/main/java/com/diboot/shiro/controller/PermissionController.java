package com.diboot.shiro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import com.diboot.shiro.authz.config.SystemParamConfig;
import com.diboot.shiro.dto.PermissionDto;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.service.PermissionService;
import com.diboot.shiro.vo.PermissionVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 权限资源相关Controller
 * @author Wangyongliang
 * @version v2.0
 * @date 2019/6/20
 */
@RestController
@RequestMapping("/permission")
@AuthorizationPrefix(prefix = "permission", code = "permission", name = "权限管理")
public class PermissionController extends BaseCrudRestController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SystemParamConfig systemParamConfig;

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
    public JsonResult getVOList(PermissionDto permissionDto, Pagination pagination, HttpServletRequest request) throws Exception{
        QueryWrapper<PermissionDto> queryWrapper = super.buildQueryWrapper(permissionDto);
        // 查询当前页的Entity主表数据
        List<Permission> entityList = permissionService.getPermissionList(queryWrapper, pagination);
        return new JsonResult(Status.OK, entityList).bindPagination(pagination);
    }

    /***
     * 查询Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("read"), name = "查看")
    public JsonResult getModel(@PathVariable("id")Long id, HttpServletRequest request)
            throws Exception{
        PermissionVO vo = permissionService.getViewObject(id, PermissionVO.class);
        return new JsonResult(vo);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    @AuthorizationWrapper(value = @RequiresPermissions("create"), name = "新建")
    public JsonResult createEntity(@ModelAttribute PermissionVO viewObject, BindingResult result, HttpServletRequest request)
            throws Exception{
        // 转换
        Permission entity = BeanUtils.convert(viewObject, Permission.class);
        // 创建
        entity.setApplication(systemParamConfig.getApplication());
        return super.createEntity(entity, result);
    }

    /***
     * 更新Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("update"), name = "更新")
    public JsonResult updateModel(@PathVariable("id")Long id, @ModelAttribute Permission entity, BindingResult result,
                                  HttpServletRequest request) throws Exception{
        entity.setApplication(systemParamConfig.getApplication());
        return super.updateEntity(entity, result);
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

    @Override
    protected BaseService getService() {
        return permissionService;
    }

}
