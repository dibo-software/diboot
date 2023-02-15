/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 通用文件VO
 * @author JerryMa
 * @version v3.0.0
 * @date 2023/2/15
 * Copyright © diboot.com
 */
@Getter @Setter @Accessors(chain = true)
public class FileVO implements Serializable {
    private static final long serialVersionUID = -5458566771194201567L;

    private String id;

    private String fileName;

    private String accessUrl;

    private String thumbnailUrl;
}
