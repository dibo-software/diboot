package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamResourcePermission;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : uu
 * @version : v1.0
 * @Date 2021/5/7  13:18
 */
@Getter
@Setter
@Accessors(chain = true)
public class InvalidResourcePermissionVO  extends IamResourcePermission {

    private static final long serialVersionUID = 6643651522844488124L;

    // display_type字段的关联数据字典
    public static final String DICT_RESOURCE_PERMISSION_TYPE = "RESOURCE_PERMISSION_TYPE";

    // 字段关联：this.parent_id=id
    @BindField(entity = IamResourcePermission.class, field = "displayName", condition = "this.parent_id=id")
    private String parentDisplayName;

    // 关联数据字典：RESOURCE_PERMISSION_TYPE
    @BindDict(type = DICT_RESOURCE_PERMISSION_TYPE, field = "displayType")
    private String displayTypeLabel;

    private List<InvalidResourcePermissionVO> children;

    /**
     * 当前权限无效的的uri集合
     */
    private Set<String> invalidApiSetList;

    public void addInvalidApiSet(String invalidApiSet) {
        if (V.isEmpty(invalidApiSetList)) {
            invalidApiSetList = new HashSet<>();
        }
        invalidApiSetList.add(invalidApiSet);
    }
}
