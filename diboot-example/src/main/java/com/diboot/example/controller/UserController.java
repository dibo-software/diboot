package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.dto.UserDto;
import com.diboot.example.entity.User;
import com.diboot.example.service.UserService;
import com.diboot.example.vo.DepartmentVO;
import com.diboot.example.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User相关Controller
 * @author Mazhicheng
 * @version 2019/05/11
 * Copyright © www.dibo.ltd
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseCrudRestController {

    @Autowired
    private UserService userService;

    /***
     * 查询ViewObject的分页数据 (此为继承父类方法的使用样例，更多自定义案例请参考DepartmentController)
     * <p>
     * url参数示例: /list?pageSize=20&pageIndex=1&orderBy=username&gender=M
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    public JsonResult getVOList(User user, Pagination pagination, HttpServletRequest request) throws Exception{
        QueryWrapper<User> queryWrapper = super.buildQueryWrapper(user);
        return super.getVOListWithPaging(queryWrapper, pagination, UserVO.class);
    }

    @GetMapping("/listDto")
    public JsonResult getVOListWithDTO(UserDto userDto, Pagination pagination, HttpServletRequest request) throws Exception{
        QueryWrapper<User> queryWrapper = super.buildQueryWrapper(userDto);
        // 构建分页
        //Pagination pagination = buildPagination(request);
        // 查询当前页的Entity主表数据
        List entityList = getService().getEntityList(queryWrapper, pagination);
        // 自动转换VO中注解绑定的关联
        List<UserVO> voList = super.convertToVoAndBindRelations(entityList, UserVO.class);
        // 返回结果
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    /***
     * 查询ViewObject的分页数据 (此为继承父类方法的使用样例，更多自定义案例请参考DepartmentController)
     * <p>
     * url参数示例: /listAll?orderBy=username&gender=M
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/listAll")
    public JsonResult getAllVOList(User user, Pagination pagination, HttpServletRequest request) throws Exception{
        QueryWrapper<User> queryWrapper = super.buildQueryWrapper(user);
        return super.getVOListWithPaging(queryWrapper, pagination, UserVO.class);
    }

    @Override
    protected BaseService getService() {
        return userService;
    }

}
