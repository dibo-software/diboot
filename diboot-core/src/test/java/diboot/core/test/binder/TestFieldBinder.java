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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.Binder;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.DepartmentService;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.FieldBinderVO;
import diboot.core.test.binder.vo.TestDictVo;
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
 * @author mazc@dibo.ltd
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
    @Autowired
    DictionaryService dictionaryService;

    @Test
    public void testBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, 1001L, 1002L);
        List<User> userList = userService.getEntityList(queryWrapper);
        // 自动绑定
        List<FieldBinderVO> voList = Binder.convertAndBindRelations(userList, FieldBinderVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(FieldBinderVO vo : voList){
            // 验证直接关联和通过中间表间接关联的绑定
            Assert.assertNotNull(vo.getDeptName());
            Assert.assertNotNull(vo.getOrgName());
            Assert.assertNotNull(vo.getOrgParentId());
            // 验证枚举值已绑定
            Assert.assertNotNull(vo.getGenderLabel());

            System.out.println(JSON.stringify(vo));
        }
    }

    @Test
    public void testBinderWithMoreCondition(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, 1001L, 1002L);
        List<User> userList = userService.getEntityList(queryWrapper);
        // 自动绑定
        List<UserVO> voList = Binder.convertAndBindRelations(userList, UserVO.class);
        if(V.notEmpty(voList)){
            for(UserVO vo : voList){
                Assert.assertNotNull(vo.getDeptName());
            }
        }
    }


    @Test
    public void testDictVoBind(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "GENDER");
        queryWrapper.gt("parent_id", 0);

        List<Dictionary> list = dictionaryService.getEntityList(queryWrapper);

        List<TestDictVo> voList = Binder.convertAndBindRelations(list, TestDictVo.class);

        for(TestDictVo vo : voList){
            Assert.assertTrue(vo.getParentDict() != null);
            Assert.assertTrue(vo.getParentDictName() != null);
        }
    }

}
