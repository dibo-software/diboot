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

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
* 前端资源权限 Entity定义
* @author yangzhao
* @version 2.0.0
* @date 2020-02-27
* Copyright © diboot.com
*/
@Getter @Setter @Accessors(chain = true)
public class IamResourcePermission extends BaseEntity {
    private static final long serialVersionUID = -6133621123987747250L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private Long tenantId;

    /**
     * 应用模块
     */
    @TableField
    private String appModule;

    // 父级菜单
    @NotNull(message = "父级资源不能为空")
    @TableField()
    private Long parentId;

    // 展现类型
    @NotNull(message = "展现类型不能为空")
    @Length(max=20, message="展现类型长度应小于20")
    @TableField()
    private String displayType;

    // 显示名称
    @NotNull(message = "显示名称不能为空")
    @Length(max=100, message="显示名称长度应小于100")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField()
    private String displayName;

    // 前端编码
    @NotNull(message = "前端资源编码不能为空")
    @Length(max=100, message="前端资源编码长度应小于100")
    @TableField()
    private String resourceCode;

    // 权限编码
    @Length(max=200, message="权限编码长度应小于200")
    @TableField()
    private String permissionCode;

    // 排序号
    @TableField
    private Long sortId;

    // 更新时间
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /***
     * 获取权限码列表
     * @return
     */
    public String[] getPermissionCodes() {
        if (V.isEmpty(permissionCode)){
            return null;
        }
        return S.split(permissionCode);
    }

    /***
     * 设置权限码列表
     * @param permissionCodes
     */
    public void setPermissionCodes(List<String> permissionCodes) {
        if (V.isEmpty(permissionCodes)){
            this.setPermissionCode(null);
        }
        this.setPermissionCode(S.join(permissionCodes));
    }

}
