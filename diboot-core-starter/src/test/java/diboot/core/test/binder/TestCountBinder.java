/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package diboot.core.test.binder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.binding.Binder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.DepartmentService;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.*;
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
 *  测试子项count计数绑定
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/06/22
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestCountBinder {

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
        queryWrapper.in(Department::getId, 10001L, 10003L);
        List<Department> entityList = departmentService.list(queryWrapper);
        // 自动绑定
        List<CountSimpleVO> voList = Binder.convertAndBindRelations(entityList, CountSimpleVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(CountSimpleVO vo : voList){
            // 验证直接关联的绑定
            Assert.assertTrue(vo.getChildrenCount() > 0);
            Assert.assertTrue(vo.getChildrenIds().size() == vo.getChildrenCount());
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
        List<User> userList = userService.getEntityList(queryWrapper);
        // 自动绑定
        List<EntityListComplexVO> voList = Binder.convertAndBindRelations(userList, EntityListComplexVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListComplexVO vo : voList){
            // 验证通过中间表间接关联的绑定
            Assert.assertTrue(vo.getRoleCount() > 0);
            Assert.assertTrue(vo.getRoleCount() == vo.getRoleCodes().size());
            System.out.println(JSON.stringify(vo));
        }
    }

}