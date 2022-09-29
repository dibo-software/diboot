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
package diboot.core.test.binder.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.vo.Pagination;
import diboot.core.test.binder.dto.DepartmentDTO;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.mapper.DepartmentMapper;
import diboot.core.test.binder.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门相关Service实现
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/30
 */
@Slf4j
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public List<Department> list(Wrapper queryWrapper) {
        return getEntityList(queryWrapper);
    }

    @Override
    public List<Department> getDepartmentSqlList(QueryWrapper<DepartmentDTO> queryWrapper, Pagination pagination) {
        return null;
    }

}
