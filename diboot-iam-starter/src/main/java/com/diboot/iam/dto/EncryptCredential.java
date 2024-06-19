package com.diboot.iam.dto;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.function.Function;

/**
 * 加密认证
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/7/13  09:35
 */
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class EncryptCredential implements Serializable {
    private static final long serialVersionUID = 8178800708883555475L;

    /**
     * 密文
     */
    @NotNull(message = "{validation.encryptCredential.ciphertext.NotNull.message}")
    private String ciphertext;

    /**
     * 获取认证信息
     *
     * @return
     */
    public <T extends AuthCredential> T getAuthCredential(Function<String, String> decrypt, Class<T> authCredentialCls) {
        try {
            String decryptContent = decrypt.apply(ciphertext);
            T result = JSON.parseObject(decryptContent, authCredentialCls);
            String errMsg = V.validateBeanErrMsg(result);
            if (V.notEmpty(errMsg)) {
                throw new BusinessException(Status.FAIL_INVALID_PARAM, errMsg);
            }
            return result;
        } catch (Exception e) {
            log.error("获取认证信息失败！", e);
            throw new BusinessException(e.getMessage());
        }
    }
}
