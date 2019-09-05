package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.dto.SysUserDto;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.SysUser;
import com.diboot.example.entity.User;
import com.diboot.example.service.DepartmentService;
import com.diboot.example.service.SysUserService;
import com.diboot.example.vo.SysUserListVO;
import com.diboot.example.vo.SysUserVO;
import com.diboot.example.vo.UserVO;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.service.PermissionService;
import com.diboot.shiro.service.RoleService;
import com.diboot.shiro.util.JwtHelper;
import com.diboot.shiro.vo.RoleVO;
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
 * 用户相关Controller
 * @author wangyonglaing
 * @version 2.0
 * @time 2018/8/5
 */
@RestController
@RequestMapping("/sysUser")
@AuthorizationPrefix(name = "用户管理", code = "sysUser", prefix = "sysUser")
public class SysUserController extends BaseCrudRestController {

    private static final Logger logger = LoggerFactory.getLogger(SysUserController.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /***
     * 获取列表页数据
     * @param sysUserDto
     * @param pagination
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    @AuthorizationWrapper(value = @RequiresPermissions("list"), name = "列表")
    public JsonResult getVOList(SysUserDto sysUserDto, Pagination pagination, HttpServletRequest request) throws Exception{
        QueryWrapper<SysUserDto> queryWrapper = super.buildQueryWrapper(sysUserDto);
        return super.getVOListWithPaging(queryWrapper, pagination, SysUserVO.class);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    @AuthorizationWrapper(value = @RequiresPermissions("create"), name = "新建")
    public JsonResult createEntity(@RequestBody SysUser entity, BindingResult result, HttpServletRequest request)
            throws Exception{
        boolean success = sysUserService.createSysUser(entity);
        if(success){
            return new JsonResult(Status.OK);
        }else{
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }


    /***
     * 更新Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("update"), name = "更新")
    public JsonResult updateModel(@PathVariable("id")Long id, @RequestBody SysUser entity, BindingResult result,
                                  HttpServletRequest request) throws Exception{
        // Model属性值验证结果
        if(result.hasErrors()) {
            return new JsonResult(Status.FAIL_INVALID_PARAM, super.getBindingError(result));
        }
        entity.setId(id);
        boolean success = sysUserService.updateSysUser(entity);
        if(success){
            return new JsonResult(Status.OK, "更新成功");
        }else{
            return new JsonResult(Status.FAIL_OPERATION, "更新失败");
        }
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
        SysUserVO sysUserVO = sysUserService.getSysUser(id);
        return new JsonResult(sysUserVO);
    }

    /***
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("delete"), name = "删除")
    public JsonResult deleteModel(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
        boolean success = sysUserService.deleteSysUser(id);
        if(success){
            return new JsonResult(Status.OK);
        }else{
            return new JsonResult(Status.FAIL_OPERATION );
        }
    }

    /***
     * 加载更多数据
     * @return
     * @throws Exception
     */
    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap)
            throws Exception{
        Wrapper wrapper = null;
        //获取角色KV
        wrapper = new QueryWrapper<Role>()
                        .lambda()
                        .select(Role::getName, Role::getId);
        List<KeyValue> roleKvList = roleService.getKeyValueList(wrapper);
        modelMap.put("roleKvList", roleKvList);

        //获取用户状态KV
        List<KeyValue> statusKvList = dictionaryService.getKeyValueList(SysUser.USER_STATUS);
        modelMap.put("statusKvList", statusKvList);

        //获取用户性别KV
        List<KeyValue> genderKvList = dictionaryService.getKeyValueList(SysUser.GENDER);
        modelMap.put("genderKvList", genderKvList);

        return new JsonResult(modelMap);
    }

    /*
    * 校验用户名是否重复
    * */
    @GetMapping("/checkUsernameRepeat")
    public JsonResult checkUsernameRepeat(@RequestParam(required = false) Long id,@RequestParam String username, HttpServletRequest request){
        if(V.isEmpty(username)){
            return new JsonResult(Status.OK);
        }
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        if (id != null){
            queryWrapper.ne(SysUser::getId, id);
        }

        List<SysUser> sysUserList = sysUserService.getEntityList(queryWrapper);
        if (V.isEmpty(sysUserList)){
            return new JsonResult(Status.OK, "用户名可用");
        }
        return new JsonResult(Status.FAIL_OPERATION, "用户名已存在");
    }


    /***
     * 获取登录用户信息
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/info")
    public JsonResult info(HttpServletRequest request) throws Exception{
        String token = JwtHelper.getRequestToken(request);
        if (V.isEmpty(token)){
            return new JsonResult(Status.FAIL_OPERATION, new String[]{"获取数据失败"});
        }

        String username = JwtHelper.getAccountFromToken(token);
        if (V.isEmpty(username)){
            return new JsonResult(Status.FAIL_OPERATION, new String[]{"获取数据失败"});
        }

        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda()
                .eq(SysUser::getUsername, username);
        List<SysUser> userList = sysUserService.getEntityList(query);
        if (V.isEmpty(userList)){
            return new JsonResult(Status.FAIL_OPERATION, new String[]{"获取数据失败"});
        }

        SysUser user = userList.get(0);

        List<RoleVO> roleVOList = roleService.getRelatedRoleAndPermissionListByUser(SysUser.class.getSimpleName(), user.getId());
        if (V.isEmpty(roleVOList)){
            return new JsonResult(Status.FAIL_OPERATION, new String[]{"用户未配置角色，获取数据失败"});
        }

        // 如果具有管理员角色，则赋予所有权限
        for (RoleVO roleVO : roleVOList){
            if (roleVO.isAdmin()){
                List<Permission> allPermissionList = permissionService.getEntityList(null);
                roleVO.setPermissionList(allPermissionList);
                break;
            }
        }

        user.setRoleVOList(roleVOList);

        return new JsonResult(Status.OK, user, new String[]{"获取角色列表成功"});
    }

    @Override
    protected BaseService getService() {
        return sysUserService;
    }
}
