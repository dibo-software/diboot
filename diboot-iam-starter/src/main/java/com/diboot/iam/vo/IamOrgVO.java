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
package com.diboot.iam.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.entity.IamOrg;
import com.diboot.iam.entity.IamUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
* 组织机构 VO定义
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-03
*/
@Getter
@Setter
@Accessors(chain = true)
public class IamOrgVO extends IamOrg  {
    private static final long serialVersionUID = 1503838254395651126L;
    @TableField(exist = false)
    @JsonIgnore
    private LocalDateTime createTime;

    // 数据字典关联
    @BindDict(type="ORG_TYPE", field = "type")
    private LabelValue typeLabel;

    // 字段关联：this.parent_id=id
    @BindField(entity = IamOrg.class, field = "name", condition = "this.parent_id=id")
    private String parentName;

    @BindEntityList(entity = IamOrg.class, condition = "this.id=parent_id")
    private List<IamOrgVO> children;

    // 字段关联：this.parent_id=id
    @BindField(entity = IamUser.class, field = "realname", condition = "this.manager_id=id")
    private String managerName;

}