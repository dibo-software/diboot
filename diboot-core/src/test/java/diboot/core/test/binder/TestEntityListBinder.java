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
import diboot.core.test.binder.vo.DepartmentVO;
import diboot.core.test.binder.vo.EntityListComplexBinderVO;
import diboot.core.test.binder.vo.EntityListSimpleBinderVO;
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
public class TestEntityListBinder {

    @Autowired
    UserService userService;

    @Autowired
    DepartmentService departmentService;

    /**
     * 验证直接关联的绑定
     */
    @Test
    public void testSimpleBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getId, 10001L);
        List<Department> entityList = departmentService.getEntityList(queryWrapper);
        // 自动绑定
        List<EntityListSimpleBinderVO> voList = RelationsBinder.convertAndBind(entityList, EntityListSimpleBinderVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListSimpleBinderVO vo : voList){
            // 验证直接关联的绑定
            Assert.assertTrue(V.notEmpty(vo.getChildren()));
            System.out.println(JSON.stringify(vo));

            if(vo.getChildren() != null){
                for(DepartmentVO dept : vo.getChildren()){
                    System.out.println(dept.toString());
                }
            }
        }
    }

    /**
     * 验证通过中间表间接关联的绑定
     */
    @Test
    public void testComplexBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, 1001L, 1002L);
        List<User> userList = userService.list(queryWrapper);
        // 自动绑定
        List<EntityListComplexBinderVO> voList = RelationsBinder.convertAndBind(userList, EntityListComplexBinderVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListComplexBinderVO vo : voList){
            // 验证通过中间表间接关联的绑定
            Assert.assertTrue(V.notEmpty(vo.getRoleList()));
            System.out.println(JSON.stringify(vo));
        }
    }

}
