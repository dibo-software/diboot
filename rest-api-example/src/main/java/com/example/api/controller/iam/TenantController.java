package com.example.api.controller.iam;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamUserFormDTO;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.service.IamResourceService;
import com.diboot.iam.vo.IamResourceListVO;
import com.diboot.tenant.entity.IamTenant;
import com.diboot.tenant.entity.IamTenantResource;
import com.diboot.tenant.service.IamTenantService;
import com.diboot.tenant.vo.IamTenantDetailVO;
import com.diboot.tenant.vo.IamTenantListVO;
import com.diboot.tenant.vo.TenantAdminUserVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 租户 相关rest接口
 *
 * @author JerryMa
 * @version 1.0
 * @date 2023-12-21
 * Copyright © MyCompany
 */
@Slf4j
@RequestMapping("/iam/tenant")
@BindPermission(name = "租户")
@RestController
public class TenantController extends BaseCrudRestController<IamTenant> {

    @Autowired
    private IamTenantService iamTenantService;
    @Autowired
    private IamResourceService iamResourceService;
    @Autowired
    private IamAccountService iamAccountService;
    @Autowired
    private IamOrgService iamOrgService;

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
    public JsonResult<List<IamTenantListVO>> getListVOMapping(IamTenant queryDto, Pagination pagination) throws Exception {
        return super.getViewObjectList(queryDto, pagination, IamTenantListVO.class);
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
    public JsonResult<IamTenantDetailVO> getDetailVOMapping(@PathVariable("id") String id) throws Exception {
        IamTenantDetailVO detailVO = iamTenantService.getViewObject(id, IamTenantDetailVO.class);
        return JsonResult.OK(detailVO);
    }


    /**
     * 创建资源对象数据
     *
     * @param iamTenant
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping()
    public JsonResult<?> createModelMapping(@RequestBody IamTenant iamTenant) throws Exception {
        return JsonResult.OK(iamTenantService.createTenantAndInitData(iamTenant));
    }


    /**
     * 根据id更新资源对象
     *
     * @param iamTenant
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult<?> updateModelMapping(@PathVariable("id") String id, @RequestBody IamTenant iamTenant) throws Exception {
        boolean success = iamTenantService.updateEntity(iamTenant);
        log.debug("更新数据 租户:{} {}", id, success ? "成功" : "失败");
        return JsonResult.OK(success);
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
        boolean success = iamTenantService.deleteEntity(id);
        log.debug("删除数据 租户:{} {}", id, success ? "成功" : "失败");
        return JsonResult.OK(success);
    }

    /**
     * 获取当前租户管理员
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/admin/{tenantId}")
    public JsonResult<TenantAdminUserVO> getTenantAdminUser(@PathVariable("tenantId") String tenantId) throws Exception {
        return JsonResult.OK(iamTenantService.getTenantAdminUserVO(tenantId));
    }

    /**
     * 获取当前租户org root
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/org/{tenantId}")
    public JsonResult<String> getTenantOrg(@PathVariable("tenantId") String tenantId) throws Exception {
        return JsonResult.OK(iamOrgService.getTenantRootOrgId(tenantId));
    }

    /**
     * 创建或更新当前租户管理员
     *
     * @return
     * @throws Exception
     */
    @Log(operation = "创建或更新管理员信息")
    @PostMapping("/admin/{tenantId}")
    public JsonResult<?> createOrUpdateTenantAdmin(@RequestBody IamUserFormDTO iamUserFormDTO, @PathVariable("tenantId") String tenantId) throws Exception {
        iamUserFormDTO.setTenantId(tenantId);
        return new JsonResult<>(iamTenantService.createOrUpdateTenantAdminUser(iamUserFormDTO));
    }

    /**
     * 校验用户名是否重复
     *
     * @param tenantId
     * @param username
     * @param userId
     * @return
     */
    @GetMapping("/admin/check-username-duplicate")
    public JsonResult<?> checkUsernameDuplicate(@RequestParam String tenantId, @RequestParam String username, @RequestParam(required = false) String userId) {
        if (V.isEmpty(username)) {
            return JsonResult.OK();
        }
        return JsonResult.OK(!iamAccountService.isAccountExists(tenantId, IamUser.class.getSimpleName(), username, userId));
    }

    /**
     * 获取资源权限树
     *
     * @return 租户可分配权限
     */
    @GetMapping("/resource")
    public JsonResult<List<IamResourceListVO>> getResourceTree() throws Exception {
        List<IamResourceListVO> voList = iamResourceService.getViewObjectList(
                Wrappers.<IamResource>lambdaQuery().orderByAsc(IamResource::getSortId), null, IamResourceListVO.class);
        Map<String, Object> paramsMap = getParamsMap();
        if (!paramsMap.containsKey("displayName") && !paramsMap.containsKey("resourceCode")) {
            voList = BeanUtils.buildTree(voList, Cons.TREE_ROOT_ID);
        }
        return JsonResult.OK(voList);
    }

    /**
     * 获取当前租户权限配置
     *
     * @return
     * @throws Exception
     */
    @Log(operation = "获取租户权限配置")
    @GetMapping("/resource/{tenantId}")
    public JsonResult<List<String>> getTenantResourceVO(@PathVariable("tenantId") String tenantId) {
        return JsonResult.OK(iamTenantService.getResourceIds(tenantId));
    }

    /**
     * 更新租户权限配置
     *
     * @return
     * @throws Exception
     */
    @Log(operation = "更新租户权限配置")
    @PostMapping("/resource/{tenantId}")
    public JsonResult<?> createOrUpdateTenantResources(@PathVariable("tenantId") String tenantId, @RequestBody List<String> ids) {
        return new JsonResult<>(iamTenantService.createOrUpdateN2NRelations(IamTenantResource::getTenantId, tenantId,
                IamTenantResource::getResourceId, ids));
    }

    /**
     * 判断租户code是否重复
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/check-code-duplicate")
    public JsonResult<?> checkCodeDuplicate(@RequestParam(required = false) String id, @RequestParam String code) {
        return JsonResult.OK(!iamTenantService.exists(Wrappers.<IamTenant>lambdaQuery().eq(IamTenant::getCode, code)
                .ne(V.notEmpty(id), IamTenant::getId, id)));
    }

}
