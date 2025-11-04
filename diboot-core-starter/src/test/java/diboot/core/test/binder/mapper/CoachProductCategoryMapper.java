package diboot.core.test.binder.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import diboot.core.test.binder.entity.CoachProductCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoachProductCategoryMapper extends BaseCrudMapper<CoachProductCategory> {

}