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

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField()
    private String displayName;

    // 前端编码
    @NotNull(message = "前端资源编码不能为空")
    @Length(max=100, message="前端资源编码长度应小于100")
    @TableField()
    private String resourceCode;

    // 接口列表
    @Length(max=5000, message="接口列表长度应小于5000")
    @TableField()
    private String apiSet;

    // 排序号
    @TableField
    private Long sortId;

    // 更新时间
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Date updateTime;


    /***
     * 获取接口列表
     * @return
     */
    public String[] getApiSetList() {
        if (V.isEmpty(this.getApiSet())){
            return null;
        }
        return S.split(this.getApiSet(), ",");
    }

    /***
     * 设置接口列表
     * @param apiSetList
     */
    public void setApiSetList(List<String> apiSetList) {
        if (V.isEmpty(apiSetList)){
            this.setApiSet(null);
        }
        this.setApiSet(S.join(apiSetList, ","));
    }

}
