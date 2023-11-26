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
package com.diboot.core.data.protect;

/**
 * 数据脱敏 处理器接口
 *
 * @author JerryMa
 * @version v3.1.1
 * @date 2023/10/10
 */
public interface DataMaskHandler {

    /**
     * 脱敏
     * @param fieldVal
     * @return
     */
    String mask(String fieldVal);

}
