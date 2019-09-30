package com.diboot.core.vo;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 分页 (属性以下划线开头以避免与提交参数字段冲突)
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 */
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
    /***
     * 排序-升序排列的字段
     */
    private List<String> ascList = null;
    /***
     * 降序排列的字段（默认以create_time降序排列，当指定了其他排列方式时以用户指定为准）
     */
    private List<String> descList = new ArrayList<>(Arrays.asList("create_time"));

    public Pagination(){
    }

    /***
     * 指定当前页数
     */
    public Pagination(int pageIndex){
        setPageIndex(pageIndex);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if(pageSize > 1000){
            log.warn("分页pageSize过大，将被调整为默认限值，请检查调用是否合理！pageSize="+ pageSize);
            pageSize = 1000;
        }
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public void setOrderBy(String orderBy){
        if(V.isEmpty(orderBy)){
            return;
        }
        // 先清空默认排序规则
        clearDefaultOrder();
        // 指定新的排序规则
        String[] orderByFields = S.split(orderBy);
        for(String field : orderByFields){
            // orderBy=name:DESC,age:ASC,birthdate
            if(field.contains(":")){
                String[] fieldAndOrder = S.split(field, ":");
                if("DESC".equalsIgnoreCase(fieldAndOrder[1])){
                    if(descList == null){
                        descList = new ArrayList<>();
                    }
                    descList.add(fieldAndOrder[0]);
                }
                else{
                    if(ascList == null){
                        ascList = new ArrayList<>();
                    }
                    ascList.add(fieldAndOrder[0]);
                }
            }
            else{
                if(ascList == null){
                    ascList = new ArrayList<>();
                }
                ascList.add(field);
            }
        }
    }

    /***
     * 清除默认排序
     */
    public void clearDefaultOrder(){
        ascList = null;
        descList = null;
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

    /***
     * 获取数据库字段的列排序，用于service层调用
     * @return
     */
    public List<String> getAscList() {
        return ascList;
    }

    /***
     * 获取数据库字段的列排序,，用于service层调用
     * @return
     */
    public List<String> getDescList() {
        return descList;
    }

}