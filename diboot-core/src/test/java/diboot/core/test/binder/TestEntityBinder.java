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
import com.diboot.core.util.BeanUtils;
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
 * @author mazc@dibo.ltd
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
        List<EntityBinderVO> voList = Binder.convertAndBindRelations(userList, EntityBinderVO.class);
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
        // 单个entity接口测试
        EntityBinderVO singleVO = BeanUtils.convert(userList.get(0), EntityBinderVO.class);
        Binder.bindRelations(singleVO);
        // 验证直接关联和通过中间表间接关联的绑定
        Assert.assertEquals(singleVO.getDepartmentId(), singleVO.getDepartment().getId());
        Assert.assertNotNull(singleVO.getDepartment().getOrgId());
        // 测试绑定VO
        Assert.assertNotNull(singleVO.getOrganizationVO());
        System.out.println(JSON.stringify(singleVO.getOrganizationVO()));
        System.out.println(JSON.stringify(singleVO));
    }

}
