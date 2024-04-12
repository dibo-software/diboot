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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.CustomerService;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.CustomerVO;
import diboot.core.test.binder.vo.UserVO;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  测试字典绑定
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2021/06/18
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestDictBinder {

    @Autowired
    UserService userService;

    @Autowired
    CustomerService customerService;

    @Test
    public void testBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, "1001", "1002");
        List<User> userList = userService.getEntityList(queryWrapper);
        // 自动绑定
        List<UserVO> voList = Binder.convertAndBindRelations(userList, UserVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(UserVO vo : voList){
            // 验证直接关联和通过中间表间接关联的绑定
            Assert.assertNotNull(vo.getGenderLabel());
            System.out.println(JSON.stringify(vo));
        }
        // 单个entity接口测试
        UserVO singleVO = BeanUtils.convert(userList.get(1), UserVO.class);
        Binder.bindRelations(singleVO);
        // 验证直接关联和通过中间表间接关联的绑定
        Assert.assertNotNull(singleVO.getGenderLabel());
        System.out.println(JSON.stringify(singleVO));
    }

    @Test
    @Transactional
    public void testDictList() {
        List<CustomerVO> customers = customerService.getViewObjectList(Wrappers.lambdaQuery(), null, CustomerVO.class);
        for(CustomerVO customerVO : customers) {
            if(V.notEmpty(customerVO.getExtjsonarr())){
                Assert.assertTrue(customerVO.getChannelLabels().size() == customerVO.getExtjsonarr().size());
            }
        }
    }

}
