package diboot.core.test.binder.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <Description>
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/06
 */
@Getter
@Setter
@Accessors(chain = true)
public class OrganizationVO {

    private Long parentId;

    private String name;

}
