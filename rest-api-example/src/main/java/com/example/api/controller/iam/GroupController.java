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
import com.diboot.iam.entity.IamGroup;
import com.diboot.iam.vo.IamGroupVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 岗位 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2025-05-28
 * Copyright © MyCompany
 */
@Slf4j
@RestController
@RequestMapping("/iam/group")
@BindPermission(name = "用户组")
public class GroupController extends BaseCrudRestController<IamGroup> {

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
    public JsonResult<?> getViewObjectListMapping(IamGroup entity, Pagination pagination) throws Exception {
        return super.getViewObjectList(entity, pagination, IamGroupVO.class);
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
    public JsonResult<?> getViewObjectWithMapping(@PathVariable("id") String id) throws Exception {
        return super.getViewObject(id, IamGroupVO.class);
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
    public JsonResult<?> createEntityWithMapping(@RequestBody @Valid IamGroup entity) throws Exception {
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
    public JsonResult<?> updateEntityWithMapping(@PathVariable("id") String id, @Valid @RequestBody IamGroup entity) throws Exception {
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
    public JsonResult<?> deleteEntity(@PathVariable("id") String id) throws Exception {
        return super.deleteEntity(id);
    }

    /**
     * 检查编码是否重复
     *
     * @param id
     * @param name
     * @return
     */
    @GetMapping("/check-name-duplicate")
    public JsonResult<?> checkCodeDuplicate(@RequestParam(required = false) String id, @RequestParam String name) {
        if (V.notEmpty(name)) {
            LambdaQueryWrapper<IamGroup> wrapper = Wrappers.<IamGroup>lambdaQuery()
                    .select(IamGroup::getId).eq(IamGroup::getName, name);
            if (V.notEmpty(id)) {
                wrapper.ne(IamGroup::getId, id);
            }
            boolean exists = getService().exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION("名称已存在: " + name);
            }
        }
        return JsonResult.OK();
    }

}
