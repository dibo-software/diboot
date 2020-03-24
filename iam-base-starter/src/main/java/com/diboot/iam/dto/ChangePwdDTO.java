package com.diboot.iam.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
public class ChangePwdDTO implements Serializable {

    private static final long serialVersionUID = 8027129084861679777L;

    @NotNull(message = "旧密码不能为空")
    private String oldPassword;

    @NotNull(message = "新密码不能为空")
    private String password;

    @NotNull(message = "确认密码不能为空")
    private String confirmPassword;
}
