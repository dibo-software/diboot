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
package com.diboot.core.data.access;

import java.io.Serializable;
import java.util.List;

/**
 * 数据权限校验扩展接口
 * @author mazc@dibo.ltd
 * @version v2.1
 * @date 2020/04/24
 */
public interface DataAccessInterface {

    /**
     * <h3>可访问的对象ID</h3>
     * <br/>
     * <table border="10">
     * <caption>添加条件规则</caption>
     * <tr>
     * <th>返回值</th>
     * <th>SQL</th>
     * <th>说明</th>
     * </tr>
     * <tr>
     * <td>null</td>
     * <td>-</td>
     * <td>为null不加入条件</td>
     * </tr>
     * <td>[]</td>
     * <td>IS NULL</td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>[10001]</td>
     * <td>= '10001'</td>
     * <td>长度等于1的列表</td>
     * </tr>
     * <tr>
     * <td>[10001, 10002] &nbsp</td>
     * <td>IN ('10001', '10002') &nbsp</td>
     * <td>长度大于1的列表</td>
     * </tr>
     * </table>
     */
    List<? extends Serializable> getAccessibleIds(Class<?> entityClass, String fieldName);

}
