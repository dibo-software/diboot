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
package com.diboot.core.util;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * Snowflake ID生成器控件
 * @author mazc@dibo.ltd
 * @version v3.0
 * @date 2023/01/11
 *
 */
public class IdGenerator {

    /***
     * 生成下一个id
     * @return
     */
    public static synchronized long nextId() {
        //暂调用IdWorker保证序列一致
        return IdWorker.getId();
    }

    /**
     * 生成String类型的雪花id
     * @return
     */
    public static synchronized String nextIdStr() {
        return IdWorker.getIdStr();
    }

    /**
     * 生成String类型的UUID
     * @return
     */
    public static String newUUID() {
        return S.newUuid();
    }

}