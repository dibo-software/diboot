package com.diboot.iam.annotation.process;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* 权限 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Getter @Setter @Accessors(chain = true)
public class ApiPermission implements Serializable {
    private static final long serialVersionUID = -1234249053749049729L;

    // 类别
    @JSONField(serialize = false)
    private String className;

    // 类别标题
    @JSONField(serialize = false)
    private String classTitle;

    // 接口名称
    private String apiName;

    /**
     * 接口Method
     */
    private String apiMethod;

    // 接口URI
    private String apiUri;

    // ID标识
    private String value;

    // 权限许可编码
    @JSONField(serialize = false)
    private String permissionCode;

    public String buildUniqueKey(){
        return className + "," + apiMethod + "," + apiUri + "," + permissionCode;
    }
}