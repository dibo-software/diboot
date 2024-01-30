/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.config;

import com.diboot.core.util.S;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * diboot-tenant 配置文件类
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "diboot.tenant")
public class TenantProperties {

    /**
     * 平台端Code
     */
    private String platformCode = "00000";


    /**
     * 租户忽略表
     */
    private List<String> ignoreTable;


    /**
     * 租户忽略表前缀
     */
    private List<String> ignoreTablePrefix;


    public boolean isIgnoreTable(String tableName) {
        if (ignoreTable != null && ignoreTable.stream().anyMatch(e -> e.equalsIgnoreCase(tableName))) return true;
        return ignoreTablePrefix != null && ignoreTablePrefix.stream().anyMatch(e -> S.startsWithIgnoreCase(tableName, e));
    }

}
