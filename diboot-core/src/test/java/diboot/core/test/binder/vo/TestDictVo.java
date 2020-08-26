package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.vo.DictionaryVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 测试字典VO
 * @author Jerry@dibo.ltd
 * @version v2.1
 * @date 2020/08/19
 */
@Getter @Setter
public class TestDictVo extends DictionaryVO {

    @BindEntity(entity= Dictionary.class, condition="this.type=type AND this.parent_id=id")
    private Dictionary parentDict;

    @BindField(entity= Dictionary.class, field = "itemName", condition="this.type=type AND this.parent_id=id")
    private String parentDictName;

}
