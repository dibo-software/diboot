package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.iam.entity.IamUser;
import lombok.Data;

/**
* 系统用户 VO定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Data
public class IamUserVO extends IamUser {
    private static final long serialVersionUID = 7571698765478647277L;

    @BindDict(type="GENDER", field = "gender")
    private String genderLabel;

    @BindDict(type="USER_STATUS", field = "status")
    private String statusLabel;
}