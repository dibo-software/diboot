package diboot.core.test.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.vo.Pagination;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.dto.PurchaseFormPlanQueryDto;
import diboot.core.test.binder.entity.DbPurchaseFormPlan;
import diboot.core.test.binder.service.DbPurchaseFormPlanSvc;
import diboot.core.test.binder.vo.DbPurchaseFormPlanVO;
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
 * @version v2.2.1
 * @date 2021/4/29
 * Copyright © diboot.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestIssue72 {

    @Autowired
    private DbPurchaseFormPlanSvc dbPurchaseFormPlanSvc;

    @Test
    public void testQueryPage(){
        PurchaseFormPlanQueryDto pfpQueryDto = new PurchaseFormPlanQueryDto();
        pfpQueryDto.setGoodsNm("abcd");
        QueryWrapper<DbPurchaseFormPlan> queryWrapper = QueryBuilder.toQueryWrapper(pfpQueryDto);
        // svc + DTO qw + Bo.class 查询 bo
        // bo必须要有所有this.xxx fields.
        // Invalid property 'purchaseFormPlanId' of bean class [club.walnuts.pms.data.bo.DbPurchaseFormPlanBo]
        Pagination pagination = new Pagination(1);
        pagination.setPageSize(10);

        // 前端传入total 缓存，本次不计算。
        // pagination.setTotalCount(100);
        // 默认asc, pagination.setOrderBy("orderBy=shortName:DESC,age:ASC,birthdate");
        pagination.setOrderBy("purchase_form_plan_id");

        // ### SQL: SELECT purchase_form_plan_id, goods_id FROM tbl_purchase_rel_plan_goods WHERE deleted = 0 AND (purchase_form_plan_id IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)) AND tenant_id = 1
        //### Cause: java.sql.SQLSyntaxErrorException: Unknown column 'deleted' in 'where clause'
        List<DbPurchaseFormPlanVO> p2 = dbPurchaseFormPlanSvc.getViewObjectList(queryWrapper, pagination,
                DbPurchaseFormPlanVO.class);
        Assert.assertTrue(p2 != null && p2.get(0).getName() != null);
    }
}
