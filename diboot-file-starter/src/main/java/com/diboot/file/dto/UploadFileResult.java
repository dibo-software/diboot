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
package com.diboot.file.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 文件上传后的结果
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/1/11  14:49
 */
@Getter
@Setter
@Accessors(chain = true)
public class UploadFileResult implements Serializable {

    private static final long serialVersionUID = -1963273564405503224L;

    /**
     * 随机id
     */
    private String uuid;

    /**
     * 原名称
     */
    private String originalFilename;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 扩展名
     */
    private String ext;

    /**
     * 存储的完整路径
     */
    private String storageFullPath;

}
