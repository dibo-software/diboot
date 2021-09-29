/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 无效使用异常类 InvalidUsageException
 *
 * @author : wind
 * @version : v2.3.1
 * @date 2021/08/23
 */
public class InvalidUsageException extends RuntimeException {

    /**
     * 自定义内容提示
     *
     * @param msg
     */
    public InvalidUsageException(String msg) {
        super(msg);
    }

    /**
     * 自定义内容提示
     *
     * @param msg
     */
    public InvalidUsageException(String msg, Throwable ex) {
        super(msg, ex);
    }

    /**
     * 转换为Map
     *
     * @return
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(8);
        map.put("code", getCode());
        map.put("msg", getMessage());
        return map;
    }

    private int getCode() {
        return 5005;
    }

}
