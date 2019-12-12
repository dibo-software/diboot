package diboot.core.test.binder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.EntityBinderVO;
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
 *  测试字段绑定
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/06/22
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestEntityBinder {

    @Autowired
    UserService userService;

    @Test
    public void testBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, 1001L, 1002L);
        List<User> userList = userService.list(queryWrapper);
        // 自动绑定
        List<EntityBinderVO> voList = RelationsBinder.convertAndBind(userList, EntityBinderVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityBinderVO vo : voList){
            // 验证直接关联和通过中间表间接关联的绑定
            Assert.assertEquals(vo.getDepartmentId(), vo.getDepartment().getId());
            Assert.assertNotNull(vo.getDepartment().getOrgId());
            // 测试绑定VO
            Assert.assertNotNull(vo.getOrganizationVO());
            System.out.println(JSON.stringify(vo.getOrganizationVO()));
            System.out.println(JSON.stringify(vo));
        }
    }

}
