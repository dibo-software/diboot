package diboot.core.test.binder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.data.access.DataAccessInterface;
import com.diboot.core.util.ContextHolder;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.CcCityInfo;
import diboot.core.test.binder.service.CcCityInfoService;
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
 * @author JerryMa
 * @version v2.5.0
 * @date 2022/3/5
 * Copyright © diboot.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestDataAccessControl {

    @Autowired
    private CcCityInfoService ccCityInfoService;

    @Test
    public void testDataControl(){
        DataAccessInterface checkImpl = ContextHolder.getBean(DataAccessInterface.class);
        Assert.assertNotNull(checkImpl);
        List<CcCityInfo> ccCityInfoList = ccCityInfoService.list(new QueryWrapper<>());
        Assert.assertEquals(2, ccCityInfoList.size());
    }
}
