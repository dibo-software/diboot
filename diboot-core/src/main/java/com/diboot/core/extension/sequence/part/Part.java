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
package com.diboot.core.extension.sequence.part;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 编码组成部分
 * @author JerryMa
 * @version v3.5.0
 * @date 2024/12/20
 */
@Getter @Setter @Accessors(chain = true)
public class Part implements Serializable {
    private static final long serialVersionUID = 894299056820401199L;

    public Part(){}

    public Part(String type, String value, int length) {
        this.type = type;
        this.value = value;
        this.length = length;
    }

    private String type;

    private String value;

    private int length;

}
