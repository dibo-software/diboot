package diboot.core.test.util;

import com.diboot.core.extension.sequence.Part;
import diboot.core.test.StartupApplication;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 测试流水编号
 * @author JerryMa
 * @version v3.5.0
 * @date 2024/12/21
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestSequence {

    @Test
    public void testBuildPart(){
        List<Part> parts =
                Part.cons("No.")
                .append(Part.date("YYYYMMDD"))
                .append(Part.seq(5))
                .append(Part.random(4))
                .append(Part.field("type", 4))
                .build();
        Assert.assertEquals(parts.size(), 5);
    }

}
