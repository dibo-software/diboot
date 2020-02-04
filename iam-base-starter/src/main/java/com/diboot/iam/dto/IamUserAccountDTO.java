package com.diboot.iam.dto;

import com.diboot.iam.entity.IamUser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 用户表单信息接收类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamUserAccountDTO extends IamUser {

    private String username;

    private String password;

    private List<Long> roleIdList;
}
