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
package com.diboot.core.extension.sequence;

import com.diboot.core.util.S;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * 编号片段生成
 * @author JerryMa
 * @version v3.5.0
 * @date 2024/12/21
 */
@Slf4j
public class PartGenerator {

    public static String generate(Part part) {
        if("cons".equals(part.getType())) {
            return part.getValue();
        }
        if("date".equals(part.getType())) {
            return generateDatePart(part.getValue());
        }
        if("random".equals(part.getType())) {
            return S.newRandomNum(part.getLength());
        }
        return null;
    }

    private static String generateDatePart(String format) {
        LocalDate now = LocalDate.now();
        String currentDate = "";
        if(format.contains("YYYY")) {
            currentDate += now.getYear();
        }
        else if(format.contains("YY")) {
            currentDate += S.substring(S.valueOf(now.getYear()), 2);
        }
        if(format.contains("MM")) {
            currentDate += now.getMonthValue();
        }
        if(format.contains("DD")) {
            currentDate += now.getDayOfMonth();
        }
        return currentDate;
    }
}
