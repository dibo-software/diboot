package com.example.api.controller.iam;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.dto.IamLoginTraceDTO;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.service.IamLoginTraceService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamLoginTraceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录日志
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/login-trace")
@Slf4j
@BindPermission(name = "登录日志")
public class LoginTraceController extends BaseCrudRestController<IamLoginTrace> {

    @Autowired
    private IamLoginTraceService iamLoginTraceService;

    /**
     * 查询分页数据
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getViewObjectListMapping(IamLoginTraceDTO entity, Pagination pagination) throws Exception {
        JsonResult<List<IamLoginTraceVO>> result =  super.getViewObjectList(entity, pagination, IamLoginTraceVO.class);
        // 对结果附加状态
        List<IamLoginTraceVO> voList = result.getData();
        iamLoginTraceService.appendLoginStatus(voList);
        return result;
    }

    /**
     * 强制退出
     * @param id 登录日志记录id
     * @return
     * @throws Exception
     */
    @Log(operation = "强制退出")
    @BindPermission(name = "强制退出", code = "FORCE_LOGOUT")
    @PostMapping("/force-logout/{id}")
    public JsonResult forceLogout(@PathVariable(name = "id") String id) throws Exception {
        if (V.isEmpty(id)) {
            return JsonResult.FAIL_OPERATION("参数错误");
        }
        IamLoginTrace loginTrace = iamLoginTraceService.getEntity(id);
        if (loginTrace == null){
            return JsonResult.FAIL_OPERATION("参数错误");
        }
        String userTypeAndId = loginTrace.getUserType() + ":" + loginTrace.getUserId();
        IamSecurityUtils.logout(userTypeAndId);
        return JsonResult.OK();
    }

}