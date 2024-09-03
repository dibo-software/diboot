package diboot.core.test.binder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.PetAdopt;
import diboot.core.test.binder.service.PetAdoptService;
import diboot.core.test.binder.vo.PetAdoptListVO;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 测试多字段关联条件绑定
 * @author JerryMa
 * @version v3.5.0
 * @date 2024/9/3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestBindWithMultiFlds {

    @Autowired
    private PetAdoptService petAdoptService;

    /**
     * 验证多个字段关联的绑定
     */
    @Test
    public void testMultiFldsConditionBinder(){
        LambdaQueryWrapper<PetAdopt> queryWrapper = new QueryWrapper<PetAdopt>().lambda()
                .eq(PetAdopt::getRealname, "张三");
        List<PetAdoptListVO> petAdoptListVOS = petAdoptService.getViewObjectList(queryWrapper, null, PetAdoptListVO.class);
        Assert.assertEquals(petAdoptListVOS.size(), 2);
        for (PetAdoptListVO vo : petAdoptListVOS) {
            if(vo.getPetCategory().equals("CAT")) {
                Assert.assertEquals("#1", vo.getPetCode());
            }
            else {
                Assert.assertEquals("#2", vo.getPetCode());
            }
        }
    }

}
