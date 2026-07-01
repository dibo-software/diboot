package diboot.core.test.binder.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import diboot.core.test.binder.entity.CoachProductCategory;
import diboot.core.test.binder.mapper.CoachProductCategoryMapper;
import diboot.core.test.binder.service.CoachProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoachProductCategoryServiceImpl extends BaseServiceImpl<CoachProductCategoryMapper, CoachProductCategory> implements CoachProductCategoryService {

}