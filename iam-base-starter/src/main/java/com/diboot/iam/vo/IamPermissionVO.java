package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.iam.entity.IamPermission;
import lombok.Data;

import java.util.List;

/**
* 权限 VO定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Data
@Deprecated
public class IamPermissionVO extends IamPermission {
    private static final long serialVersionUID = -5053188875259947594L;

    // 字段关联：this.id=parent_id
    @BindEntityList(entity = IamPermission.class, condition = "this.id=parent_id")
    private List<IamPermission> children;
}