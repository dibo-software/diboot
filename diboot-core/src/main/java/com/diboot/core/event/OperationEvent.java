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
package com.diboot.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * 操作事件
 *
 * @author : JerryMa
 * @version : v3.0.0
 * @date 2023/05/24
 */
public class OperationEvent extends ApplicationEvent {
    private static final long serialVersionUID = 8508501973156800727L;

    /**
     * 事件类型
     */
    private String type;

    public OperationEvent(String eventType, Object source) {
        super(source);
        this.type = eventType;
    }

    public String getType() {
        return type;
    }

}