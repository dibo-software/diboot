package com.example.api.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.service.IamPositionService;
import com.diboot.iam.service.IamUserPositionService;
import com.diboot.iam.vo.IamPositionVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/position")
@BindPermission(name = "岗位")
@Slf4j
public class PositionController extends BaseCrudRestController<IamPosition> {
    @Autowired
    private IamPositionService iamPositionService;
    @Autowired
    private IamUserPositionService iamUserPositionService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?field=abc&pageIndex=1&orderBy=abc:DESC
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getViewObjectListMapping(IamPosition entity, Pagination pagination) throws Exception{
        return super.getViewObjectList(entity, pagination, IamPositionVO.class);
    }

    /**
     * 根据 Ids 获取列表
     *
     * @param ids
     * @return
     */
    @PostMapping("/ids")
    public JsonResult getObjectListByIds(@RequestBody List<String> ids) {
        return JsonResult.OK(iamPositionService.getEntityListByIds(ids));
    }

    /**
     * 根据资源id查询ViewObject
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getViewObjectWithMapping(@PathVariable("id") String id) throws Exception{
        return super.getViewObject(id, IamPositionVO.class);
    }

    /**
     * 创建资源对象
     *
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult createEntityWithMapping(@RequestBody @Valid IamPosition entity) throws Exception {
        return super.createEntity(entity);
    }

    /**
     * 根据ID更新资源对象
     *
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityWithMapping(@PathVariable("id") String id, @Valid @RequestBody IamPosition entity) throws Exception {
        return super.updateEntity(id, entity);
    }

    /**
     * 根据id删除资源对象
     *
     * @param id
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntity(@PathVariable("id") String id) throws Exception {
        boolean hasUser = iamUserPositionService.exists(IamUserPosition::getPositionId, id);
        if (hasUser) {
            return JsonResult.FAIL_OPERATION("该岗位下存在有效用户，解除关系后方可删除");
        }
        return super.deleteEntity(id);
    }

    /**
     * 检查编码是否重复
     *
     * @param id
     * @param code
     * @return
     */
    @GetMapping("/check-code-duplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) String id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamPosition> wrapper = Wrappers.<IamPosition>lambdaQuery()
                    .select(IamPosition::getId).eq(IamPosition::getCode, code);
            if (V.notEmpty(id)) {
                wrapper.ne(IamPosition::getId, id);
            }
            boolean exists = iamPositionService.exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION( "编码已存在: "+code);
            }
        }
        return JsonResult.OK();
    }

} 