package com.diboot.iam.service.impl;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.Encryptor;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.mapper.IamAccountMapper;
import com.diboot.iam.service.IamAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* 认证用户相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Service
@Slf4j
public class IamAccountServiceImpl extends BaseIamServiceImpl<IamAccountMapper, IamAccount> implements IamAccountService {

    @Override
    public boolean createEntity(IamAccount iamAccount) {
        // 生成加密盐并加密
        encryptSecret(iamAccount);
        // 保存
        try{
            return super.createEntity(iamAccount);
        }
        catch (Exception e){ // 重复账号创建会异常
            log.warn("保存账号异常: "+e.getMessage(), e);
            throw new BusinessException(Status.FAIL_VALIDATION, "该账号可能已存在，请重新设置！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createEntities(List<IamAccount> accountList){
        if(V.notEmpty(accountList)){
            accountList.stream().forEach(account->{
                // 生成加密盐并加密
                encryptSecret(account);
            });
        }
        // 保存
        try{
            return super.createEntities(accountList);
        }
        catch (Exception e){ // 重复账号创建会异常
            log.warn("保存账号异常: "+e.getMessage(), e);
            throw new BusinessException(Status.FAIL_VALIDATION, "账号中可能包含已存在账号，请检查！");
        }
    }

    /**
     * 加密账号密码
     * @param iamAccount
     */
    private void encryptSecret(IamAccount iamAccount){
        if(Cons.DICTCODE_AUTH_TYPE.PWD.name().equals(iamAccount.getAuthType())){
            if(V.isEmpty(iamAccount.getSecretSalt())){
                // 生成加密盐并加密
                String salt = S.cut(S.newUuid(), 8);
                iamAccount.setSecretSalt(salt);
            }
            String encryptedStr = Encryptor.encrypt(iamAccount.getAuthSecret(), iamAccount.getSecretSalt());
            iamAccount.setAuthSecret(encryptedStr);
        }
    }
}
