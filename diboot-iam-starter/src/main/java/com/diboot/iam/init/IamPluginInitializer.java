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
package com.diboot.iam.init;

import com.diboot.core.entity.Dictionary;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.SqlFileInitializer;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.*;
import com.diboot.iam.service.*;
import com.diboot.iam.vo.IamResourceListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * IAM组件相关的初始化
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Order(912)
@Slf4j
@Component
@ConditionalOnProperty(prefix = "diboot", name = "init-sql", havingValue = "true")
public class IamPluginInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库表是否已存在
        String initDetectSql = "SELECT id FROM dbt_iam_role WHERE id='0'";
        if(!SqlFileInitializer.checkSqlExecutable(initDetectSql)){
            log.info("diboot-IAM 初始化SQL ...");
            // 执行初始化SQL
            SqlFileInitializer.initBootstrapSql(this.getClass(), "iam");
            // 插入相关数据：Dict，Role等
            insertInitData();
            log.info("diboot-IAM 初始化SQL完成.");
        }
    }

    /**
     * 插入初始化数据
     */
    private synchronized void insertInitData(){
        // 插入iam组件所需的数据字典
        DictionaryService dictionaryService = ContextHolder.getBean(DictionaryService.class);
        if(dictionaryService != null && !dictionaryService.exists(Dictionary::getType, "AUTH_TYPE")){
            String[] DICT_INIT_DATA = {
                    "{\"type\":\"AUTH_TYPE\", \"itemName\":\"登录认证方式\", \"description\":\"IAM用户登录认证方式\", \"children\":[{\"itemName\":\"用户名密码\", \"itemValue\":\"PWD\", \"sortId\":1},{\"itemName\":\"单点登录\", \"itemValue\":\"SSO\", \"sortId\":2},{\"itemName\":\"公众号\", \"itemValue\":\"WX_MP\", \"sortId\":3},{\"itemName\":\"企业微信\", \"itemValue\":\"WX_CP\", \"sortId\":4},{\"itemName\":\"其他\", \"itemValue\":\"OTHER\", \"sortId\":5}]}",
                    "{\"type\":\"ACCOUNT_STATUS\",\"itemName\":\"账号状态\",\"description\":\"IAM登录账号状态\",\"children\":[{\"itemName\":\"有效\",\"itemValue\":\"A\",\"extension\":{\"color\":\"#2ECC71\"},\"sortId\":1},{\"itemName\":\"无效\",\"itemValue\":\"I\",\"sortId\":2},{\"itemName\":\"锁定\",\"itemValue\":\"L\",\"extension\":{\"color\":\"#FF6F00\"},\"sortId\":3}]}",
                    "{\"type\":\"USER_STATUS\",\"itemName\":\"用户状态\",\"description\":\"IAM用户状态\",\"isEditable\":true,\"children\":[{\"itemName\":\"在职\",\"itemValue\":\"A\",\"extension\":{\"color\":\"#2ECC71\"},\"sortId\":1},{\"itemName\":\"离职\",\"itemValue\":\"I\",\"sortId\":2}]}",
                    "{\"itemName\":\"用户性别\",\"type\":\"GENDER\",\"description\":\"用户性别数据字典\",\"children\":[{\"itemValue\":\"F\",\"itemName\":\"女\",\"extension\":{\"color\":\"#FD8BB8\"}},{\"itemValue\":\"M\",\"itemName\":\"男\",\"extension\":{\"color\":\"#55B0EE\"}}]}",
                    "{\"type\":\"RESOURCE_TYPE\", \"itemName\":\"资源类型\", \"description\":\"IAM资源类型\", \"children\":[{\"itemName\":\"菜单\", \"itemValue\":\"MENU\", \"sortId\":1},{\"itemName\":\"按钮/操作\", \"itemValue\":\"OPERATION\", \"sortId\":2}]}",
                    "{\"itemName\":\"前端按钮/权限编码\",\"type\":\"RESOURCE_CODE\",\"description\":\"前端按钮/权限编码 常用选项\",\"children\":[{\"sortId\":1,\"itemName\":\"详情\",\"itemValue\":\"detail\"},{\"sortId\":2,\"itemName\":\"新建\",\"itemValue\":\"create\"},{\"sortId\":3,\"itemName\":\"更新\",\"itemValue\":\"update\"},{\"sortId\":4,\"itemName\":\"删除\",\"itemValue\":\"delete\"},{\"sortId\":5,\"itemName\":\"导出\",\"itemValue\":\"export\"},{\"sortId\":6,\"itemName\":\"导入\",\"itemValue\":\"import\"}]}",
                    "{\"type\":\"ORG_TYPE\", \"itemName\":\"组织类型\", \"description\":\"组织节点类型\", \"isEditable\":false, \"children\":[{\"itemName\":\"部门\", \"itemValue\":\"DEPT\", \"sortId\":1},{\"itemName\":\"公司\", \"itemValue\":\"COMP\", \"sortId\":2}]}",
                    "{\"type\":\"DATA_PERMISSION_TYPE\", \"itemName\":\"IAM数据权限类型\", \"description\":\"IAM数据权限类型定义\", \"isEditable\":true, \"children\":[{\"itemName\":\"本人\", \"itemValue\":\"SELF\", \"sortId\":1},{\"itemName\":\"本人及下属\", \"itemValue\":\"SELF_AND_SUB\", \"sortId\":2},{\"itemName\":\"本部门\", \"itemValue\":\"DEPT\", \"sortId\":3},{\"itemName\":\"本部门及下属部门\", \"itemValue\":\"DEPT_AND_SUB\", \"sortId\":4},{\"itemName\":\"全部\", \"itemValue\":\"ALL\", \"sortId\":5}]}",
                    "{\"type\":\"POSITION_GRADE\", \"itemName\":\"职级定义\", \"description\":\"职务级别定义\", \"isEditable\":true, \"children\":[{\"itemName\":\"初级\", \"itemValue\":\"E1\", \"sortId\":1},{\"itemName\":\"中级\", \"itemValue\":\"E2\", \"sortId\":2},{\"itemName\":\"高级\", \"itemValue\":\"E3\", \"sortId\":3},{\"itemName\":\"专家\", \"itemValue\":\"E4\", \"sortId\":4}]}"
            };
            // 插入数据字典
            for(String dictJson : DICT_INIT_DATA){
                DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
                dictVo.setParentId(Cons.ID_PREVENT_NULL);
                dictionaryService.createDictAndChildren(dictVo);
            }
            DICT_INIT_DATA = null;
        }

        // 插入iam组件所需的初始权限数据
        IamResourceService resourcePermissionService = ContextHolder.getBean(IamResourceService.class);
        if(resourcePermissionService != null && !resourcePermissionService.exists(IamResource::getResourceCode, "system")){
            String[] RESOURCE_PERMISSION_DATA = {
                    "{\"displayType\":\"CATALOGUE\",\"displayName\":\"组织人员\",\"routePath\":\"org-structure\",\"resourceCode\":\"OrgStructure\",\"meta\":\"{\\\"icon\\\":\\\"Element:UserFilled\\\"}\",\"sortId\":\"90\",\"children\":[{\"displayType\":\"MENU\",\"displayName\":\"人员管理\",\"routePath\":\"user\",\"resourceCode\":\"User\",\"permissionCode\":\"IamOrg:read,IamUser:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:User\\\",\\\"componentPath\\\":\\\"@/views/org-structure/user/index.vue\\\"}\",\"sortId\":\"1\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"IamUser:write\",\"sortId\":\"40\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"IamUser:write,IamUser:read\",\"sortId\":\"39\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"IamUser:write\",\"sortId\":\"38\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"IamUser:read\",\"sortId\":\"37\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"导入\",\"resourceCode\":\"import\",\"permissionCode\":\"IamUserExcel:import\",\"sortId\":\"36\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"导出\",\"resourceCode\":\"export\",\"permissionCode\":\"IamUserExcel:export\",\"sortId\":\"35\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"人员岗位设置\",\"resourceCode\":\"position\",\"permissionCode\":\"IamPosition:write,IamPosition:read\",\"sortId\":\"34\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"添加岗位\",\"resourceCode\":\"addPosition\",\"permissionCode\":\"IamPosition:write,IamPosition:read\",\"sortId\":\"33\"}]},{\"displayType\":\"MENU\",\"displayName\":\"组织部门\",\"routePath\":\"org\",\"resourceCode\":\"Org\",\"permissionCode\":\"IamOrg:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:Folder\\\",\\\"componentPath\\\":\\\"@/views/org-structure/org/index.vue\\\"}\",\"sortId\":\"2\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"排序\",\"resourceCode\":\"sort\",\"permissionCode\":\"IamOrg:write\",\"sortId\":\"106\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"IamOrg:write\",\"sortId\":\"105\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"IamOrg:write,IamOrg:read\",\"sortId\":\"104\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"IamOrg:write\",\"sortId\":\"103\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"IamOrg:read\",\"sortId\":\"102\"}]},{\"displayType\":\"MENU\",\"displayName\":\"岗位管理\",\"routePath\":\"position\",\"resourceCode\":\"Position\",\"permissionCode\":\"IamPosition:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:Postcard\\\",\\\"componentPath\\\":\\\"@/views/org-structure/position/List.vue\\\"}\",\"sortId\":\"3\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"IamPosition:write\",\"sortId\":\"112\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"IamPosition:read\",\"sortId\":\"111\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"IamPosition:write,IamPosition:read\",\"sortId\":\"110\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"IamPosition:write\",\"sortId\":\"108\"}]}]}",
                    "{\"displayType\":\"CATALOGUE\",\"displayName\":\"系统管理\",\"routePath\":\"system\",\"resourceCode\":\"System\",\"meta\":\"{\\\"icon\\\":\\\"Element:Tools\\\"}\",\"sortId\":\"95\",\"children\":[{\"displayType\":\"MENU\",\"displayName\":\"数据字典\",\"routePath\":\"dictionary\",\"resourceCode\":\"Dictionary\",\"permissionCode\":\"Dictionary:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:Collection\\\",\\\"componentPath\\\":\\\"@/views/system/dictionary/List.vue\\\"}\",\"sortId\":\"10\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"Dictionary:read\",\"sortId\":\"6\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"Dictionary:write\",\"sortId\":\"5\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"Dictionary:write,Dictionary:read\",\"sortId\":\"4\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"Dictionary:write\",\"sortId\":\"3\"}]},{\"displayType\":\"MENU\",\"displayName\":\"菜单资源\",\"routePath\":\"resource\",\"resourceCode\":\"Resource\",\"permissionCode\":\"IamResource:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:Menu\\\",\\\"componentPath\\\":\\\"@/views/system/resource/index.vue\\\"}\",\"sortId\":\"20\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"IamResource:read\",\"sortId\":\"23\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"IamResource:write\",\"sortId\":\"21\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"IamResource:write,IamResource:read\",\"sortId\":\"20\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"IamResource:write\",\"sortId\":\"19\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"排序\",\"resourceCode\":\"sort\",\"permissionCode\":\"IamResource:write\",\"sortId\":\"18\"}]},{\"displayType\":\"MENU\",\"displayName\":\"角色权限\",\"routePath\":\"role\",\"resourceCode\":\"Role\",\"permissionCode\":\"IamRole:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:Avatar\\\",\\\"componentPath\\\":\\\"@/views/system/role/List.vue\\\"}\",\"sortId\":\"30\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"IamRole:read\",\"sortId\":\"16\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"IamRole:write\",\"sortId\":\"15\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"IamRole:write,IamRole:read,IamResource:read\",\"sortId\":\"14\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"IamRole:write\",\"sortId\":\"13\"}]},{\"displayType\":\"MENU\",\"displayName\":\"定时任务\",\"routePath\":\"schedule-job\",\"resourceCode\":\"ScheduleJob\",\"permissionCode\":\"ScheduleJob:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:AlarmClock\\\",\\\"componentPath\\\":\\\"@/views/system/schedule-job/List.vue\\\"}\",\"sortId\":\"40\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"ScheduleJob:write\",\"sortId\":\"7\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"ScheduleJob:write,ScheduleJob:read\",\"sortId\":\"6\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"ScheduleJob:write\",\"sortId\":\"5\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"ScheduleJob:read\",\"sortId\":\"4\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"运行一次\",\"resourceCode\":\"executeOnce\",\"permissionCode\":\"ScheduleJob:write\",\"sortId\":\"3\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"日志记录\",\"resourceCode\":\"logList\",\"permissionCode\":\"ScheduleJob:read\",\"sortId\":\"2\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"日志删除\",\"resourceCode\":\"logDelete\",\"permissionCode\":\"ScheduleJob:write\",\"sortId\":\"1\"}]},{\"displayType\":\"MENU\",\"displayName\":\"消息模板\",\"routePath\":\"message-template\",\"resourceCode\":\"MessageTemplate\",\"permissionCode\":\"MessageTemplate:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:ChatLineSquare\\\",\\\"componentPath\\\":\\\"@/views/system/message-template/List.vue\\\"}\",\"sortId\":\"50\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"MessageTemplate:read\",\"sortId\":\"16\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"MessageTemplate:write\",\"sortId\":\"15\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"MessageTemplate:write,MessageTemplate:read\",\"sortId\":\"14\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"MessageTemplate:write\",\"sortId\":\"13\"}]},{\"displayType\":\"MENU\",\"displayName\":\"消息记录\",\"routePath\":\"message\",\"resourceCode\":\"Message\",\"permissionCode\":\"Message:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:ChatDotRound\\\",\\\"componentPath\\\":\\\"@/views/system/message/List.vue\\\"}\",\"sortId\":\"60\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"Message:read\",\"sortId\":\"16\"}]},{\"displayType\":\"MENU\",\"displayName\":\"文件记录\",\"routePath\":\"file-record\",\"resourceCode\":\"FileRecord\",\"permissionCode\":\"FileRecord:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:FolderOpened\\\",\\\"componentPath\\\":\\\"@/views/system/file-record/List.vue\\\"}\",\"sortId\":\"70\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"FileRecord:read\",\"sortId\":\"16\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"FileRecord:write,FileRecord:read\",\"sortId\":\"14\"}]},{\"displayType\":\"MENU\",\"displayName\":\"系统配置\",\"routePath\":\"config\",\"resourceCode\":\"SystemConfig\",\"permissionCode\":\"SystemConfig:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:Setting\\\",\\\"componentPath\\\":\\\"@/views/system/config/index.vue\\\"}\",\"sortId\":\"80\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"permissionCode\":\"SystemConfig:write\",\"sortId\":\"1\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"permissionCode\":\"SystemConfig:write\",\"sortId\":\"2\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"permissionCode\":\"SystemConfig:write\",\"sortId\":\"3\"}]},{\"displayType\":\"MENU\",\"displayName\":\"操作日志\",\"routePath\":\"operation-log\",\"resourceCode\":\"OperationLog\",\"permissionCode\":\"IamOperationLog:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:Pointer\\\",\\\"componentPath\\\":\\\"@/views/system/operation-log/List.vue\\\"}\",\"sortId\":\"90\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"permissionCode\":\"IamOperationLog:read\",\"sortId\":\"1\"}]},{\"displayType\":\"MENU\",\"displayName\":\"登录日志\",\"routePath\":\"login-trace\",\"resourceCode\":\"LoginTrace\",\"permissionCode\":\"IamLoginTrace:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:Finished\\\",\\\"componentPath\\\":\\\"@/views/system/login-trace/List.vue\\\"}\",\"sortId\":\"100\",\"children\":[]}]}"
            };
            // 插入多层级资源权限初始数据
            try {
                for (String resourcePermissionJson : RESOURCE_PERMISSION_DATA) {
                    IamResourceListVO permissionListVO = JSON.toJavaObject(resourcePermissionJson, IamResourceListVO.class);
                    resourcePermissionService.deepCreateResourceAndChildren(permissionListVO);
                }
                RESOURCE_PERMISSION_DATA = null;
            } catch (BusinessException e){
                log.error("初始化资源权限数据出错，请手动配置前端资源初始的权限数据", e.getMessage());
            }
        }

        // 插入公司根节点
        IamOrgService iamOrgService = ContextHolder.getBean(IamOrgService.class);
        IamOrg iamOrg = new IamOrg();
        if(iamOrgService != null && iamOrgService.getEntityListCount(null) == 0){
            iamOrg.setCode("ROOT").setRootOrgId("1").setName("我的公司")
                    .setType(Cons.DICTCODE_ORG_TYPE.COMP.name()).setOrgComment("初始根节点，请按需修改").setId("1");
            iamOrgService.createEntity(iamOrg);
        }

        // 插入超级管理员用户及角色
        IamRoleService iamRoleService = ContextHolder.getBean(IamRoleService.class);
        if(iamRoleService != null && iamRoleService.getEntityListCount(null) == 0){
            IamRole iamRole = new IamRole();
            iamRole.setName("超级管理员").setCode(Cons.ROLE_SUPER_ADMIN);
            iamRoleService.createEntity(iamRole);

            IamUser iamUser = new IamUser();
            iamUser.setOrgId(iamOrg.getId()).setRealname("DIBOOT").setUserNum("0000").setGender("M").setMobilePhone("10000000000");
            ContextHolder.getBean(IamUserService.class).createEntity(iamUser);

            // 插入对象
            IamUserRole iamUserRole = new IamUserRole(IamUser.class.getSimpleName(), iamUser.getId(), iamRole.getId());
            ContextHolder.getBean(IamUserRoleService.class).getMapper().insert(iamUserRole);

            // 创建账号
            IamAccount iamAccount = new IamAccount();
            iamAccount.setUserType(IamUser.class.getSimpleName()).setUserId(iamUser.getId())
                    .setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name())
                    .setAuthAccount("admin").setAuthSecret("123456");
            ContextHolder.getBean(IamAccountService.class).createEntity(iamAccount);
        }

    }
}
