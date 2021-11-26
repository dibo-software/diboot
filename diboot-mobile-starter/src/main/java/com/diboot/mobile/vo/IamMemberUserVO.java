/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.mobile.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.iam.entity.IamUser;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端用户VO(绑定User)
 *
 * @author : uu
 * @version : v2.4.0
 * @Copyright © diboot.com
 * @Date 2021/8/31  18:00
 */
@Getter
@Setter
public class IamMemberUserVO extends IamMemberVO {

    private static final long serialVersionUID = -6745864206439737631L;
    /**
     * 关联字典：GENDER
     */
    @BindDict(type = DICT_GENDER, field = "gender")
    private String genderLabel;

    /**
     * 关联字典：account_status
     */
    @BindDict(type = DICT_MEMBER_STATUS, field = "status")
    private String statusLabel;

    /**
     * IamMember绑定IamUser
     */
    @BindEntity(entity = IamUser.class, condition = "this.user_id = id")
    private IamUser iamUser;

}
