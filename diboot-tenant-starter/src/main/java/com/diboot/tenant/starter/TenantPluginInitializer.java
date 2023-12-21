/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.starter;

import com.diboot.core.entity.Dictionary;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.SqlFileInitializer;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.service.IamResourceService;
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
 * diboot-tenant 配置文件类
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Slf4j
@Component
@Order(910)
@ConditionalOnProperty(prefix = "diboot", name = "init-sql", havingValue = "true")
public class TenantPluginInitializer implements ApplicationRunner {
    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化SCHEMA
        SqlFileInitializer.init(environment);
        // 检查数据库租户是否已存在
        String initDetectSql = "SELECT id FROM dbt_iam_tenant WHERE id='0'";
        if (SqlFileInitializer.checkSqlExecutable(initDetectSql) == false) {
            SqlFileInitializer.initBootstrapSql(this.getClass(), environment, "tenant");
            // 插入相关数据
            insertInitData();
            log.info("diboot-tenant 初始化SQL完成.");
        }
    }

    /**
     * 插入初始化数据
     */
    private void insertInitData() {
        // 插入tenant组件所需的数据字典
        DictionaryService dictionaryService = ContextHolder.getBean(DictionaryService.class);
        if(dictionaryService != null && !dictionaryService.exists(Dictionary::getType, "TENANT_STATUS")){
            // 插入tenant组件所需的数据字典
            final String[] DICT_INIT_DATA = {
                    "{\n" +
                            "  \"type\": \"TENANT_STATUS\",\n" +
                            "  \"itemName\": \"租户状态\",\n" +
                            "  \"description\": \"租户状态定义\",\n" +
                            "  \"isEditable\": false,\n" +
                            "  \"children\": [\n" +
                            "    {\n" +
                            "      \"itemName\": \"有效\",\n" +
                            "      \"itemValue\": \"A\",\n" +
                            "      \"sortId\": 1\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"itemName\": \"无效\",\n" +
                            "      \"itemValue\": \"I\",\n" +
                            "      \"sortId\": 2\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}"
            };
            // 插入数据字典
            for (String dictJson : DICT_INIT_DATA) {
                DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
                dictionaryService.createDictAndChildren(dictVo);
            }
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
    }
}
