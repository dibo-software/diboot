package diboot.core.test.query;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.vo.Pagination;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Stock;
import diboot.core.test.binder.service.StockService;
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
 * 测试 Issue R09
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestIssueR09 {

    @Autowired
    private StockService stockService;

    @Test
    public void testDateCompaire() {
        Stock stock = new Stock();
        stock.setLocId(5L);
        stock.setNewProductName("签字笔");

        Pagination pagination = new Pagination();
        pagination.setOrderBy("locId:DESC,newProductName");

        QueryWrapper<Stock> queryWrapper = QueryBuilder.toQueryWrapper(stock, pagination);
        List<Stock> list = stockService.getEntityList(queryWrapper, pagination);

        Assert.assertTrue(list.size() > 0);
    }

}
