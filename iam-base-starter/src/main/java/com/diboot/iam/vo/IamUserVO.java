package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.iam.entity.IamPermission;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUser;
import lombok.Data;

import java.util.List;

/**
* 系统用户 VO定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Data
public class IamUserVO extends IamUser {
    private static final long serialVersionUID = 7571698765478647277L;

    private String username;

    @BindDict(type="GENDER", field = "gender")
    private String genderLabel;

    @BindDict(type="USER_STATUS", field = "status")
    private String statusLabel;

    // 字段关联：this.id=iam_user_role.user_id AND iam_user_role.role_id=id
    @BindEntityList(entity = IamRole.class, condition = "this.id=iam_user_role.user_id AND iam_user_role.role_id=id AND iam_user_role.is_deleted=0")
    private List<IamRole> roleList;
}