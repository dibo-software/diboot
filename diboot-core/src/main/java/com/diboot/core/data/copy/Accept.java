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
package com.diboot.core.data.copy;

import java.lang.annotation.*;

/**
 * 拷贝字段时的非同名字段处理
 * @author mazc@dibo.ltd
 * @version v2.1
 * @date 2020/06/04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Accept {
    /**
     * 接收来源对象的属性名
     * @return
     */
    String name();
    /**
     * source该字段有值时是否覆盖
     * @return
     */
    boolean override() default false;
}