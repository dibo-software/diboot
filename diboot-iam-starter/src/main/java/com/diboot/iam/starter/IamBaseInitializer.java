/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.starter;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.JSON;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.entity.IamUserRole;
import com.diboot.iam.service.*;
import com.diboot.iam.vo.IamResourcePermissionListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * IAM组件相关的初始化
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Component
@Slf4j
public class IamBaseInitializer{

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private IamUserService iamUserService;
    @Autowired
    private IamUserRoleService iamUserRoleService;
    @Autowired
    private IamAccountService iamAccountService;
    @Autowired
    private IamResourcePermissionService iamResourcePermissionService;

    /**
     * 插入初始化数据
     */
    public void insertInitData(){
        // 插入iam-base所需的数据字典
        String[] DICT_INIT_DATA = {
            "{\"type\":\"AUTH_TYPE\", \"itemName\":\"登录认证方式\", \"description\":\"IAM用户登录认证方式\", \"children\":[{\"itemName\":\"用户名密码\", \"itemValue\":\"PWD\", \"sortId\":1},{\"itemName\":\"单点登录\", \"itemValue\":\"SSO\", \"sortId\":2},{\"itemName\":\"公众号\", \"itemValue\":\"WX_MP\", \"sortId\":3},{\"itemName\":\"企业微信\", \"itemValue\":\"WX_CP\", \"sortId\":4},{\"itemName\":\"其他\", \"itemValue\":\"OTHER\", \"sortId\":5}]}",
            "{\"type\":\"ACCOUNT_STATUS\", \"itemName\":\"账号状态\", \"description\":\"IAM登录账号状态\", \"children\":[{\"itemName\":\"有效\", \"itemValue\":\"A\", \"sortId\":1},{\"itemName\":\"无效\", \"itemValue\":\"I\", \"sortId\":2},{\"itemName\":\"锁定\", \"itemValue\":\"L\", \"sortId\":3}]}",
            "{\"type\":\"USER_STATUS\", \"itemName\":\"用户状态\", \"description\":\"IAM用户状态\", \"editable\":true, \"children\":[{\"itemName\":\"在职\", \"itemValue\":\"A\", \"sortId\":1},{\"itemName\":\"离职\", \"itemValue\":\"I\", \"sortId\":2}]}",
            "{\"itemName\":\"用户性别\",\"type\":\"GENDER\",\"description\":\"用户性别数据字典\",\"children\":[{\"itemValue\":\"F\",\"sortId\":99,\"itemName\":\"女\"},{\"itemValue\":\"M\",\"sortId\":99,\"itemName\":\"男\"}]}",
            "{\"type\":\"PERMISSION_TYPE\", \"itemName\":\"权限类型\", \"description\":\"IAM权限类型\", \"children\":[{\"itemName\":\"菜单\", \"itemValue\":\"MENU\", \"sortId\":1},{\"itemName\":\"操作\", \"itemValue\":\"OPERATION\", \"sortId\":2}]}",
            "{\"itemName\":\"前端按钮/权限编码\",\"type\":\"RESOURCE_PERMISSION_CODE\",\"description\":\"前端按钮/权限编码 常用选项\",\"children\":[{\"sortId\":1,\"itemName\":\"详情\",\"itemValue\":\"detail\"},{\"sortId\":2,\"itemName\":\"新建\",\"itemValue\":\"create\"},{\"sortId\":3,\"itemName\":\"更新\",\"itemValue\":\"update\"},{\"sortId\":4,\"itemName\":\"删除\",\"itemValue\":\"delete\"},{\"sortId\":5,\"itemName\":\"导出\",\"itemValue\":\"export\"},{\"sortId\":6,\"itemName\":\"导入\",\"itemValue\":\"import\"}]}"
        };
        // 插入iam-base所需的初始权限数据
        String[] RESOURCE_PERMISSION_DATA = {
            "{\"displayType\":\"MENU\",\"displayName\":\"系统管理\",\"frontendCode\":\"system\",\"children\":[{\"displayType\":\"MENU\",\"displayName\":\"数据字典管理\",\"frontendCode\":\"Dictionary\",\"apiSet\":\"GET:/dictionary/list\",\"sortId\":\"22\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"frontendCode\":\"detail\",\"apiSet\":\"GET:/dictionary/{id}\",\"sortId\":\"6\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"frontendCode\":\"create\",\"apiSet\":\"POST:/dictionary/\",\"sortId\":\"5\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"frontendCode\":\"update\",\"apiSet\":\"PUT:/dictionary/{id}\",\"sortId\":\"4\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"frontendCode\":\"delete\",\"apiSet\":\"DELETE:/dictionary/{id}\",\"sortId\":\"3\"}]},{\"displayType\":\"MENU\",\"displayName\":\"系统用户管理\",\"frontendCode\":\"IamUser\",\"apiSet\":\"GET:/iam/user/list\",\"sortId\":\"17\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"frontendCode\":\"detail\",\"apiSet\":\"GET:/iam/user/{id}\",\"sortId\":\"11\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"frontendCode\":\"create\",\"apiSet\":\"POST:/iam/user/\",\"sortId\":\"10\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"frontendCode\":\"update\",\"apiSet\":\"PUT:/iam/user/{id}\",\"sortId\":\"9\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"frontendCode\":\"delete\",\"apiSet\":\"DELETE:/iam/user/{id}\",\"sortId\":\"8\"}]},{\"displayType\":\"MENU\",\"displayName\":\"角色权限管理\",\"frontendCode\":\"IamRole\",\"apiSet\":\"GET:/iam/role/list\",\"sortId\":\"12\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"frontendCode\":\"detail\",\"apiSet\":\"GET:/iam/role/{id}\",\"sortId\":\"16\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"frontendCode\":\"create\",\"apiSet\":\"POST:/iam/role/\",\"sortId\":\"15\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"frontendCode\":\"update\",\"apiSet\":\"PUT:/iam/role/{id}\",\"sortId\":\"14\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"frontendCode\":\"delete\",\"apiSet\":\"DELETE:/iam/role/{id}\",\"sortId\":\"13\"}]},{\"displayType\":\"MENU\",\"displayName\":\"菜单权限管理\",\"resourceCode\":\"IamResourcePermission\",\"apiSet\":\"GET:/iam/resourcePermission/list\",\"sortId\":\"7\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"frontendCode\":\"detail\",\"apiSet\":\"GET:/iam/resourcePermission/{id}\",\"sortId\":\"23\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"frontendCode\":\"create\",\"apiSet\":\"POST:/resourcePermission/user/\",\"sortId\":\"21\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"frontendCode\":\"update\",\"apiSet\":\"PUT:/iam/resourcePermission/{id}\",\"sortId\":\"20\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"frontendCode\":\"delete\",\"apiSet\":\"DELETE:/iam/resourcePermission/{id}\",\"sortId\":\"19\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"排序\",\"frontendCode\":\"sort\",\"apiSet\":\"POST:/iam/resourcePermission/sortList\",\"sortId\":\"18\"}]},{\"displayType\":\"MENU\",\"displayName\":\"操作日志查看\",\"frontendCode\":\"IamOperationLog\",\"apiSet\":\"GET:/iam/operationLog/list\",\"sortId\":\"2\"},{\"displayType\":\"MENU\",\"displayName\":\"登录日志查看\",\"frontendCode\":\"IamLoginTrace\",\"apiSet\":\"GET:/iam/loginTrace/list\",\"sortId\":\"2\"}]}"
        };
        // 插入数据字典
        for(String dictJson : DICT_INIT_DATA){
            DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
            dictionaryService.createDictAndChildren(dictVo);
        }
        DICT_INIT_DATA = null;

        // 插入多层级菜单权限初始数据
        try {
            for (String resourcePermissionJson : RESOURCE_PERMISSION_DATA) {
                IamResourcePermissionListVO permissionListVO = JSON.toJavaObject(resourcePermissionJson, IamResourcePermissionListVO.class);
                iamResourcePermissionService.deepCreatePermissionAndChildren(permissionListVO);
            }
            RESOURCE_PERMISSION_DATA = null;
        } catch (BusinessException e){
            log.error("初始化菜单权限数据出错，请手动配置菜单初始的权限数据", e.getMessage());
        }

        // 插入超级管理员用户及角色
        IamRole iamRole = new IamRole();
        iamRole.setName("超级管理员").setCode("SUPER_ADMIN");
        iamRoleService.createEntity(iamRole);

        IamUser iamUser = new IamUser();
        iamUser.setOrgId(0L).setRealname("DIBOOT").setUserNum("0000").setGender("M").setMobilePhone("10000000000");
        iamUserService.createEntity(iamUser);

        // 插入对象
        IamUserRole iamUserRole = new IamUserRole(IamUser.class.getSimpleName(), iamUser.getId(), iamRole.getId());
        iamUserRoleService.getMapper().insert(iamUserRole);

        // 创建账号
        IamAccount iamAccount = new IamAccount();
        iamAccount.setUserType(IamUser.class.getSimpleName()).setUserId(iamUser.getId())
                .setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name())
                .setAuthAccount("admin").setAuthSecret("123456");
        iamAccountService.createEntity(iamAccount);
    }
}