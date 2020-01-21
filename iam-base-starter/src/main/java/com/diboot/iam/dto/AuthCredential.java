package com.diboot.iam.dto;

import com.diboot.iam.entity.IamUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 登录凭证
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class AuthCredential implements Serializable {
    private static final long serialVersionUID = -4721950772621829194L;

    /**
     * 用户类型的Class
     */
    private Class userTypeClass = IamUser.class;

    @NotNull(message = "认证方式不能为空")
    private String authType;
    /**
     * 记住我
     */
    private boolean rememberMe;
    /**
     * 账号
     * @return
     */
    public abstract String getAuthAccount();

    /**
     * 认证密码凭证
      */
    public abstract String getAuthSecret();

    /**
     * 获取用户类型
     * @return
     */
    public String getUserType(){
        return userTypeClass.getSimpleName();
    }
}
