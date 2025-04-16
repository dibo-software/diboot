package diboot.core.test.util;

import com.diboot.core.extension.sequence.Part;
import com.diboot.core.extension.sequence.PartGenerator;
import com.diboot.core.extension.sequence.SequenceGenerator;
import com.diboot.core.util.D;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Problem;
import diboot.core.test.binder.service.ProblemService;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
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

    @Autowired
    private ProblemService problemService;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Test
    public void testBuildPart(){
        List<Part> parts =
                Part.cons("No.")
                .append(Part.date(Part.DATE_FORMAT.yyyyMMdd))
                .append(Part.seq(5))
                .append(Part.random(4))
                .append(Part.field("type", 4))
                .build();
        Assert.assertEquals(parts.size(), 5);

        System.out.println(PartGenerator.generate(Part.date(Part.DATE_FORMAT.yy)));
        System.out.println(PartGenerator.generate(Part.date(Part.DATE_FORMAT.yyyy)));
        System.out.println(PartGenerator.generate(Part.date(Part.DATE_FORMAT.yyMM)));
        System.out.println(PartGenerator.generate(Part.date(Part.DATE_FORMAT.yyyyMM)));
        System.out.println(PartGenerator.generate(Part.date(Part.DATE_FORMAT.yyMMdd)));
        System.out.println(PartGenerator.generate(Part.date(Part.DATE_FORMAT.yyyyMMdd)));
    }

    @Test
    public void testGenerate(){
        /*
        List<Part> parts = Part.cons("No.")
                        .append(Part.date(D.FORMAT_DATE_y4Md))
                        .append(Part.seq(4)).build();
        // 初始化序列号生成器
        SequenceGenerator sequenceGenerator = new DefaultSequenceGenerator<Problem>(seqCounter, Problem::getSn, parts);
        */
        String expression = "%04d";
        // 先更新序列号为当天02
        String dateFormat = D.convert2FormatString(new Date(), D.FORMAT_DATE_y4Md);
        Problem problem = problemService.getEntity("2");
        problem.setSn("No." + dateFormat + "0002");
        problemService.updateEntity(problem);

        for (int i = 1; i < 8; i++) {
            String sn = (String)sequenceGenerator.buildFillValue(problem);
            problem.setSn(sn);
            Assert.assertEquals(problem.getSn(), "No."+dateFormat+String.format(expression, i+2));
        }
    }

}
