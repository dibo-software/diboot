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
package com.diboot.core.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.entity.I18nConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 国际化配置 VO
 *
 * @author wind
 * @version v3.0.0
 * @date 2022-10-12
 */
@Getter
@Setter
@Accessors(chain = true)
public class I18nConfigVO extends I18nConfig {
    private static final long serialVersionUID = 5679642618572762054L;

    @BindDict(field = "type", type = DICT_I18N_TYPE)
    private LabelValue typeLabel;
}
