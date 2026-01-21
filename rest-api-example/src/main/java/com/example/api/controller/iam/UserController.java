package com.example.api.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.ChangePwdDTO;
import com.diboot.iam.dto.IamUserFormDTO;
import com.diboot.iam.dto.IamUserSearchDTO;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamUserOrgVO;
import com.diboot.iam.vo.IamUserVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * 系统用户相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/user")
@Slf4j
@BindPermission(name = "用户")
public class UserController extends BaseCrudRestController<IamUser> {

    @Autowired
    private IamUserService iamUserService;

    @Autowired
    private IamAccountService iamAccountService;

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
    public JsonResult getViewObjectListMapping(IamUserSearchDTO dto, String _conditions, Pagination pagination) throws Exception {
        String orgId = dto.getOrgId();
        dto.setOrgId(null);
        // 处理 _conditions
        if (V.notEmpty(_conditions)) {
            List<Map<String, Object>> conditions = JSON.parseArray(_conditions, new TypeReference<List<Map<String, Object>>>(){});
            for (Map<String, Object> condition : conditions) {
                String field = String.valueOf(condition.get("field"));
                Object value = condition.get("value");
                BeanUtils.setProperty(dto, field, value);
            }
        }
        QueryWrapper<IamUser> queryWrapper = super.buildQueryWrapperByDTO(dto);
        if (pagination != null && V.isEmpty(pagination.getOrderBy())) {
            pagination.setOrderBy("sortId:ASC");
        }
        List<IamUserVO> voList = iamUserService.getUserViewList(queryWrapper, pagination, orgId);
        // 返回结果
        return JsonResult.OK(voList).bindPagination(pagination);
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
        return super.getViewObject(id, IamUserOrgVO.class);
    }

    /**
     * 新建用户、账号和用户角色关联列表、用户岗位
     *
     * @param iamUserFormDTO
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult createEntityMapping(@Valid @RequestBody IamUserFormDTO iamUserFormDTO) throws Exception {
        iamUserService.createUserRelatedInfo(iamUserFormDTO);
        return JsonResult.OK();
    }

    /**
     * 更新用户、账号和用户角色关联列表、用户岗位
     *
     * @param iamUserFormDTO
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") String id, @Valid @RequestBody IamUserFormDTO iamUserFormDTO) throws Exception {
        iamUserFormDTO.setUpdateTime(null);
        iamUserService.updateUserRelatedInfo(iamUserFormDTO);
        return JsonResult.OK();
    }

    /**
     * 删除用户、账号和用户角色关联列表
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") String id) throws Exception {
        iamUserService.deleteUserAndRelatedInfo(id);
        return JsonResult.OK();
    }

    /**
     * 获取用户名
     *
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/account/{id}")
    public JsonResult getUsername(@PathVariable("id") String id) throws Exception {
        IamAccount account = iamAccountService.getSingleEntity(
                Wrappers.<IamAccount>lambdaQuery()
                        .select(IamAccount::getAuthAccount, IamAccount::getStatus)
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getUserId, id)
        );
        return JsonResult.OK(account);
    }

    /**
     * 获取指定orgId下的用户列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/user-list/{orgId}")
    public JsonResult getUserList(@PathVariable("orgId") String orgId, IamUser iamUser, Pagination pagination) throws Exception {
        QueryWrapper<IamUser> wrapper = super.buildQueryWrapperByDTO(iamUser);
        if (orgId != null && !V.equals(orgId, Cons.TREE_ROOT_ID)) {
            wrapper.lambda().eq(IamUser::getOrgId, orgId);
        }
        List entityList = iamUserService.getEntityList(wrapper, pagination);
        return JsonResult.OK(entityList).bindPagination(pagination);
    }

    /**
     * 获取当前用户信息
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/current-user-info")
    public JsonResult getCurrentUserInfo() throws Exception {
        IamUser iamUser = IamSecurityUtils.getCurrentUser();
        IamUser newIamUser = iamUserService.getEntity(iamUser.getId());
        return JsonResult.OK(newIamUser);
    }

    /**
     * 更新当前用户信息
     *
     * @param userInfo
     * @return
     * @throws Exception
     */
    @Log(operation = "更新个人信息")
    @PostMapping("/update-current-user-info")
    public JsonResult updateCurrentUserInfo(@Valid @RequestBody IamUser userInfo) throws Exception {
        IamUser newIamUser = iamUserService.getEntity(IamSecurityUtils.getCurrentUserId());
        newIamUser.setRealname(userInfo.getRealname())
                .setGender(userInfo.getGender())
                .setMobilePhone(userInfo.getMobilePhone())
                .setEmail(userInfo.getEmail())
                .setAvatarUrl(userInfo.getAvatarUrl());
        boolean success = iamUserService.updateEntity(newIamUser);
        if (!success) {
            return JsonResult.FAIL_OPERATION("更新个人信息失败");
        }
        return JsonResult.OK(newIamUser).msg("更新成功");
    }

    /**
     * 更改密码
     *
     * @param changePwdDTO
     * @return
     * @throws Exception
     */
    @Log(operation = "更改密码")
    @PostMapping("/change-pwd")
    public JsonResult changePwd(@Valid @RequestBody ChangePwdDTO changePwdDTO) throws Exception {
        IamUser iamUser = IamSecurityUtils.getCurrentUser();
        IamAccount iamAccount = iamAccountService.getSingleEntity(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getUserId, iamUser.getId())
        );
        boolean success = iamAccountService.changePwd(changePwdDTO, iamAccount);
        if (!success) {
            return JsonResult.FAIL_OPERATION("更改密码失败");
        }
        return JsonResult.OK().msg("更改密码成功");
    }

    /**
     * 校验用户名是否重复
     *
     * @param id
     * @param username
     * @return
     */
    @GetMapping("/check-username-duplicate")
    public JsonResult checkUsernameDuplicate(@RequestParam(required = false) String id, @RequestParam String username) {
        if (V.isEmpty(username)) {
            return JsonResult.FAIL_VALIDATION("用户名不能为空");
        }
        IamAccount account = new IamAccount().setAuthAccount(username);
        account.setUserType(IamUser.class.getSimpleName());
        account.setUserId(id);
        boolean alreadyExists = iamAccountService.isAccountExists(account);
        if (alreadyExists) {
            return JsonResult.FAIL_VALIDATION("用户名已存在");
        }
        return JsonResult.OK();
    }

    /**
     * 校验用户编号是否重复
     *
     * @param id
     * @param userNum
     * @return
     */
    @GetMapping("/check-user-num-duplicate")
    public JsonResult checkUserNumDuplicate(@RequestParam(required = false) String id, @RequestParam String userNum) {
        if (V.isEmpty(userNum)) {
            return JsonResult.FAIL_VALIDATION("用户编号不能为空");
        }
        boolean alreadyExists = iamUserService.isUserNumExists(id, userNum);
        if (alreadyExists) {
            return JsonResult.FAIL_VALIDATION("用户编号已存在");
        }
        return JsonResult.OK();
    }
}
