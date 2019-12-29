package com.diboot.core.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON返回结果
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 */
public class PagingJsonResult extends JsonResult{
    private static final long serialVersionUID = 1001L;

    /***
     * 分页相关信息
     */
    private Pagination page;

    /**
     * 默认成功，无返回数据
      */
    public PagingJsonResult(JsonResult jsonResult, Pagination pagination){
        super(jsonResult.getCode(), jsonResult.getMsg(), jsonResult.getData());
        this.page = pagination;
    }

    /**
     * 基于IPage<T>转换为PagingJsonResult
     * @param iPage
     * @param <T>
     */
    public <T> PagingJsonResult(IPage<T> iPage){
        Pagination pagination = new Pagination();
        pagination.setPageIndex((int)iPage.getCurrent());
        pagination.setPageSize((int)iPage.getSize());
        pagination.setTotalCount(iPage.getTotal());
        if(V.notEmpty(iPage.orders())){
            List<String> orderByList = new ArrayList<>();
            iPage.orders().stream().forEach(o ->{
                if(o.isAsc()){
                    orderByList.add(o.getColumn());
                }
                else{
                    orderByList.add(o.getColumn() + ":" + Cons.ORDER_DESC);
                }
            });
            pagination.setOrderBy(S.join(orderByList));
        }
        this.page = pagination;
        this.data(iPage.getRecords());
    }

    public Pagination getPage() {
        return page;
    }

}