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
package diboot.core.test.util;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.PropertiesUtils;
import diboot.core.test.StartupApplication;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 配置文件读取测试
 * @author mazc@dibo.ltd
 * @version 1.0
 * @date 2019/06/02
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class PropertiesTest {

    @Autowired
    private Environment environment;

    @Test
    public void testGetString(){
        String str1 = PropertiesUtils.get("spring.datasource.url");
        String str2 = PropertiesUtils.get("spring.datasource.username");
        Assert.assertNotNull(str1);
        Assert.assertNotNull(str2);
        System.out.println(BaseConfig.getPageSize());
        Assert.assertTrue(BaseConfig.getPageSize() == 20);
        Assert.assertTrue(BaseConfig.getBatchSize() == 1000);
    }

    @Test
    public void testGetNumber(){
        Integer num = PropertiesUtils.getInteger("spring.datasource.hikari.maximum-pool-size");
        Assert.assertTrue(num > 0 );
    }

    @Test
    public void testDatasourceUrl(){
        String jdbcUrl = null;
        if(jdbcUrl == null){
            String names = environment.getProperty("spring.shardingsphere.datasource.names");
            if(names != null){
                jdbcUrl = environment.getProperty("spring.shardingsphere.datasource."+ names.split(",")[0] +".url");
            }
        }
        if(jdbcUrl == null){
            String urlConfigItem = environment.getProperty("diboot.datasource.url.config");
            if(urlConfigItem != null){
                jdbcUrl = environment.getProperty(urlConfigItem);
            }
        }
        System.out.println(jdbcUrl);
        Assert.assertTrue(jdbcUrl != null || ContextHelper.getJdbcUrl() != null);
    }
}
