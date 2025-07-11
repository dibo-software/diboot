package com.example.api.controller.iam;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.entity.IamClient;
import com.diboot.iam.service.IamClientService;
import com.diboot.iam.vo.IamClientVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 三方应用接口服务 相关rest接口
 *
 * @author JerryMa
 * @version 1.0
 * @date 2025-01-13
 * Copyright © MyCompany
 */
@Slf4j
@RestController
@RequestMapping("/iam/client")
@BindPermission(name = "客户端")
public class ClientController extends BaseCrudRestController<IamClient> {

    @Autowired
    private IamClientService iamClientService;

    /**
     * 查询资源对象的列表VO记录
     * <p>
     * url请求参数示例: ?fieldA=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping()
    public JsonResult<List<IamClientVO>> getListVOMapping(IamClient queryDto, Pagination pagination) throws Exception {
        return super.getViewObjectList(queryDto, pagination, IamClientVO.class);
    }

    /**
     * 根据id查询资源对象的详情VO
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult<IamClientVO> getDetailVOMapping(@PathVariable("id") String id) throws Exception {
        return super.getViewObject(id, IamClientVO.class);
    }

    /**
     * 创建资源对象数据
     *
     * @param iamClient
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult<?> createModelMapping(@RequestBody IamClient iamClient) throws Exception {
        return super.createEntity(iamClient);
    }

    /**
     * 根据id更新资源对象
     *
     * @param iamClient
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult<?> updateModelMapping(@PathVariable("id") String id, @RequestBody IamClient iamClient) throws Exception {
        return super.updateEntity(id, iamClient);
    }

    /**
     * 根据id删除资源对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult<?> deleteModelMapping(@PathVariable("id") String id) throws Exception {
        boolean success = iamClientService.deleteEntity(id);
        log.debug("删除数据 客户端:{} {}", id, success ? "成功" : "失败");
        return JsonResult.OK();
    }

    /**
     * 根据ids集合删除资源对象
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_BATCH_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @PostMapping("/batch-delete")
    public JsonResult<?> deleteEntityMapping(@RequestBody List<String> ids) throws Exception {
        boolean success = iamClientService.deleteEntities(ids);
        log.debug("批量删除数据 客户端:{} {}", ids, success ? "成功" : "失败");
        return JsonResult.OK();
    }

    /**
     * 检查数据唯一性
     *
     * @param id
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    @GetMapping("/check-unique")
    public JsonResult<?> checkUnique(@RequestParam(required = false) String id, @RequestParam String field, @RequestParam String value) throws Exception {
        if (V.isEmpty(value)) {
            return JsonResult.FAIL_VALIDATION("待检查字段值不能为空");
        }
        boolean isUnique = iamClientService.isValueUnique(field, value, id);
        return JsonResult.OK(isUnique);
    }
}
