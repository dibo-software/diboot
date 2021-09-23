package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindFieldList;
import diboot.core.test.binder.entity.DemoTest;
import diboot.core.test.binder.entity.DemoTestJoin;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/9/23
 * Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class DemoTestVO extends DemoTest {

    @BindFieldList(entity = DemoTestJoin.class, field = "email", condition = "this.id=demo_test_id")
    private List<String> emails;

}
