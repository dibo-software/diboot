package com.diboot.iam.vo;

import com.diboot.core.util.S;
import com.diboot.iam.entity.IamPermission;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 权限VO
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Getter @Setter @Accessors(chain = true)
public class PermissionVO extends IamPermission {
    private static final long serialVersionUID = 3140824759716297175L;
    public PermissionVO(){}
    public PermissionVO(String application, String type){
        super(application, type);
    }

    private List<PermissionVO> children;

    /**
     * 构建第一级比较字符串(MENU)
     * @return
     */
    public String buildExistKey(){
        String[] fields = {
                getApplication(), getType(), getCode(), getOperationCode()
        };
        return S.join(fields);
    }

    /**
     * 构建操作级别比对字符串
     * @return
     */
    public String buildEqualsKey(){
        String[] fields = {
                getApplication(), getType(), getCode(), getOperationCode(), getName(), getOperationName(), String.valueOf(getSortId())
        };
        return S.join(fields);
    }
}
