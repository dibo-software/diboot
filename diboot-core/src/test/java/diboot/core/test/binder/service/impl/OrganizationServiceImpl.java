package diboot.core.test.binder.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import diboot.core.test.binder.entity.Organization;
import diboot.core.test.binder.mapper.OrganizationMapper;
import diboot.core.test.binder.service.OrganizationService;
import org.springframework.stereotype.Service;

/**
 * 单位相关Service实现
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@Service
public class OrganizationServiceImpl extends BaseServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

}
