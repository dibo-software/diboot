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
package com.diboot.ai.models.kimi;

import com.diboot.ai.common.AiMessage;
import com.diboot.ai.common.response.AiChoice;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Kimi 备选结果
 * @author JerryMa
 * @version v3.4.0
 * @date 2024-04-28
 */
@Getter @Setter @Accessors(chain = true)
public class KimiChoice extends AiChoice implements Serializable {
    private static final long serialVersionUID = -6133725684226421453L;

    private int index;

    private AiMessage delta;

}
