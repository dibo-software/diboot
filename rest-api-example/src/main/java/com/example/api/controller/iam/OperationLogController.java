package com.example.api.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.entity.IamClient;
import com.diboot.iam.entity.IamOperationLog;
import com.diboot.iam.entity.IamUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* 操作日志相关Controller
* @author MyName
* @version 1.0
* @date 2022-12-30
* Copyright © MyCompany
*/
@Slf4j
@BindPermission(name = "操作日志")
@RestController
@RequestMapping("/iam/operation-log")
public class OperationLogController extends BaseCrudRestController<IamOperationLog> {

    /**
    * 查询ViewObject的分页数据
    * <p>
    * url请求参数示例: ?field=abc&pageSize=20&pageIndex=1&orderBy=id
    * </p>
    * @return
    * @throws Exception
    */
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getViewObjectListMapping(IamOperationLog entity, String filterType, Pagination pagination) throws Exception{
        if(pagination != null && V.isEmpty(pagination.getOrderBy())) {
            pagination.setOrderBy(Pagination.ORDER_BY_ID_DESC);
        }
        QueryWrapper<IamOperationLog> queryWrapper = super.buildQueryWrapperByDTO(entity);
        // 处理 filterType
        if(V.notEmpty(filterType)) {
            switch (filterType) {
                case "business":
                    queryWrapper.lambda().eq(IamOperationLog::getUserType, IamUser.class.getSimpleName());
                    break;
                case "client":
                    queryWrapper.lambda().eq(IamOperationLog::getUserType, IamClient.class.getSimpleName());
                    break;
                case "exception":
                    queryWrapper.lambda().in(IamOperationLog::getStatusCode, 500, 5000);
                    break;
                default:
                    log.warn("未知的 filterType:{}", filterType);
            }
        }
        return super.getEntityListWithPaging(queryWrapper, pagination);
    }

    /**
    * 根据资源id查询ViewObject
    * @param id ID
    * @return
    * @throws Exception
    */
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") String id) throws Exception{
        IamOperationLog operationLog = super.getEntity(id);
        return JsonResult.OK(operationLog);
    }
}
