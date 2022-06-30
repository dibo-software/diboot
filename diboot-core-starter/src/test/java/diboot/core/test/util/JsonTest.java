/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package diboot.core.test.util;

import com.diboot.core.util.D;
import com.diboot.core.util.JSON;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.PagingJsonResult;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Role;
import diboot.core.test.binder.entity.User;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JSON单元测试
 * @author mazc@dibo.ltd
 * @version 1.0
 * @date 2021/01/22
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class JsonTest {

    @Test
    public void testJsonDateConvert(){
        User user = new User();
        user.setId(123L);
        user.setUsername("zhangs").setCreateTime(new Date());
        user.setBirthdate(D.convert2Date("1988-09-12 12:34"));
        user.setCreateTime(new Date());
        String jsonStr = JSON.stringify(user);
        Assert.assertTrue(jsonStr != null);
        System.out.println(jsonStr);
        User user2 = JSON.toJavaObject(jsonStr, User.class);
        Assert.assertTrue("1988-09-12".equals(D.convert2DateString(user2.getBirthdate())));
    }

    @Test
    public void testJsonConvert(){
        Role role = new Role();
        role.setCreateTime(new Date());
        role.setId(1L);
        role.setCode("ADMIN").setName("管理员");

        String jsonStr = JSON.stringify(role);
        Assert.assertTrue(jsonStr != null);
        System.out.println(jsonStr);
    }


    @Test
    public void testJsonResult(){
        User user = new User();
        user.setId(123L);
        user.setUsername("zhangs").setCreateTime(new Date());
        user.setBirthdate(D.convert2Date("1988-09-12 12:34"));
        List<User> userList = new ArrayList<>();
        userList.add(user);

        Pagination pagination = new Pagination();
        pagination.setTotalCount(100).setPageIndex(2);
        JsonResult jsonResult = JsonResult.OK(userList).bindPagination(pagination);

        String jsonStr = JSON.toJSONString(jsonResult);
        PagingJsonResult pagingJsonResult = JSON.toJavaObject(jsonStr, PagingJsonResult.class);

        Assert.assertTrue(pagingJsonResult.getPage().getPageIndex() == 2);
        List<User> userList1 = (List<User>)pagingJsonResult.getData();
        Assert.assertTrue(userList1 != null && userList1.size() == 1);
    }

}
