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
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.binding.annotation.BindI18n;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.entity.route.RouteMeta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 前端资源权限 Entity定义
 *
 * @author yangzhao
 * @version 2.0.0
 * @date 2020-02-27
 * Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_resource")
public class IamResource extends BaseEntity<String> {
    private static final long serialVersionUID = -6133621123987747250L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    /**
     * 应用模块
     */
    @TableField
    private String appModule;

    // 父级菜单
    @TableField()
    private String parentId;

    // 展现类型
    @NotNull(message = "{validation.iamResource.displayType.NotNull.message}")
    @Length(max = 20, message = "{validation.iamResource.displayType.Length.message}")
    @TableField()
    private String displayType;

    // 显示名称
    @NotNull(message = "{validation.iamResource.displayName.NotNull.message}")
    @Length(max = 100, message = "{validation.iamResource.displayName.Length.message}")
    @BindQuery(comparison = Comparison.LIKE)
    @BindI18n("displayNameI18n")
    private String displayName;

    /**
     * 显示名称国际化资源标识
     */
    private String displayNameI18n;

    // 权限编码
    @Length(max = 200, message = "{validation.iamResource.routePath.Length.message}")
    @TableField()
    private String routePath;

    // 前端资源编码
    @NotNull(message = "{validation.iamResource.resourceCode.NotNull.message}")
    @Length(max = 50, message = "{validation.iamResource.resourceCode.Length.message}")
    @TableField()
    private String resourceCode;

    // 权限编码
    @Length(max = 200, message = "{validation.iamResource.permissionCode.Length.message}")
    @TableField()
    private String permissionCode;

    // meta配置
    @Length(max = 200, message = "{validation.iamResource.meta.Length.message}")
    @TableField()
    private String meta;

    // 状态
    @Length(max = 10, message = "{validation.iamResource.status.Length.message}")
    @TableField()
    private String status;

    // 排序号
    @TableField
    private Long sortId;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private RouteMeta routeMeta;

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public BaseEntity<String> setId(String id) {
        super.setId(id);
        return this;
    }

    public RouteMeta getRouteMeta() {
        if (V.notEmpty(routeMeta)) {
            return routeMeta;
        }
        if (V.isEmpty(this.getMeta())) {
            return new RouteMeta();
        }
        return JSON.parseObject(this.getMeta(), RouteMeta.class);
    }

    public void setRouteMeta(RouteMeta routeMeta) {
        this.routeMeta = routeMeta;
        routeMeta = V.isEmpty(routeMeta) ? new RouteMeta() : routeMeta;
        this.setMeta(JSON.stringify(routeMeta));
    }

    /***
     * 获取权限码列表
     * @return
     */
    public String[] getPermissionCodes() {
        if (V.isEmpty(permissionCode)) {
            return null;
        }
        return S.split(permissionCode);
    }

    /***
     * 设置权限码列表
     * @param permissionCodes
     */
    public void setPermissionCodes(List<String> permissionCodes) {
        if (V.isEmpty(permissionCodes)) {
            this.setPermissionCode(null);
        }
        this.setPermissionCode(S.join(permissionCodes));
    }

}
