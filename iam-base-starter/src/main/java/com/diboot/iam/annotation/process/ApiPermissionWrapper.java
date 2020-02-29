package com.diboot.iam.annotation.process;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <Description>
 *
 * @author Mazhicheng
 * @version v2.0
 * @date 2020/02/28
 */
@Getter @Setter
public class ApiPermissionWrapper implements Serializable {
    private static final long serialVersionUID = 544405101900928328L;

    public ApiPermissionWrapper(){}

    public ApiPermissionWrapper(String classTitle){
        this.classTitle = classTitle;
    }

    // 类别标题
    private String classTitle;

    /**
     * 子节点
     */
    private List<ApiPermission> children;

}
