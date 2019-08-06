package com.diboot.shiro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.shiro.authz.annotation.AuthorizationCache;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.service.RoleService;
import com.diboot.shiro.vo.RoleVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/role")
@AuthorizationPrefix(prefix = "role", code = "role", name = "权限")
public class RoleController extends BaseCrudRestController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private DictionaryService dictionaryService;

    @Override
    protected BaseService getService() {
        return roleService;
    }

    /***
     * 获取Entity列表（分页）
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    @AuthorizationWrapper(value = @RequiresPermissions("list"), name = "列表")
    @AuthorizationCache
    public JsonResult getVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<Role> queryWrapper = buildQuery(request);
        // 构建分页
        Pagination pagination = buildPagination(request);
        // 获取结果
        List<RoleVO> voList = roleService.getRoleList(queryWrapper, pagination);
        // 返回结果
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    /***
     * 显示创建页面
     * @return
     * @throws Exception
     */
    @GetMapping("/toCreatePage")
    public JsonResult toCreatePage(HttpServletRequest request, ModelMap modelMap)
            throws Exception{
        List<Permission> menuList = roleService.getAllMenu();
        modelMap.put("menuList", menuList);
        return new JsonResult(modelMap);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    public JsonResult createEntity(@RequestBody Role entity, BindingResult result, HttpServletRequest request)
            throws Exception{
        // 创建
        boolean success = roleService.createRole(entity);
        if(success){
            return new JsonResult(Status.OK);
        }else{
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }

    /***
     * 显示更新页面
     * @return
     * @throws Exception
     */
    @GetMapping("/toUpdatePage/{id}")
    public JsonResult toUpdatePage(@PathVariable("id")Long id, HttpServletRequest request)
            throws Exception{
        RoleVO roleVO = roleService.toUpdatePage(id);
        return new JsonResult(roleVO);
    }


    /***
     * 更新Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable("id")Long id, @RequestBody Role entity, BindingResult result,
                                  HttpServletRequest request) throws Exception{
        // Model属性值验证结果
        if(result.hasErrors()) {
            return new JsonResult(Status.FAIL_INVALID_PARAM, super.getBindingError(result));
        }

        entity.setId(id);
        boolean success = roleService.updateRole(entity);

        if(success){
            return new JsonResult(Status.OK);
        }else{
            return new JsonResult(Status.FAIL_OPERATION);
        }
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
        RoleVO roleVO = roleService.getRole(id);
        return new JsonResult(roleVO);
    }

    /***
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
        boolean success = roleService.deleteRole(id);
        if(success){
            return new JsonResult(Status.OK);
        }else{
            return new JsonResult(Status.FAIL_OPERATION );
        }
    }

    /***
     * 获取所有菜单,以及每个菜单下的所有权限
     * @return
     * @throws Exception
     */
    @GetMapping("/getAllMenu")
    public JsonResult getAllMenu(HttpServletRequest request)
            throws Exception{
        List<Permission> list = roleService.getAllMenu();
        return new JsonResult(list);
    }

    /***
     * 加载更多数据
     * @return
     * @throws Exception
     */
    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap)
            throws Exception{

        //获取角色状态KV
        List<KeyValue> roleStatusKvList = dictionaryService.getKeyValueList(Role.METATYPE_STATUS);
        modelMap.put("roleStatusKvList", roleStatusKvList);

        return new JsonResult(modelMap);
    }


    /*
     * 校验角色code是否重复
     * */
    @GetMapping("/checkCodeRepeat")
    public JsonResult checkCodeRepeat(Long id, String code, HttpServletRequest request){
        if(V.notEmpty(code)){
            QueryWrapper<Role> wrapper = new QueryWrapper();
            wrapper.lambda().eq(Role::getCode, code);
            List<Role> roleList = roleService.getEntityList(wrapper);
            if(V.isEmpty(id)){//新建时
                if(V.notEmpty(roleList)){
                    return new JsonResult(Status.FAIL_OPERATION, "code已存在");
                }
            }else{//更新时
                Role role = roleService.getEntity(id);
                if(V.notEmpty(role)){
                    if(V.notEmpty(roleList)){
                        if(roleList.size() >= 2){
                            return new JsonResult(Status.FAIL_OPERATION, "code已存在");
                        }else if(!(role.getId().equals(roleList.get(0).getId()))){
                            return new JsonResult(Status.FAIL_OPERATION, "code已存在");
                        }
                    }
                }else{
                    if(V.notEmpty(roleList)){
                        return new JsonResult(Status.FAIL_OPERATION, "code已存在");
                    }
                }
            }

        }

        return new JsonResult(Status.OK);
    }


}
