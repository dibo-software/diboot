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
package com.diboot.core.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON返回结果
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
public class PagingJsonResult<T> extends JsonResult<T> {
    private static final long serialVersionUID = 1002L;

    /***
     * 分页相关信息
     */
    private Pagination page;

    public PagingJsonResult(){
    }

    public PagingJsonResult(T data){
        this.data(data);
    }

    /**
     * 默认成功，无返回数据
      */
    public PagingJsonResult(JsonResult<T> jsonResult, Pagination pagination){
        super(jsonResult.getCode(), jsonResult.getMsg(), jsonResult.getData());
        this.page = pagination;
    }

    /**
     * 基于IPage<T>转换为PagingJsonResult
     * @param iPage
     * @param
     */
    public PagingJsonResult(IPage<?> iPage){
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
        T data = (T)iPage.getRecords();
        this.data(data);
    }

    public PagingJsonResult setPage(Pagination pagination){
        this.page = pagination;
        return this;
    }

    public Pagination getPage() {
        return page;
    }

}