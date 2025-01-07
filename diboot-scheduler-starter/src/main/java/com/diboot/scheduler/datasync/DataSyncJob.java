/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.scheduler.datasync;

import com.diboot.scheduler.datasync.read.Reader;
import com.diboot.scheduler.datasync.write.Writer;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.xml.transform.Transformer;

/**
 * 数据同步Job
 * @author JerryMa
 * @version v3.5.0
 * @date 2025/1/7
 */
public class DataSyncJob extends QuartzJobBean {

    private Reader reader = null;
    private Transformer transformer = null;
    private Writer writer = null;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

    }

    public DataSyncJob read(Reader reader) {
        this.reader = reader;
        return this;
    }

    public DataSyncJob transform(Transformer transformer) {
        this.transformer = transformer;
        return this;
    }

    public DataSyncJob write(Writer writer) {
        this.writer = writer;
        return this;
    }

}
