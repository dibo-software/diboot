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
package com.diboot.tenant.init;

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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * diboot-tenant 配置文件类
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Order(971)
@Slf4j
@Component
@ConditionalOnProperty(prefix = "diboot", name = "init-sql", havingValue = "true")
public class TenantPluginInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库租户是否已存在
        String initDetectSql = "SELECT id FROM dbt_iam_tenant WHERE id='0'";
        if (!SqlFileInitializer.checkSqlExecutable(initDetectSql)) {
            SqlFileInitializer.initBootstrapSql(this.getClass(), "tenant");
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
                            "       \"itemNameI18n\": \"Dictionary.TENANT_STATUS.A\",\n" +
                            "      \"itemValue\": \"A\",\n" +
                            "      \"sortId\": 1\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"itemName\": \"无效\",\n" +
                            "       \"itemNameI18n\": \"Dictionary.TENANT_STATUS.I\",\n" +
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

        // 插入tenant组件所需的初始权限数据
        IamResourceService resourcePermissionService = ContextHolder.getBean(IamResourceService.class);
        if(resourcePermissionService != null && !resourcePermissionService.exists(IamResource::getResourceCode, "TenantMgt")){
            String[] RESOURCE_PERMISSION_DATA = {
                    "{\"displayType\":\"CATALOGUE\",\"displayName\":\"租户管理\",\"displayNameI18n\":\"Resource.Tenant\",\"routePath\":\"tenant\",\"resourceCode\":\"TenantMgt\",\"meta\":\"{\\\"icon\\\":\\\"Element:Platform\\\"}\",\"sortId\":\"91\",\"children\":[{\"displayType\":\"MENU\",\"displayName\":\"租户信息管理\",\"displayNameI18n\":\"Resource.TenantInformation\",\"routePath\":\"tenant\",\"resourceCode\":\"IamTenant\",\"permissionCode\":\"IamTenant:read\",\"meta\":\"{\\\"icon\\\":\\\"Element:User\\\",\\\"componentPath\\\":\\\"@/views/tenant/tenant-info/List.vue\\\"}\",\"sortId\":\"1\",\"children\":[{\"displayType\":\"PERMISSION\",\"displayName\":\"新建\",\"resourceCode\":\"create\",\"displayNameI18n\":\"Operation.create\",\"permissionCode\":\"IamTenant:write\",\"sortId\":\"40\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"更新\",\"resourceCode\":\"update\",\"displayNameI18n\":\"Operation.update\",\"permissionCode\":\"IamTenant:write,IamTenant:read\",\"sortId\":\"39\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"删除\",\"resourceCode\":\"delete\",\"displayNameI18n\":\"Operation.delete\",\"permissionCode\":\"IamTenant:write\",\"sortId\":\"38\"},{\"displayType\":\"PERMISSION\",\"displayName\":\"详情\",\"resourceCode\":\"detail\",\"displayNameI18n\":\"Operation.detail\",\"permissionCode\":\"IamTenant:read\",\"sortId\":\"37\"}]}]}"
            };
            // 插入多层级资源权限初始数据
            try {
                for (String resourcePermissionJson : RESOURCE_PERMISSION_DATA) {
                    IamResourceListVO permissionListVO = JSON.toJavaObject(resourcePermissionJson, IamResourceListVO.class);
                    resourcePermissionService.deepCreateResourceAndChildren(permissionListVO);
                }
            } catch (BusinessException e){
                log.error("初始化资源权限数据出错: {}，请手动配置前端资源初始的权限数据", e.getMessage());
            }
        }
    }
}
