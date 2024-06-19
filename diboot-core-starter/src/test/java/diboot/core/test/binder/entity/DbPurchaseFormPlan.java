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
@Getter
@Setter
@Accessors(chain = true)
public class DbPurchaseFormPlan extends MyBaseEntity {
    @TableId(value = "purchase_form_plan_id", type = IdType.ASSIGN_ID)
    private Long purchaseFormPlanId;

    private String name;
}
