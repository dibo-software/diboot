/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.mobile.init;

import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.SqlFileInitializer;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.service.IamAccountService;
import com.diboot.starter.IamAutoConfig;
import com.diboot.mobile.entity.IamMember;
import com.diboot.mobile.service.IamMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 移动端组件相关的初始化
 * @author mazc@dibo.ltd
 * @version v2.3.1
 * @date 2021/09/14
 */
@Order(931)
@Slf4j
@Component
@AutoConfigureAfter({IamAutoConfig.class})
@ConditionalOnProperty(prefix = "diboot", name = "init-sql", havingValue = "true")
public class MobilePluginInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查数据库表是否已存在
        String initDetectSql = "SELECT id FROM dbt_iam_member WHERE id='0'";
        if(!SqlFileInitializer.checkSqlExecutable(initDetectSql)){
            log.info("diboot-mobile 初始化SQL ...");
            // 执行初始化SQL
            SqlFileInitializer.initBootstrapSql(this.getClass(), "mobile");
            // 插入IamMember登录数据
            insertInitData();
            log.info("diboot-mobile 初始化SQL完成.");
        }
    }

    /**
     * 插入初始化数据
     */
    private synchronized void insertInitData(){
        // 插入mobile组件所需的数据字典
        DictionaryService dictionaryService = ContextHolder.getBean(DictionaryService.class);
        if(dictionaryService != null && !dictionaryService.exists(Dictionary::getType, "MEMBER_STATUS")){
            String[] DICT_INIT_DATA = {
                    "{\"type\":\"MEMBER_STATUS\", \"itemName\":\"用户状态\", \"description\":\"Member用户状态\", \"children\":[{\"itemName\":\"有效\", \"itemNameI18n\":\"Dictionary.MEMBER_STATUS.A\", \"itemValue\":\"A\", \"sortId\":1},{\"itemName\":\"无效\", \"itemNameI18n\":\"Invalid\", \"Dictionary.MEMBER_STATUS.I\":\"I\", \"sortId\":2}]}",
            };
            // 插入数据字典
            for(String dictJson : DICT_INIT_DATA){
                DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
                dictionaryService.createDictAndChildren(dictVo);
            }
        }
        IamMemberService iamMemberService = ContextHolder.getBean(IamMemberService.class);
        if(iamMemberService != null && iamMemberService.getEntityListCount(null) == 0){
            IamMember iamMember = new IamMember();
            iamMember.setOrgId(Cons.ID_PREVENT_NULL)
                    .setNickname("DIBOOT-Mobile")
                    .setStatus(Cons.DICTCODE_ACCOUNT_STATUS.A.name())
                    .setUserType(IamMember.class.getSimpleName())
                    .setUserId(Cons.ID_PREVENT_NULL)
                    .setGender("M")
                    .setOpenid(S.newUuid());
            iamMemberService.createEntity(iamMember);

            // 创建账号
            IamAccount iamAccount = new IamAccount();
            iamAccount.setUserType(IamMember.class.getSimpleName()).setUserId(iamMember.getId())
                    .setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name())
                    .setAuthAccount("admin").setAuthSecret("123456");
            ContextHolder.getBean(IamAccountService.class).createEntity(iamAccount);
        }

    }
}
