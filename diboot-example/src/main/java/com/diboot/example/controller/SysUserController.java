package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.service.RoleService;
import com.diboot.shiro.service.SysUserService;
import com.diboot.shiro.util.JwtHelper;
import com.diboot.shiro.vo.RoleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    private static final Logger logger = LoggerFactory.getLogger(SysUserController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private SysUserService sysUserService;

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
}
