package diboot.core.test.binder.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import diboot.core.test.binder.entity.PetAdopt;
import org.apache.ibatis.annotations.Mapper;

/**
* 领养 相关Mapper层接口定义
* @author MyName
* @version 1.0
* @date 2024-09-03
* Copyright © MyCorp
*/
@Mapper
public interface PetAdoptMapper extends BaseCrudMapper<PetAdopt> {

}