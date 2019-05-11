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
 * 分页
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
    private int _pageIndex = 1;
    /***
     * 默认每页数量10
     */
    private int _pageSize = BaseConfig.getPageSize();
    /***
     * count总数
     */
    private long _totalCount = 0;
    /***
     * 排序-升序排列的字段
     */
    private List<String> ascList = null;
    /***
     * 降序排列的字段（默认以ID降序排列，当指定了其他排列方式时以用户指定为准）
     */
    private List<String> descList = new ArrayList<>(Arrays.asList(Cons.FieldName.id.name()));

    public Pagination(){
    }

    /***
     * 指定当前页数
     */
    public Pagination(int pageIndex){
        set_pageIndex(pageIndex);
    }

    public int get_pageIndex() {
        return _pageIndex;
    }

    public void set_pageIndex(int _pageIndex) {
        this._pageIndex = _pageIndex;
    }

    public int get_pageSize() {
        return _pageSize;
    }

    public void set_pageSize(int _pageSize) {
        if(_pageSize > 1000){
            log.warn("分页pageSize过大，将被调整为默认限值，请检查调用是否合理！pageSize="+_pageSize);
            _pageSize = 1000;
        }
        this._pageSize = _pageSize;
    }

    public long get_totalCount() {
        return _totalCount;
    }

    public void set_totalCount(long _totalCount) {
        this._totalCount = _totalCount;
    }

    public void set_orderBy(String orderBy){
        if(V.notEmpty(orderBy)){
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
    public int get_totalPage() {
        if(_totalCount <= 0){
            return 0;
        }
        return  (int)Math.ceil((float)_totalCount/_pageSize);
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