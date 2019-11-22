package diboot.core.test.util;

import com.diboot.core.util.PropertiesUtils;
import diboot.core.test.StartupApplication;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 配置文件读取测试
 * @author Mazhicheng
 * @version 1.0
 * @date 2019/06/02
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class PropertiesTest {

    @Test
    public void testGetString(){
        String str1 = PropertiesUtils.get("spring.datasource.url");
        String str2 = PropertiesUtils.get("spring.datasource.username2");
        Assert.assertNotNull(str1);
        Assert.assertNotNull(str2);
    }

    @Test
    public void testGetNumber(){
        Integer num = PropertiesUtils.getInteger("spring.datasource.hikari.maximum-pool-size");
        Assert.assertTrue(num > 0 );
    }
}
