package diboot.core.test.binder.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.mapper.DepartmentMapper;
import diboot.core.test.binder.service.DepartmentService;
import org.springframework.stereotype.Service;

/**
 * 部门相关Service实现
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentMapper, Department> implements DepartmentService {

}
