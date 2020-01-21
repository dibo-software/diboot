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
