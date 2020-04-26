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
package com.diboot.core.binding.data;

/**
 * checkpoint类型
 * @author Mazc@dibo.ltd
 * @version v2.1
 * @date 2020/04/24
 */
public enum CheckpointType {

    USER(0), // 相关用户
    ORG(1), // 组织

    OBJ_1(2), // 其他1
    OBJ_2(3), // 其他2
    OBJ_3(4); // 其他3

    private int index;
    CheckpointType(int index){
        this.index = index;
    }

    public int index(){
        return index;
    }
}