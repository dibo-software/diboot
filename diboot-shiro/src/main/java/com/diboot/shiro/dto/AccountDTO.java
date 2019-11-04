package com.diboot.shiro.dto;

import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.enums.IUserType;
import lombok.Getter;
import lombok.Setter;

/**
 * 账号修改类
 * @author : wee
 * @version : v2.0
 * @Date 2019-10-23  13:57
 */
@Getter
@Setter
public class AccountDTO {

    /**
     * 账号id
     * {@link SysUser#getId()}
     */
    private Long accountId;

    /**
     * 账号
     */
    private String username;

    /**
     * 旧密码
     */
    private String oldPassword;


    /**
     * 新密码
     */
    private String password;

    /**
     * 新重复密码
     */
    private String rePassword;

    /**
     * 用户类型
     */
    private String userType;


}
