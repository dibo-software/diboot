/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * 日期单元测试
 * @author mazc@dibo.ltd
 * @version 1.0
 * @date 2019/06/02
 */
public class DTest {

    @Test
    public void testFuzzyConvert(){
        String[] dateStrArray = {
                "2019-06-02 13:35",
                "2019年6月2日 13:35:00",
                "2019/6/2 13:35:34:000"
        };
        for(String dateStr : dateStrArray){
            Date date = D.fuzzyConvert(dateStr);
            Assert.assertTrue(date != null);
        }
    }

    @Test
    public void testJsonResult(){
        String token = "token";
        JsonResult j1 = new JsonResult(token);
        JsonResult j2 = new JsonResult(token, "申请token成功");
        JsonResult j3 = new JsonResult(Status.OK, token);
        JsonResult j4 = new JsonResult(Status.OK, token, "申请token成功");
        JsonResult j5 = new JsonResult(Status.OK);
        System.out.println(j1.getData());
        System.out.println(j2.getData());
        System.out.println(j3.getData());
        System.out.println(j4.getData());
        System.out.println(j5.getData());
    }
}
