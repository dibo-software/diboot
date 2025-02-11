package com.example.api.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamRoleFormDTO;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.entity.IamUserRole;
import com.diboot.iam.service.IamRoleResourceService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.vo.IamResourceListVO;
import com.diboot.iam.vo.IamRoleVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 角色相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/role")
@Slf4j
@BindPermission(name = "角色")
public class RoleController extends BaseCrudRestController<IamRole> {
    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private IamUserRoleService iamUserRoleService;

    @Autowired
    private IamRoleResourceService iamRoleResourceService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getViewObjectListMapping(IamRole entity, Pagination pagination) throws Exception {
        return super.getViewObjectList(entity, pagination, IamRoleVO.class);
    }

    /**
     * 根据 Ids 获取列表
     *
     * @param ids
     * @return
     */
    @PostMapping("/ids")
    public JsonResult getObjectListByIds(@RequestBody List<String> ids) {
        return JsonResult.OK(iamRoleService.getEntityListByIds(ids));
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
    public JsonResult getViewObjectMapping(@PathVariable("id") String id) throws Exception {
        IamRoleVO roleVO = iamRoleService.getViewObject(id, IamRoleVO.class);
        if (V.notEmpty(roleVO.getPermissionList())) {
            List<IamResourceListVO> resourceVoList = Binder.convertAndBindRelations(roleVO.getPermissionList(), IamResourceListVO.class);
            roleVO.setPermissionVOList(BeanUtils.buildTree(resourceVoList, Cons.TREE_ROOT_ID));
        }
        return JsonResult.OK(roleVO);
    }

    /**
     * 新建角色和角色权限关联列表
     *
     * @param roleFormDTO
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult createEntityMapping(@Valid @RequestBody IamRoleFormDTO roleFormDTO) throws Exception {
        return super.createEntity(roleFormDTO);
    }

    /**
     * 更新角色和角色权限关联列表
     *
     * @param roleFormDTO
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") String id, @Valid @RequestBody IamRoleFormDTO roleFormDTO) throws Exception {
        return super.updateEntity(id, roleFormDTO);
    }

    /**
     * 更新角色用户
     *
     * @param id 角色ID
     * @param userIds 对应用户id列表
     * @return
     */
    @Log(operation = "更新角色用户")
    @BindPermission(name = "更新角色用户", code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}/user")
    public JsonResult<?> updateRoleUserMapping(@PathVariable("id") String id, @RequestBody List<String> userIds) {
        boolean success = iamUserRoleService.createOrUpdateN2NRelations(IamUserRole::getRoleId,id, IamUserRole::getUserId, userIds,
                query -> query.lambda().eq(IamUserRole::getUserType, IamUser.class.getSimpleName()),
                e -> e.setUserType(IamUser.class.getSimpleName()));
        return new JsonResult<>(success);
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
    public JsonResult deleteEntityMapping(@PathVariable("id") String id) throws Exception {
        boolean hasUser = iamUserRoleService.exists(IamUserRole::getRoleId, id);
        if (hasUser) {
            return JsonResult.FAIL_OPERATION("该角色下存在有效用户，解除关系后方可删除");
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
            LambdaQueryWrapper<IamRole> wrapper = Wrappers.<IamRole>lambdaQuery()
                    .select(IamRole::getId).eq(IamRole::getCode, code);
            if (V.notEmpty(id)) {
                wrapper.ne(IamRole::getId, id);
            }
            boolean exists = iamRoleService.exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION("编码已存在: " + code);
            }
        }
        return JsonResult.OK();
    }

    @Override
    protected void beforeUpdate(IamRole entity) throws Exception {
        super.beforeUpdate(entity);
        if (Cons.ROLE_SUPER_ADMIN.equals(entity.getCode())) {
            throw new BusinessException(Status.FAIL_OPERATION, "不能更新超级管理员角色");
        }
    }

    @Override
    protected void beforeDelete(Serializable id) throws Exception {
        if (Cons.ROLE_SUPER_ADMIN.equals(getService().getEntity(id).getCode())) {
            throw new BusinessException(Status.FAIL_OPERATION, "不能删除超级管理员角色");
        }
    }

    @Override
    protected void afterCreated(IamRole entity) throws Exception {
        IamRoleFormDTO roleFormDTO = (IamRoleFormDTO) entity;
        iamRoleResourceService.createRoleResourceRelations(roleFormDTO.getId(), roleFormDTO.getPermissionIdList());
    }

    @Override
    protected void afterUpdated(IamRole entity) throws Exception {
        IamRoleFormDTO roleFormDTO = (IamRoleFormDTO) entity;
        iamRoleResourceService.updateRoleResourceRelations(roleFormDTO.getId(), roleFormDTO.getPermissionIdList());
    }

    @Override
    protected void afterDeleted(Serializable roleId) throws Exception {
        iamRoleResourceService.deleteRoleResourceRelations((String)roleId);
    }

}
