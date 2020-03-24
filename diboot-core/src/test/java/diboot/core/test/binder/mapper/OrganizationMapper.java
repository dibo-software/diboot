package diboot.core.test.binder.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import diboot.core.test.binder.entity.Organization;
import org.apache.ibatis.annotations.Mapper;

/**
 * 单位Mapper
 * @author mazc@dibo.ltd
 * @version 2018/12/22
 */
@Mapper
public interface OrganizationMapper extends BaseCrudMapper<Organization> {

}

