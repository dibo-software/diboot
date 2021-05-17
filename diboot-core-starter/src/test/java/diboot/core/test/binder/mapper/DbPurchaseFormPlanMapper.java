package diboot.core.test.binder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diboot.core.mapper.BaseCrudMapper;
import diboot.core.test.binder.entity.DbPurchaseFormPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/29
 * Copyright © diboot.com
 */
@Mapper
public interface DbPurchaseFormPlanMapper extends BaseCrudMapper<DbPurchaseFormPlan> {

}
