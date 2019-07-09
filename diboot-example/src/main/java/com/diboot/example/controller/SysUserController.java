package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.manager.AnnotationBindingManager;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.SysUser;
import com.diboot.example.service.DepartmentService;
import com.diboot.example.service.SysUserService;
import com.diboot.example.vo.SysUserListVO;
import com.diboot.example.vo.SysUserVO;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.service.RoleService;
import com.diboot.shiro.util.JwtHelper;
import com.diboot.shiro.vo.RoleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/sysUser")
public class SysUserController extends BaseCrudRestController {

    private static final Logger logger = LoggerFactory.getLogger(SysUserController.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    public JsonResult getVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<SysUser> queryWrapper = buildQuery(request);
        // 构建分页
        Pagination pagination = buildPagination(request);
        // 查询当前页的Entity主表数据
        List<SysUserVO> voList = sysUserService.getSysUserList(queryWrapper, pagination);
        //筛选出在列表页展示的字段
        List<SysUserListVO> userVoList = AnnotationBindingManager.autoConvertAndBind(voList, SysUserListVO.class);
        // 返回结果
        return new JsonResult(Status.OK, userVoList).bindPagination(pagination);
    }


    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    public JsonResult createEntity(@RequestBody SysUser entity, BindingResult result, HttpServletRequest request, ModelMap modelMap)
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
    public JsonResult updateModel(@PathVariable("id")Long id, @RequestBody SysUser entity, BindingResult result,
                                  HttpServletRequest request, ModelMap modelMap) throws Exception{
        // Model属性值验证结果
        if(result.hasErrors()) {
            return new JsonResult(Status.FAIL_INVALID_PARAM, super.getBindingError(result));
        }
        if(modelMap.get(ERROR) != null){
            return new JsonResult(Status.FAIL_VALIDATION, (String) modelMap.get(ERROR));
        }

        entity.setId(id);
        boolean success = sysUserService.updateSysUser(entity);
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
    public JsonResult getModel(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap)
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

        //获取部门KV
        wrapper = new QueryWrapper<Department>()
                .lambda()
                .select(Department::getName, Department::getId);
        List<KeyValue> departmentKvList = departmentService.getKeyValueList(wrapper);
        modelMap.put("departmentKvList", departmentKvList);

        //获取用户状态KV
        List<KeyValue> userStatusKvList = dictionaryService.getKeyValueList(SysUser.USER_STATUS);
        modelMap.put("userStatusKvList", userStatusKvList);

        //获取用户性别KV
        List<KeyValue> genderKvList = dictionaryService.getKeyValueList(SysUser.GENDER);
        modelMap.put("genderKvList", genderKvList);

        return new JsonResult(modelMap);
    }

    /*
    * 校验用户名是否重复
    * */
    @GetMapping("/checkUsernameRepeat")
    public JsonResult checkUsernameRepeat(@RequestParam("id") Long id,@RequestParam("username") String username, HttpServletRequest request){
        if(V.notEmpty(username)){
            QueryWrapper<SysUser> wrapper = new QueryWrapper();
            wrapper.lambda().eq(SysUser::getUsername, username);
            List<SysUser> sysUserList = sysUserService.getEntityList(wrapper);
            if(V.isEmpty(id)){//新建时
                if(V.notEmpty(sysUserList)){
                    return new JsonResult(Status.FAIL_OPERATION, "用户名已存在");
                }
            }else{//更新时
                SysUser sysUser = sysUserService.getEntity(id);
                if(V.notEmpty(sysUser)){
                    if(V.notEmpty(sysUserList)){
                        if(sysUserList.size() >= 2){
                            return new JsonResult(Status.FAIL_OPERATION, "用户名已存在");
                        }else if(!(sysUser.getId().equals(sysUserList.get(0).getId()))){
                            return new JsonResult(Status.FAIL_OPERATION, "用户名已存在");
                        }
                    }
                }else{
                    if(V.notEmpty(sysUserList)){
                        return new JsonResult(Status.FAIL_OPERATION, "用户名已存在");
                    }
                }
            }

        }

        return new JsonResult(Status.OK);
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
            return new JsonResult(Status.FAIL_OPERATION, new String[]{"获取用户角色失败"});
        }

        user.setRoleVOList(roleVOList);

        return new JsonResult(Status.OK, user, new String[]{"获取角色列表成功"});
    }

    @Override
    protected BaseService getService() {
        return sysUserService;
    }
}
