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
package com.diboot.core.extension.sequence;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.diboot.core.util.BeanUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 编码组成部分
 * @author JerryMa
 * @version v3.5.0
 * @date 2024/12/20
 */
@Accessors(chain = true)
public class Part implements Serializable {
    private static final long serialVersionUID = 894299056820401199L;

    /**
     * 日期格式
     */
    public enum DATE_FORMAT {
        yyyy,
        yy,
        yyyyMM,
        yyMM,
        yyyyMMdd,
        yyMMdd,
        MMdd
    }

    @JsonIgnore
    private List<Part> parts;

    public Part(){}

    public Part(String type, String value, int length) {
        this.type = type;
        this.value = value;
        this.length = length;
    }

    @Getter @Setter
    private String type;

    @Getter @Setter
    private String value;

    @Getter @Setter
    private int length;

    public Part append(Part part) {
        if(this.parts == null){
            this.parts = new ArrayList<>();
            this.parts.add(this);
        }
        this.parts.add(part);
        return this;
    }

    public List<Part> build() {
        return parts;
    }

    public static Part cons(String value) {
        return new Part("cons", value, value.length());
    }

    public static Part date(Part.DATE_FORMAT format) {
        return date(format.name());
    }

    public static Part date(String format) {
        return new Part("date", format, format.length());
    }

    public static Part random(int length) {
        return new Part("random", null, length);
    }

    public static Part field(String fieldName, int length) {
        return new Part("field", fieldName, length);
    }

    public static <T> Part field(SFunction<T, ?> fieldGetter, int length) {
        return new Part("field", BeanUtils.convertSFunctionToFieldName(fieldGetter), length);
    }

    public static Part seq(int length) {
        return new Part("seq", null, length);
    }

}
