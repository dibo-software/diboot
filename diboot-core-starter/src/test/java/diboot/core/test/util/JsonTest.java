/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package diboot.core.test.util;

import com.diboot.core.util.D;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import diboot.core.test.binder.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * JSON单元测试
 * @author mazc@dibo.ltd
 * @version 1.0
 * @date 2021/01/22
 */
public class JsonTest {

    @Test
    public void testJsonDateConvert(){
        User user = new User();
        user.setId(123L);
        user.setUsername("zhangs").setCreateTime(new Date());
        user.setBirthdate(D.convert2Date("1988-09-12 12:34"));
        String jsonStr = JSON.toJSONString(user);
        Assert.assertTrue(jsonStr != null);

        User user2 = JSON.toJavaObject(jsonStr, User.class);
        Assert.assertTrue("1988-09-12".equals(D.convert2DateString(user2.getBirthdate())));
    }

}
