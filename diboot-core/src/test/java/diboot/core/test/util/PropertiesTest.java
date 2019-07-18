package diboot.core.test.util;

import com.diboot.core.util.PropertiesUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * 配置文件读取测试
 * @author Mazhicheng
 * @version 1.0
 * @date 2019/06/02
 */
public class PropertiesTest {

    @Test
    public void testGetString(){
        String str1 = PropertiesUtils.get("spring.datasource.url");
        String str2 = PropertiesUtils.get("spring.datasource.url", "application.properties.bak");
        Assert.assertNotNull(str1);
        Assert.assertNotNull(str2);
    }

    @Test
    public void testGetNumber(){
        Integer num = PropertiesUtils.getInteger("spring.datasource.hikari.maximum-pool-size");
        Assert.assertTrue(num > 0 );
    }
}
