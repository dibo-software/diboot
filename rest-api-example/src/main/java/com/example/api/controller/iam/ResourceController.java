package com.example.demo.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.dto.SortParamDTO;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.cache.IamPermissionCacheManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamResourceDTO;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.service.IamResourceService;
import com.diboot.iam.vo.IamResourceListVO;
import com.diboot.iam.vo.IamResourceVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * 系统资源权限相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/resource")
@Slf4j
@BindPermission(name = "系统资源权限")
public class ResourceController extends BaseCrudRestController<IamResource> {

    @Autowired
    private IamResourceService iamResourceService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getViewObjectListMapping(IamResource entity, Pagination pagination) throws Exception {
        return super.getViewObjectList(entity, pagination, IamResourceListVO.class);
    }

    /**
     * 获取菜单树
     *
     * @return
     */
    @GetMapping("/menu-tree")
    public JsonResult getMenuTreeList() {
        LambdaQueryWrapper<IamResource> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.ne(IamResource::getDisplayType, "PERMISSION");
        queryWrapper.orderByAsc(IamResource::getSortId).orderByAsc(IamResource::getId);
        List<IamResourceListVO> list = iamResourceService.getViewObjectList(queryWrapper, null, IamResourceListVO.class);
        return JsonResult.OK(BeanUtils.buildTree(list, Cons.TREE_ROOT_ID));
    }

    @GetMapping("/tree")
    public JsonResult getTreeList(IamResourceDTO entity) throws Exception {
        String appModule = entity.getAppModule();
        entity.setAppModule(null);
        QueryWrapper<IamResource> queryWrapper = super.buildQueryWrapperByDTO(entity);
        if (V.equals(appModule, Cons.RESOURCE_APP_MODULE.PC.name())) {
            queryWrapper.lambda().and(qw -> qw.eq(IamResource::getAppModule, appModule).or().isNull(IamResource::getAppModule));
        } else if (V.equals(appModule, Cons.RESOURCE_APP_MODULE.MOBILE.name())){
            queryWrapper.lambda().eq(IamResource::getAppModule, appModule);
        }
        queryWrapper.lambda().orderByAsc(IamResource::getSortId).orderByAsc(IamResource::getId);
        List<IamResourceListVO> list = iamResourceService.getViewObjectList(queryWrapper, null, IamResourceListVO.class);
        List<IamResourceListVO> tree = BeanUtils.buildTree(list, Cons.TREE_ROOT_ID);
        tree.sort(Comparator.comparing(IamResource::getAppModule, Comparator.nullsFirst(Comparator.naturalOrder())));
        return JsonResult.OK(tree);
    }

    /**
     * 根据资源id查询ViewObject
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") String id) throws Exception {
        return super.getViewObject(id, IamResourceVO.class);
    }

    /**
     * 新建菜单项、按钮/权限列表
     * @param IamResourceDTO
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult createEntityMapping(@Valid @RequestBody IamResourceDTO IamResourceDTO) throws Exception {
        iamResourceService.createMenuResources(IamResourceDTO);
        return JsonResult.OK(IamResourceDTO.getId());
    }

    /**
     * 更新用户、账号和用户角色关联列表
     * @param IamResourceDTO
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") String id, @Valid @RequestBody IamResourceDTO IamResourceDTO) throws Exception {
        iamResourceService.updateMenuResources(IamResourceDTO);
        return JsonResult.OK();
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") String id) throws Exception {
        iamResourceService.deleteMenuResources(id);
        return JsonResult.OK();
    }

    /**
     * 排序
     *
     * @param sortParam
     * @return
     */
    @PatchMapping("/sort")
    @BindPermission(name = "列表排序", code = OperationCons.CODE_WRITE)
    public JsonResult<?> sort(@RequestBody @Valid SortParamDTO<String> sortParam) {
        return new JsonResult<>(getService().sort(sortParam, IamResource::getSortId, IamResource::getParentId, null));
    }

    /**
     * api接口列表（供前端选择）
     * @return
     * @throws Exception
     */
    @GetMapping("/api-list")
    public JsonResult apiList(boolean openApi) throws Exception {
        return JsonResult.OK(IamPermissionCacheManager.getApiPermissionVoList(openApi));
    }

    /**
     * 检查菜单编码是否重复
     * @param id
     * @param code
     * @return
     */
    @GetMapping("/check-code-duplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) String id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamResource> wrapper = Wrappers.<IamResource>lambdaQuery()
                    .select(IamResource::getId)
                    .eq(IamResource::getResourceCode, code)
                    .ne(IamResource::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name())
                    .ne(V.notEmpty(id),IamResource::getId, id);
            boolean exists = iamResourceService.exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION("编码已存在: " + code);
            }
        }
        return JsonResult.OK();
    }

}
