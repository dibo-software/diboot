package diboot.core.test.util;

import com.diboot.core.util.D;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * 日期单元测试
 * @author Mazhicheng
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

}
