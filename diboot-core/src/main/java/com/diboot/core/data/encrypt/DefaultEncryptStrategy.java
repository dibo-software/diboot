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
package com.diboot.core.data.encrypt;

import com.diboot.core.util.Encryptor;

/**
 * 加密算法默认实现
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/19
 */
public class DefaultEncryptStrategy implements IEncryptStrategy {

    @Override
    public String encrypt(String content) {
        return Encryptor.encrypt(content);
    }

    @Override
    public String decrypt(String content) {
        return Encryptor.decrypt(content);
    }
}
