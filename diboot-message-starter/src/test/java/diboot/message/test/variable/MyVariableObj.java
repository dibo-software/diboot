package diboot.message.test.variable;

import com.diboot.message.annotation.BindVariable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/28
 * Copyright © diboot.com
 */
@Getter @Setter @Accessors(chain = true)
public class MyVariableObj implements Serializable {

    private static final long serialVersionUID = -1993000690817844748L;

    @BindVariable(name = "${验证码}")
    private String vcode;

    @BindVariable(name = "${序列号}")
    private String sn;

}
