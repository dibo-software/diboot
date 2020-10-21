package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindField;
import diboot.core.test.binder.entity.CcCityInfo;
import lombok.Data;

/**
 * <Description>
 *
 * @author mazc
 * @version v1.0
 * @date 2020/09/29
 */
@Data
public class CcCityInfoVO extends CcCityInfo {

    @BindField(entity = CcCityInfo.class, field = "regionName", condition = "this.parent_id=cc_city_info.region_id AND cc_city_info.parent_id=region_id")
    private String provenceName;

}
