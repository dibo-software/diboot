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
package com.diboot.core.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * diboot-core配置文件类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/08/06
 */
@ConfigurationProperties(prefix = "diboot.core")
public class CoreProperties {
    /**
     * 每页记录数量
     */
    private int pageSize = 20;
    /**
     * 每批次数量
     */
    private int batchSize = 1000;
    /**
     * 是否初始化，默认true自动安装SQL
     */
    private boolean initSql = true;

    public boolean isInitSql() {
        return initSql;
    }

    public void setInitSql(boolean initSql) {
        this.initSql = initSql;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

}
