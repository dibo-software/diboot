package diboot.core.test.binder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import diboot.core.test.binder.entity.Role;
import diboot.core.test.binder.mapper.RoleMapper;
import diboot.core.test.binder.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 员工相关Service
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
