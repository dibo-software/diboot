package diboot.core.test.binder.entity;

import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/9/23
 * Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class DemoTestJoin extends BaseEntity {

    Long demoTestId;

    String name;

    String email;

}
