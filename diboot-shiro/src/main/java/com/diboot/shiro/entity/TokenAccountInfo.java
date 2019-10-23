package com.diboot.shiro.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * token中的用户信息
 * @author : wee
 * @version : v2.0
 * @Date 2019-10-14  18:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenAccountInfo implements Serializable {

    private static final long serialVersionUID = 8134572626042791766L;
    /**
     * 账号
     */
    private String account;

    /**
     * 用户类型
     */
    private String userType;
}
