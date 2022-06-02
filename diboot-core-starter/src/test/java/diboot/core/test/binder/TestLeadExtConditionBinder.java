/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.FieldBinderVO;
import diboot.core.test.binder.vo.UserExtVO;
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
 * 测试主表扩展条件
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/6/2
 * Copyright © diboot.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestLeadExtConditionBinder {
    @Autowired
    UserService userService;

    @Test
    public void testBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        List<User> userList = userService.getEntityList(queryWrapper);
        // 自动绑定
        List<UserExtVO> voList = Binder.convertAndBindRelations(userList, UserExtVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(UserExtVO vo : voList){
            if(vo.getGender().equals("F")){
                Assert.assertNotNull(vo.getDeptName());
                Assert.assertNotNull(vo.getDeptNameLike());
                Assert.assertNotNull(vo.getDeptNameIn());
            }
            else {
                Assert.assertNull(vo.getDeptName());
                Assert.assertNull(vo.getDeptNameLike());
                Assert.assertNull(vo.getDeptNameIn());
            }
            if(vo.getDepartmentId().equals(10002l)) {
                Assert.assertNotNull(vo.getDeptId());
                Assert.assertNotNull(vo.getRoleCodes());
            }
            else{
                Assert.assertNull(vo.getDeptId());
            }
            System.out.println(JSON.stringify(vo));
        }
    }

}
