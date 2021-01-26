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
package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
* 角色 Entity定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Getter @Setter @Accessors(chain = true)
public class IamRole extends BaseEntity {
    private static final long serialVersionUID = -1186305888909118267L;

    public IamRole(){}
    public IamRole(String name, String code){
        this.name = name;
        this.code = code;
    }

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private Long tenantId;

    // 名称
    @NotNull(message = "名称不能为空")
    @Length(max=50, message="名称长度应小于50")
    @TableField()
    private String name;

    // 编码
    @NotNull(message = "编码不能为空")
    @Length(max=50, message="编码长度应小于50")
    @TableField()
    private String code;

    // 备注
    @TableField()
    private String description;

}