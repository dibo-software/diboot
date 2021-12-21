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
package com.diboot.file.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.ListUtils;
import com.diboot.core.config.BaseConfig;
import com.diboot.file.excel.BaseExcelModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 分页读取Excel
 *
 * @author wind
 * @version v2.4.0
 * @date 2021/11/22
 */
@Slf4j
public abstract class PageReadExcelListener<T extends BaseExcelModel> extends ReadExcelListener<T> {

    private static final Integer BATCH_COUNT = BaseConfig.getBatchSize();

    /**
     * 缓存数据列表
     */
    @Getter
    private final List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    protected CompletableFuture<Boolean> completableFuture;

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (completableFuture != null) {
            completableFuture.join();
        }
        if (CollectionUtils.isNotEmpty(cachedDataList)) {
            handle(cachedDataList);
        }
        super.finish();
    }

    @Override
    protected void cachedData(T data) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            if (completableFuture != null) {
                completableFuture.join();
            }
            completableFuture = asyncHandle(new ArrayList<>(cachedDataList));
            cachedDataList.clear();
        }
    }

    /**
     * 异步处理数据，提高效率
     *
     * @param dataList
     * @return
     */
    @Async("applicationTaskExecutor")
    public CompletableFuture<Boolean> asyncHandle(List<T> dataList) {
        super.handle(dataList);
        return CompletableFuture.completedFuture(true);
    }
}
