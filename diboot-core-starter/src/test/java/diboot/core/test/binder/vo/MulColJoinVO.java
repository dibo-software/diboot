package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.annotation.BindFieldList;
import com.diboot.core.entity.Dictionary;
import diboot.core.test.binder.entity.Organization;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <Description>
 *
 * @author mazc
 * @version v2.1.2
 * @date 2020/08/25
 */
@Getter @Setter
public class MulColJoinVO {

    private Long dictId;
    private String dictType;

    private Long orgPid;
    private String telphone;

    @BindEntity(entity= Dictionary.class, condition="this.dict_type=type AND this.dict_id=id")
    private Dictionary parentDict;

    @BindField(entity= Dictionary.class, field = "itemName", condition="this.dict_type=type AND this.dict_id=id")
    private String parentDictName;

    @BindEntityList(entity = Organization.class, condition = "this.org_pid=parent_id AND this.telphone=telphone")
    private List<Organization> orgList;

    @BindFieldList(entity = Organization.class, field = "name", condition = "this.org_pid=parent_id AND this.telphone=telphone")
    private List<String> orgNames;

}
