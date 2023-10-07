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
package com.diboot.core.extension;

import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.IdGenerator;
import com.diboot.core.vo.LabelValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 序列号生成器的雪花id字符串生成实现
 * @author mazc@dibo.ltd
 * @version v3.1.1
 * @date 2023/10/07
 */
@Slf4j
@Component
public class SnowflakeIDGenerator implements SerialNumberGenerator {

    @Override
    public LabelValue definition() {
        return new LabelValue("雪花ID生成器", "SnowflakeID");
    }

    @Override
    public String generate(Map<String, Object> entityDataMap) {
        return IdGenerator.nextIdStr();
    }

}
