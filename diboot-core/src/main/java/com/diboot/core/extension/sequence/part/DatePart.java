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
package com.diboot.core.extension.sequence.part;

/**
 * 日期类型-编码组成部分
 * @author JerryMa
 * @version v3.5.0
 * @date 2024/12/20
 */
public class DatePart extends Part {
    private static final long serialVersionUID = -2416846293979274779L;

    public DatePart(String format) {
        super("date", format, format.length());
    }

}
