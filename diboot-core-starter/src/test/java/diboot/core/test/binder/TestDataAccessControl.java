package diboot.core.test.binder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.data.access.DataAccessInterface;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.dto.DepartmentDTO;
import diboot.core.test.binder.entity.CcCityInfo;
import diboot.core.test.binder.entity.Department;
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
        DataAccessInterface checkImpl = ContextHelper.getBean(DataAccessInterface.class);
        Assert.assertTrue(checkImpl != null);
        List<CcCityInfo> ccCityInfoList = ccCityInfoService.list(null);
        Assert.assertTrue(V.isEmpty(ccCityInfoList));
    }
}
