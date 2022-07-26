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
package com.diboot.core.util.init;

/**
 * 对象 初始化器 接口
 * <p>仿照 {@link org.apache.commons.lang3.concurrent.ConcurrentInitializer} 的初始化器接口,
 * 优点是 {@link #get()} 不会抛出受检异常
 * </p>
 *
 * @author Zjp
 * @date 2022/7/15
 * @see org.apache.commons.lang3.concurrent.ConcurrentInitializer
 */
public interface BeanInitializer<T> {
    /**
     * 返回创建好的对象实例, 该方法可能会阻塞, 保证多次调用该方法获取的都是同一实例
     *
     * @return 创建好的对象实例
     * @implSpec 子类 必须保证返回的是同一对象实例
     */
    T get();
}
