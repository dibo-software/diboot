package diboot.core.test.binder.entity;

import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Stock extends BaseEntity {

    private Long productId;

    private Long locId;

    private String title;

    @BindQuery(entity = Product.class, field = "productName",
            condition = "this.product_id=product_rel.orig_product_id and product_rel.tmr_product_id=id")
    @BindField(entity = Product.class, field = "productName",
            condition = "this.product_id=product_rel.orig_product_id and product_rel.tmr_product_id=id")
    private String newProductName;

}