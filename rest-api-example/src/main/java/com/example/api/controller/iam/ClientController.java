package com.example.api.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.dto.ClientCredential;
import com.diboot.iam.entity.Client;
import com.diboot.iam.service.ClientService;
import com.diboot.iam.vo.ClientVO;
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
@RequestMapping("/client")
@BindPermission(name = "客户端")
public class ClientController extends BaseCrudRestController<Client> {

    @Autowired
    private ClientService clientService;

    /**
     * 客户端登录获取token
     *
     * @param credential 登录凭证
     * @return 响应（troken）
     */
    @PostMapping("/login")
    public JsonResult<String> login(@RequestBody ClientCredential credential) {
        return JsonResult.OK(AuthServiceFactory.getAuthService(ClientCredential.AUTH_TYPE).applyToken(credential));
    }

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
    public JsonResult<List<ClientVO>> getListVOMapping(Client queryDto, Pagination pagination) throws Exception {
        return super.getViewObjectList(queryDto, pagination, ClientVO.class);
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
    public JsonResult<ClientVO> getDetailVOMapping(@PathVariable("id") String id) throws Exception {
        return super.getViewObject(id, ClientVO.class);
    }

    /**
     * 创建资源对象数据
     *
     * @param client
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult<?> createModelMapping(@RequestBody Client client) throws Exception {
        return super.createEntity(client);
    }

    /**
     * 根据id更新资源对象
     *
     * @param client
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult<?> updateModelMapping(@PathVariable("id") String id, @RequestBody Client client) throws Exception {
        return super.updateEntity(id, client);
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
        boolean success = clientService.deleteEntity(id);
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
        boolean success = clientService.deleteEntities(ids);
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
        QueryWrapper<Object> wrapper = Wrappers.query().eq(S.toSnakeCase(field), value);
        if (V.notEmpty(id)) {
            wrapper.ne(ContextHolder.getIdFieldName(getEntityClass()), id);
        }
        boolean isUnique = !clientService.exists(wrapper);
        return JsonResult.OK(isUnique);
    }
}
