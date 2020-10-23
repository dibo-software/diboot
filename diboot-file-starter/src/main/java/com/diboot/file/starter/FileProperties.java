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
package com.diboot.file.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author diboot
 */
@Data
@ConfigurationProperties(prefix = "diboot.component.file")
public class FileProperties {

    /**
     * 是否初始化，默认true自动安装SQL
     */
    private boolean initSql = true;

    /**
     * 最大上传大小（默认10M）
     */
    private Long maxUploadSize = 10 * 1024 * 1024L;
}
