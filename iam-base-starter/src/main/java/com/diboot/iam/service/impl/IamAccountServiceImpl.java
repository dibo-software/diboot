package com.diboot.iam.service.impl;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.ChangePwdDTO;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.mapper.IamAccountMapper;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.util.IamSecurityUtils;
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
        IamSecurityUtils.encryptPwd(iamAccount);
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
                IamSecurityUtils.encryptPwd(account);
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


    @Override
    public boolean changePwd(ChangePwdDTO changePwdDTO, IamAccount iamAccount) throws Exception {
        // 验证账号信息是否存在
        if (iamAccount == null){
            throw new BusinessException(Status.FAIL_OPERATION, "账号信息不存在");
        }
        // 验证当前账号登录方式
        if (!Cons.DICTCODE_AUTH_TYPE.PWD.name().equals(iamAccount.getAuthType())){
            throw new BusinessException(Status.FAIL_OPERATION, "该账号登录方式不支持更改密码");
        }
        // 验证密码一致性
        if (V.notEquals(changePwdDTO.getPassword(), changePwdDTO.getConfirmPassword())){
            throw new BusinessException(Status.FAIL_OPERATION, "密码与确认密码不一致，请重新输入");
        }
        // 验证旧密码是否正确
        String oldAuthSecret = IamSecurityUtils.encryptPwd(changePwdDTO.getOldPassword(), iamAccount.getSecretSalt());
        if (V.notEquals(oldAuthSecret, iamAccount.getAuthSecret())){
            throw new BusinessException(Status.FAIL_OPERATION, "旧密码错误，请重新输入");
        }

        // 更改密码
        iamAccount.setAuthSecret(changePwdDTO.getPassword());
        IamSecurityUtils.encryptPwd(iamAccount);
        boolean success = this.updateEntity(iamAccount);

        return success;
    }
}
