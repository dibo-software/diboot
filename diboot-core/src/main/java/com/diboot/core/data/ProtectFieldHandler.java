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
package com.diboot.core.data;

/**
 * 保护字段处理器
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/03/25
 */
public interface ProtectFieldHandler {

    /**
     * 加密
     *
     * @param content 内容
     * @return 密文
     */
    String encrypt(Class<?> clazz, String fieldName, String content);

    /**
     * 解密
     *
     * @param content 内容
     * @return 明文
     */
    String decrypt(Class<?> clazz, String fieldName, String content);

    /**
     * 脱敏处理
     *
     * @param content 字符串
     * @return 脱敏之后的字符串
     */
    String mask(Class<?> clazz, String fieldName, String content);

}
