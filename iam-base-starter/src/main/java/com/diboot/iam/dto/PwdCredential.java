package com.diboot.iam.dto;

import com.diboot.iam.config.Cons;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 登录凭证
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public class PwdCredential extends AuthCredential {
    private static final long serialVersionUID = -5020652642432896556L;

    public PwdCredential(){
        setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name());
    }

    public PwdCredential(String username, String password){
        this.username = username;
        this.password = password;
        setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name());
    }

    // 用户名，同authAccount
    @NotNull(message = "用户名不能为空")
    private String username;

    // 密码
    @NotNull(message = "密码不能为空")
    private String password;

    // 登录的验证码
    private String captcha;

    @Override
    public String getAuthAccount() {
        return this.username;
    }

    @Override
    public String getAuthSecret() {
        return this.password;
    }
}
