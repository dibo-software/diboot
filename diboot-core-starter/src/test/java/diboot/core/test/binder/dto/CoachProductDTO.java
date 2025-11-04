package diboot.core.test.binder.dto;

import com.diboot.core.binding.query.BindQuery;
import diboot.core.test.binder.entity.Product;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author JerryMa
 * @version v3.8.1
 * @date 2025/11/4
 */
@Getter
@Setter
public class CoachProductDTO extends Product {

    /**
     * 教练ID
     */
    @BindQuery(ignore = true)
    private Long coachId;

}
