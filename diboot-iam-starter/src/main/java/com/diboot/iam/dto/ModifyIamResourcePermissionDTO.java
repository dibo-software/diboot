package com.diboot.iam.dto;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 修改权限DTO
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/5/8  12:49
 */
@Getter
@Setter
@Accessors(chain = true)
public class ModifyIamResourcePermissionDTO implements Serializable {
    private static final long serialVersionUID = 211276433727981441L;

    private Long id;

    private String apiSet;

    private List<String> apiSetList;

    /***
     * 设置接口列表
     * @param apiSetList
     */
    public void setApiSetList(List<String> apiSetList) {
        if (V.isEmpty(apiSetList)){
            this.setApiSet(null);
        }
        this.setApiSet(S.join(apiSetList, ","));
    }
}
