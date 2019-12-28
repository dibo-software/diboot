package com.diboot.core.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页 (属性以下划线开头以避免与提交参数字段冲突)
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 */
@Getter @Setter @Accessors(chain = true)
public class Pagination implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Pagination.class);

    private static final long serialVersionUID = -4083929594112114522L;

    /***
     * 当前页
     */
    private int pageIndex = 1;
    /***
     * 默认每页数量10
     */
    private int pageSize = BaseConfig.getPageSize();
    /***
     * count总数
     */
    private long totalCount = 0;
    /**
     * 默认排序
     */
    private static final String DEFAULT_ORDER_BY = Cons.FieldName.id.name()+":"+Cons.ORDER_DESC;
    /**
     * 排序
     */
    private String orderBy = DEFAULT_ORDER_BY;

    public Pagination(){
    }

    /***
     * 指定当前页数
     */
    public Pagination(int pageIndex){
        setPageIndex(pageIndex);
    }

    public void setPageSize(int pageSize) {
        if(pageSize > 1000){
            log.warn("分页pageSize过大，将被调整为默认限值，请检查调用是否合理！pageSize="+ pageSize);
            pageSize = 1000;
        }
        this.pageSize = pageSize;
    }

    /***
     * 获取总的页数
     * @return
     */
    public int getTotalPage() {
        if(totalCount <= 0){
            return 0;
        }
        return  (int)Math.ceil((float) totalCount / pageSize);
    }

    /**
     * 清空默认排序
     */
    public void clearDefaultOrder(){
        // 是否为默认排序
        if(V.equals(orderBy, DEFAULT_ORDER_BY)){
            orderBy = null;
        }
    }

    /**
     * 转换为IPage
     * @param <T>
     * @return
     */
    public <T> IPage<T> toIPage(){
        List<OrderItem> orderItemList = null;
        // 解析排序
        if(V.notEmpty(this.orderBy)){
            orderItemList = new ArrayList<>();
            // orderBy=shortName:DESC,age:ASC,birthdate
            String[] orderByFields = S.split(this.orderBy);
            for(String field : orderByFields){
                if(field.contains(":")){
                    String[] fieldAndOrder = S.split(field, ":");
                    String columnName = S.toSnakeCase(fieldAndOrder[0]);
                    if(Cons.ORDER_DESC.equalsIgnoreCase(fieldAndOrder[1])){
                        orderItemList.add(OrderItem.desc(columnName));
                    }
                    else{
                        orderItemList.add(OrderItem.asc(columnName));
                    }
                }
                else{
                    orderItemList.add(OrderItem.asc(S.toSnakeCase(field)));
                }
            }
        }
        IPage<T> page = new Page<T>()
                .setCurrent(getPageIndex())
                .setSize(getPageSize())
                // 如果前端传递过来了缓存的总数，则本次不再count统计
                .setTotal(getTotalCount() > 0? -1 : getTotalCount());
        if(orderItemList != null){
            ((Page<T>) page).addOrder(orderItemList);
        }
        return page;
    }
}