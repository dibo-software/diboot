package com.diboot.message.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 基础变量值
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/25  17:48
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseVariableData implements Serializable {

    /**
     * 姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;
}
