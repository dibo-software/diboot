package diboot.core.test.binder.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import diboot.core.test.binder.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseCrudMapper<Product> {

}