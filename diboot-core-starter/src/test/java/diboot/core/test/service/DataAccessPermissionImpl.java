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
package diboot.core.test.service;

import com.diboot.core.binding.data.CheckpointType;
import com.diboot.core.binding.data.DataAccessInterface;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数据访问控制测试
 * @author Mazc@dibo.ltd
 * @version v2.0
 * @date 2020/04/24
 */
@Component
public class  DataAccessPermissionImpl implements DataAccessInterface {

    @Override
    public List<Long> getAccessibleIds(CheckpointType type) {
        if(type.equals(CheckpointType.ORG)){
            return Arrays.asList(100001L);
        }
        return Collections.emptyList();
    }
}
