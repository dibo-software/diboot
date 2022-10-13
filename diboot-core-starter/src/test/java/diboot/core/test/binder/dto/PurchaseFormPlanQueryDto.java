package diboot.core.test.binder.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import diboot.core.test.binder.entity.DbGoodsGoodsInfo;
import diboot.core.test.binder.entity.DbPurchaseFormPlan;
import lombok.Data;

/**
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/29
 * Copyright © diboot.com
 */
@Data
public class PurchaseFormPlanQueryDto extends DbPurchaseFormPlan {

    private Long purchaseFormPlanId;

    @BindQuery(comparison = Comparison.EQ,
        entity= DbGoodsGoodsInfo.class, column ="goods_nm",
        condition="this.purchase_form_plan_id = db_purchase_rel_plan_goods.purchase_form_plan_id and "
                + "db_purchase_rel_plan_goods.goods_id=goods_id"
        )
    private String goodsNm;

}
