package diboot.core.test.binder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.mapper.UserMapper;
import diboot.core.test.binder.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 员工相关Service
 * @author mazc@dibo.ltd
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
