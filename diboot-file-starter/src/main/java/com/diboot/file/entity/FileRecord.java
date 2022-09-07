/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.file.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 文件记录 Entity定义
 *
 * @author wind
 * @version v3.0.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@Getter
@Setter
@Accessors(chain = true)
public class FileRecord extends BaseEntity {
    private static final long serialVersionUID = -202L;

    public FileRecord(){
    }

    public FileRecord(String id) {
        this.setId(id);
    }

    /**
     * 主键类型为String型UUID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 租户ID
     */
    @JsonIgnore
    private String tenantId;

    /**
     * 应用模块
     */
    private String appModule;

    /**
     * MD5标识
     */
    private String md5;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小（Byte）
     */
    private Long fileSize;

    /**
     * 存储路径
     */
    @JsonIgnore
    private String storagePath;

    /**
     * 访问地址
     */
    private String accessUrl;

    /**
     * 缩略图地址
     */
    private String thumbnailUrl;

    /**
     * 备注
     */
    private String description;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

}
