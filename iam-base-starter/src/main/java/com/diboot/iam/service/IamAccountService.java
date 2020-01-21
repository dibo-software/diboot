package com.diboot.iam.service;

import com.diboot.iam.entity.IamAccount;

import java.util.List;

/**
* 认证用户相关Service
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
public interface IamAccountService extends BaseIamService<IamAccount> {
    /**
     * 保存账号（密码存储前加密）
     * @param iamAccount
     * @return
     */
    @Override
    boolean createEntity(IamAccount iamAccount);

    /**
     * 批量创建Entity
     * @param accountList
     * @return
     */
    boolean createEntities(List<IamAccount> accountList);

}