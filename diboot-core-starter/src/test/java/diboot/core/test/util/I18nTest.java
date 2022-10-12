package diboot.core.test.util;

import com.diboot.core.util.I18n;
import diboot.core.test.StartupApplication;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class I18nTest {

    @Test
    public void testI18n() {
        String msg = (I18n.message("sys.message.args", "A"));
        System.out.println(msg);
        Assert.assertTrue(msg.contains("测试"));
        msg = (I18n.message("sys.message.def"));
        System.out.println(msg);
        Assert.assertTrue(msg.contains("default msg"));
    }

}
