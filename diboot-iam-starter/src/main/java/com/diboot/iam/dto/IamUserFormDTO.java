/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.dto;

import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.entity.IamUserPosition;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * 用户表单信息接收类
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamUserFormDTO extends IamUser {
    private static final long serialVersionUID = -3201792449485186314L;

    // 认证方式
    private String authType = Cons.DICTCODE_AUTH_TYPE.PWD.name();

    private String username;

    private String password;

    private String accountStatus;

    private String accountId;

    private List<String> roleIdList;

    private List<IamUserPosition> userPositionList;
}
