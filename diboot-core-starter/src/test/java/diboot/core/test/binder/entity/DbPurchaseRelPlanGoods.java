package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/29
 * Copyright © diboot.com
 */
@Getter @Setter @Accessors(chain = true)
public class DbPurchaseRelPlanGoods extends MyBaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "rel_id", type = IdType.ASSIGN_ID)
    private Long relId;

    private Long purchaseFormPlanId;

    private Long goodsId;

}
