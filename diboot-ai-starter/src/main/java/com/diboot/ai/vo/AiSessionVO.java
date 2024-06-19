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
package com.diboot.ai.vo;

import com.diboot.ai.entity.AiSession;
import com.diboot.ai.entity.AiSessionRecord;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 会话 VO
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/6
 */
@Getter@Setter@Accessors(chain = true)
public class AiSessionVO extends AiSession {
    @Serial
    private static final long serialVersionUID = 2692633068140907334L;
    /**
     * 关联会话记录
     */
    @BindEntityList(entity = AiSessionRecord.class, condition = "this.id=session_id", orderBy = "createTime:ASC")
    private String sessionRecordList;
}
