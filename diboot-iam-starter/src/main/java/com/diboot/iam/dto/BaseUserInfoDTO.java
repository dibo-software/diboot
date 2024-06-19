package com.diboot.iam.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
public class BaseUserInfoDTO implements Serializable {
    private static final long serialVersionUID = 10302L;

    @NotNull(message = "{validation.baseUserInfoDTO.realname.NotNull.message}")
    @Length(max = 50, message = "{validation.baseUserInfoDTO.realname.Length.message}")
    private String realname;

    @NotNull(message = "{validation.baseUserInfoDTO.gender.NotNull.message}")
    @Length(max = 10, message = "{validation.baseUserInfoDTO.gender.Length.message}")
    private String gender;

    @Length(max = 20, message = "{validation.baseUserInfoDTO.mobilePhone.Length.message}")
    private String mobilePhone;

    @Length(max = 50, message = "{validation.baseUserInfoDTO.email.Length.message}")
    private String email;

}
