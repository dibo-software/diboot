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
package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.entity.IamClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 三方客户端 VO定义
 *
 * @author wind
 * @version 3.5.1
 * @date 2025/1/13
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamClientVO extends IamClient {
    @Serial
    private static final long serialVersionUID = 8928260369300882232L;

    @BindDict(type = "ACCOUNT_STATUS", field = "status")
    private LabelValue statusLabel;

}
