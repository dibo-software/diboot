package com.diboot.commons.vo;

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

    public Pagination getPage() {
        return page;
    }
}