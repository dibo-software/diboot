package com.example.api.excel.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUserRole;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamUserService;

import java.util.*;
import java.util.stream.Collectors;

import com.example.api.excel.model.UserImportModel;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户导入Excel listener
 */
public class UserImportExcelListener extends FixedHeadExcelListener<UserImportModel> {
    private static final String USER_NUM_FIELD_NAME = BeanUtils.convertToFieldName(UserImportModel::getUserNum);
    private static final String PASSWORD_FIELD_NAME = BeanUtils.convertToFieldName(UserImportModel::getPassword);
    private static final String USERNAME_FIELD_NAME = BeanUtils.convertToFieldName(UserImportModel::getUsername);

    /**
     * 附加校验
     *
     * @param dataList
     * @param requestParams
     */
    @Override
    protected void additionalValidate(List<UserImportModel> dataList, Map<String, Object> requestParams) {
        // 对 用户编号、用户名 是否重复进行校验
        List<String> userNumList = new ArrayList<>();
        List<String> usernameList = new ArrayList<>();
        for (UserImportModel userExcelModel : dataList) {
            if (userNumList.contains(userExcelModel.getUserNum())) {
                userExcelModel.addComment(USER_NUM_FIELD_NAME, "编号重复");
            }
            userNumList.add(userExcelModel.getUserNum());

            if (usernameList.contains(userExcelModel.getUsername())) {
                userExcelModel.addComment(USERNAME_FIELD_NAME, "用户名重复");
            }
            if (V.notEmpty(userExcelModel.getUsername()) && V.isEmpty(userExcelModel.getPassword())) {
                userExcelModel.addComment(PASSWORD_FIELD_NAME, "存在用户名时密码不能为空");
            }
            usernameList.add(userExcelModel.getUsername());
        }
        // 对用户编号在系统中是否重复进行校验
        List<String> duplicateUserNumList = ContextHolder.getBean(IamUserService.class).filterDuplicateUserNums(userNumList);
        for (String userNum : duplicateUserNumList) {
            dataList.get(userNumList.indexOf(userNum)).addComment(USER_NUM_FIELD_NAME, "该编号在系统中已存在");
        }

        // 对用户名在系统中是否重复进行校验
        List<String> collect = usernameList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return;
        }
        IamAccountService accountService = ContextHolder.getBean(IamAccountService.class);
        int totalSize = collect.size(), batchSize = BaseConfig.getBatchSize();
        int startInx = 0;
        while (startInx < totalSize) {
            LambdaQueryWrapper<IamAccount> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.in(IamAccount::getAuthAccount, collect.subList(startInx, Math.min(startInx + batchSize, totalSize)));
            List<String> existUsernameList = accountService.getValuesOfField(queryWrapper, IamAccount::getAuthAccount);
            for (String username : existUsernameList) {
                dataList.get(usernameList.indexOf(username)).addComment(USERNAME_FIELD_NAME, "该用户名在系统中已存在");
            }
            startInx += batchSize;
        }
    }

    /**
     * 保存数据
     *
     * @param dataList
     * @param requestParams
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveData(List<UserImportModel> dataList, Map<String, Object> requestParams) {
        if (V.isEmpty(dataList)) {
            return;
        }
        String orgId = S.defaultIfEmpty(S.valueOf(requestParams.get("orgId")), "0");
        List<IamUser> iamUserList = new ArrayList<>();
        Map<IamUser, IamAccount> accountMap = new HashMap<>();
        Map<IamUser, IamUserRole> userRoleMap = new HashMap<>();
        for (UserImportModel data : dataList) {
            IamUser iamUser = new IamUser();
            BeanUtils.copyProperties(data, iamUser);
            iamUser.setOrgId(orgId);
            iamUserList.add(iamUser);
            if (V.notEmpty(data.getUsername())) {
                IamAccount account = new IamAccount();
                account.setAuthAccount(data.getUsername().trim());
                account.setAuthSecret(data.getPassword());
                accountMap.put(iamUser, account);
            }
            if (data.getRoleId() != null) {
                userRoleMap.put(iamUser, new IamUserRole().setRoleId(data.getRoleId()));
            }
        }
        ContextHolder.getBean(IamUserService.class).createEntities(iamUserList);

        // 创建账户
        if (!accountMap.isEmpty()) {
            List<IamAccount> accountList = accountMap.entrySet().stream().map(map -> map.getValue().setUserId(map.getKey().getId())).collect(Collectors.toList());
            ContextHolder.getBean(IamAccountService.class).createEntities(accountList);
        }

        // 创建用户角色关联关系
        if (!userRoleMap.isEmpty()) {
            List<IamUserRole> userRoleList = userRoleMap.entrySet().stream().map(map -> map.getValue().setUserId(map.getKey().getId())).collect(Collectors.toList());
            ContextHolder.getBean(IamUserRoleService.class).createEntities(userRoleList);
        }
    }
}