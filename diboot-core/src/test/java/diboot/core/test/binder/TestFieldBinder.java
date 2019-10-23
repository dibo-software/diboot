package diboot.core.test.binder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.DepartmentService;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.FieldBinderVO;
import diboot.core.test.binder.vo.UserVO;
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
public class TestFieldBinder {

    @Autowired
    UserService userService;
    @Autowired
    DepartmentService departmentService;

    @Test
    public void testBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, 1001L, 1002L);
        List<User> userList = userService.list(queryWrapper);
        // 自动绑定
        List<FieldBinderVO> voList = RelationsBinder.convertAndBind(userList, FieldBinderVO.class);
        // 验证绑定结果
        if(V.notEmpty(voList)){
            for(FieldBinderVO vo : voList){
                // 验证直接关联和通过中间表间接关联的绑定
                Assert.assertNotNull(vo.getDeptName());
                Assert.assertNotNull(vo.getOrgName());
                Assert.assertNotNull(vo.getOrgTelphone());
                // 验证枚举值已绑定
                Assert.assertNotNull(vo.getGenderLabel());

                System.out.println(JSON.stringify(vo));
            }
        }
    }

    @Test
    public void testBinderWithMoreCondition(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, 1001L, 1002L);
        List<User> userList = userService.list(queryWrapper);
        // 自动绑定
        List<UserVO> voList = RelationsBinder.convertAndBind(userList, UserVO.class);
        if(V.notEmpty(voList)){
            for(UserVO vo : voList){
                Assert.assertNotNull(vo.getDeptName());
            }
        }
    }

}
