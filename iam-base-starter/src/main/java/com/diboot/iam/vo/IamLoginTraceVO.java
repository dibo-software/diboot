package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.iam.entity.IamLoginTrace;
import lombok.Data;

/**
* 登录记录 VO定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Data
public class IamLoginTraceVO extends IamLoginTrace  {
    private static final long serialVersionUID = -753084580143028183L;

    @BindDict(type="AUTH_TYPE", field = "authType")
    private String authTypeLabel;

}