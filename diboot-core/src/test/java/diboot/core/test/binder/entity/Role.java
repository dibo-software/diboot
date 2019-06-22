package diboot.core.test.binder.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.diboot.core.entity.BaseEntity;

import java.util.Date;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
public class Role extends BaseEntity {
    private static final long serialVersionUID = 3701095453152116088L;

    private String name;

    private String code;

    @JSONField(serialize = false)
    public Date createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
