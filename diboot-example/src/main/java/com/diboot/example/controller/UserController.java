package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.User;
import com.diboot.example.service.UserService;
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
     * 默认Entity的分页实现
     * <p>
     * url参数示例: /list?_pageSize=20&_pageIndex=1&_orderBy=username&gender=M
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    public JsonResult getDefaultVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<User> queryWrapper = buildQuery(request);
        return super.getEntityListWithPaging(request, queryWrapper);
    }

    /***
     * 自定义VO的分页实现
     * <p>
     * url参数示例: /listVo?_pageSize=20&_pageIndex=1&_orderBy=username&gender=M
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/listVo")
    public JsonResult getCustomVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<User> queryWrapper = buildQuery(request);
        return super.getVOListWithPaging(request, queryWrapper, UserVO.class);
    }

    @Override
    protected BaseService getService() {
        return userService;
    }

}
