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
package com.diboot.iam.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.iam.entity.IamLoginTrace;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 登录记录 DTO
 * @author wind
 * @version v3.0
 * @date 2023/02/27
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamLoginTraceDTO extends IamLoginTrace {
    private static final long serialVersionUID = 2694245822574591716L;

    @BindQuery(column= "create_time", comparison = Comparison.GE)
    private LocalDateTime createTimeBegin;

    @BindQuery(column= "create_time", comparison = Comparison.LT)
    private LocalDateTime createTimeEnd;

    @BindQuery(column= "logout_time", comparison = Comparison.GE)
    private LocalDateTime logoutTimeBegin;

    @BindQuery(column= "logout_time", comparison = Comparison.LT)
    private LocalDateTime logoutTimeEnd;

    public IamLoginTraceDTO setCreateTimeEnd(LocalDateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd.plusDays(1);
        return this;
    }

    public IamLoginTraceDTO setLogoutTimeEnd(LocalDateTime logoutTimeEnd) {
        this.logoutTimeEnd = logoutTimeEnd.plusDays(1);
        return this;
    }
}
