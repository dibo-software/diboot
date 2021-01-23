package com.diboot.iam.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.iam.entity.IamUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* 用户搜索 DTO定义
* @author Yangzhao
* @version 2.0
* @date 2021-01-21
 * Copyright © dibo.ltd
*/
@Getter
@Setter
@Accessors(chain = true)
public class IamUserSearchDTO extends IamUser {
    private static final long serialVersionUID = 6992509629060077210L;

    @BindQuery(comparison = Comparison.LIKE)
    private String realname;

    @BindQuery(comparison = Comparison.LIKE)
    private String userNum;

    @BindQuery(comparison = Comparison.LIKE)
    private String email;

    @BindQuery(comparison = Comparison.LIKE)
    private String mobilePhone;
}
