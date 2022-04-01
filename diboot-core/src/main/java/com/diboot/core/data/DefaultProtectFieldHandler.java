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

import com.diboot.core.util.Encryptor;
import com.diboot.core.util.S;

/**
 * 保护字段默认处理器
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/03/25
 */
public class DefaultProtectFieldHandler implements ProtectFieldHandler {

    @Override
    public String encrypt(Class<?> clazz, String fieldName, String content) {
        return Encryptor.encrypt(content, fieldName);
    }

    @Override
    public String decrypt(Class<?> clazz, String fieldName, String content) {
        return Encryptor.decrypt(content, fieldName);
    }

    @Override
    public String mask(Class<?> clazz, String fieldName, String content) {
        if (S.isBlank(content)) {
            return S.EMPTY;
        }
        int length = content.length();
        switch (length) {
            case 11:
                // 11位手机号，保留前3位和后4位
                return S.replace(content, 3, length - 4, '*');
            case 18:
                // 18位身份证号，保留前6位和后4位
                return S.replace(content, 6, length - 4, '*');
            default:
                // 其他长度，保留前0位和后4位，长度小于5位不脱敏
                return S.replace(content, 0, length - 4, '*');
        }
    }
}
