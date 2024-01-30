package com.diboot.tenant.vo;

import com.diboot.iam.vo.IamUserVO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 租户管理员信息
 *
 * @author : uu
 * @version : v1.0
 * @Date 2023/12/18  16:25
 */
@Getter@Setter@Accessors(chain = true)
public class TenantAdminUserVO extends IamUserVO {
    private static final long serialVersionUID = 2082776340684016972L;

    /**
     * 账号信息
     */
    private String username;
    /**
     * 账号id
     */
    private String accountId;
}
