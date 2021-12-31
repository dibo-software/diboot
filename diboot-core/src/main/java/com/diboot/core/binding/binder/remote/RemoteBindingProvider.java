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
package com.diboot.core.binding.binder.remote;

import com.diboot.core.vo.JsonResult;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 远程绑定Provider接口
 * @author JerryMa
 * @version v2.4.0
 * @date 2021/11/3
 * Copyright © diboot.com
 */
public interface RemoteBindingProvider {

    /**
     * 加载请求数据
     * @param remoteBindDTO
     * @return
     */
    @PostMapping("/common/remoteBinding")
    JsonResult<String> loadBindingData(RemoteBindDTO remoteBindDTO);

}
