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
package com.diboot.ai.init;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.SqlFileInitializer;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.service.IamResourceService;
import com.diboot.iam.vo.IamResourceListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 组件初始化
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/1
 */
@Order(961)
@Slf4j
@Component
@ConditionalOnProperty(prefix = "diboot", name = "init-sql", havingValue = "true")
public class AiPluginInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String initDetectSql = "SELECT id FROM dbt_ai_session";
        if (!SqlFileInitializer.checkSqlExecutable(initDetectSql)) {
            SqlFileInitializer.initBootstrapSql(this.getClass(), "ai");
            // 插入相关数据
            insertInitData();
        }
    }


    private synchronized void insertInitData() {
        // 插入ai组件所需的初始权限数据
        IamResourceService resourcePermissionService = ContextHolder.getBean(IamResourceService.class);
        // 初始化chat ai相关资源
        if (resourcePermissionService != null && !resourcePermissionService.exists(IamResource::getResourceCode, "ChatAI")) {
            List<IamResourceListVO> permissionListVOs = new ArrayList<>();
            IamResourceListVO chatAIPermission = (IamResourceListVO) new IamResourceListVO().setChildren(new ArrayList<>()).setParentId("0").setDisplayType("MENU").setDisplayName("ChatAI").setDisplayNameI18n("Resource.ChatAI").setRoutePath("chat-ai").setResourceCode("ChatAI").setPermissionCode("").setMeta("{\"icon\":\"Element:Cpu\",\"componentPath\":\"@/views/chat-ai/index.vue\",\"keepAlive\":false,\"hidden\":true}").setSortId(90L);
            permissionListVOs.add(chatAIPermission);
            // 插入多层级资源权限初始数据
            try {
                for (IamResourceListVO iamResourceListVO : permissionListVOs) {
                    resourcePermissionService.deepCreateResourceAndChildren(iamResourceListVO);
                }
            } catch (BusinessException e) {
                log.error("初始化资源权限数据出错: {}，请手动配置前端资源初始的权限数据", e.getMessage());
            }
        }
    }

}
