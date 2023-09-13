package diboot.core.test.binder.entity;

import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductRel extends BaseEntity {

    private Long origProductId;

    private Long tmrProductId;

}