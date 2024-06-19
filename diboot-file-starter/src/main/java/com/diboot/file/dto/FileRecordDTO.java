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
package com.diboot.file.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.file.entity.FileRecord;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 文件记录 DTO
 *
 * @author wind
 * @version v3.0.0
 * @date 2022-05-30
 */
@Getter
public class FileRecordDTO extends FileRecord {
    private static final long serialVersionUID = -7388146735066760480L;

    /**
     * 创建时间-起始
     */
    @BindQuery(comparison = Comparison.GE, column = "create_time")
    private LocalDateTime createTimeBegin;

    /**
     * 创建时间-截止
     */
    @BindQuery(comparison = Comparison.LT, column = "create_time")
    private LocalDateTime createTimeEnd;

    public FileRecord setCreateTimeEnd(LocalDateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd.plusDays(1);
        return this;
    }
}
