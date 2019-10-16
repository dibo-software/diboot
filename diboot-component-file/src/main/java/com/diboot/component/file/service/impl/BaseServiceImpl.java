package com.diboot.component.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diboot.component.file.config.BaseConfig;
import com.diboot.component.file.config.BaseCons;
import com.diboot.component.file.service.BaseService;
import com.diboot.component.file.utils.S;
import com.diboot.component.file.utils.V;
import com.diboot.component.file.vo.KeyValue;
import com.diboot.component.file.vo.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * @author Lishuaifei
 * @description 文件实现类
 * @creatime 2019-07-18 15:29
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {

    private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);

    /***
     * 获取当前的Mapper对象
     * @return
     */
    protected M getMapper(){
        return baseMapper;
    }

    @Override
    public T getEntity(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean createEntity(T entity) {
        if(entity == null){
            warning("createModel", "参数entity为null");
            return false;
        }
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createEntities(Collection entityList) {
        if(V.isEmpty(entityList)){
            warning("createEntities", "参数entityList为空!");
            return false;
        }
        // 批量插入
        return super.saveBatch(entityList, BaseConfig.getBatchSize());
    }

    @Override
    public boolean updateEntity(T entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean updateEntity(T entity, Wrapper updateCriteria) {
        return super.update(entity, updateCriteria);
    }

    @Override
    public boolean updateEntity(Wrapper updateWrapper) {
        return super.update(null, updateWrapper);
    }

    @Override
    public boolean createOrUpdateEntity(T entity) {
        return super.saveOrUpdate(entity);
    }

    @Override
    public boolean createOrUpdateEntities(Collection entityList) {
        if(V.isEmpty(entityList)){
            warning("createOrUpdateEntities", "参数entityList为空!");
            return false;
        }
        // 批量插入
        return super.saveOrUpdateBatch(entityList, BaseConfig.getBatchSize());
    }

    @Override
    public boolean deleteEntity(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean deleteEntities(Wrapper queryWrapper) throws Exception {
        return super.remove(queryWrapper);
    }

    @Override
    public int getEntityListCount(Wrapper queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    public List<T> getEntityList(Wrapper queryWrapper) {
        return getEntityList(queryWrapper, null);
    }

    @Override
    public List<T> getEntityList(Wrapper queryWrapper, Pagination pagination) {
        if(pagination != null){
            IPage<T> page = convertToIPage(pagination);
            page = super.page(page, queryWrapper);
            // 如果重新执行了count进行查询，则更新pagination中的总数
            if(page.isSearchCount()){
                pagination.setTotalCount(page.getTotal());
            }
            return page.getRecords();
        }
        else{
            List<T> list = super.list(queryWrapper);
            if(list == null){
                list = Collections.emptyList();
            }
            else if(list.size() > BaseConfig.getBatchSize()){
                log.warn("单次查询记录数量过大，返回结果数={}", list.size());
            }
            return list;
        }
    }

    @Override
    public List<T> getEntityListByIds(List ids) {
        QueryWrapper<T> queryWrapper = new QueryWrapper();
        queryWrapper.in(BaseCons.FieldName.id.name(), ids);
        return getEntityList(queryWrapper);
    }

    @Override
    public List<T> getEntityListLimit(Wrapper queryWrapper, int limitCount) {
        IPage<T> page = new Page<>(1, limitCount);
        page = super.page(page, queryWrapper);
        return page.getRecords();
    }

    @Override
    public List<Map<String, Object>> getMapList(Wrapper queryWrapper) {
        return getMapList(queryWrapper, null);
    }

    @Override
    public List<Map<String, Object>> getMapList(Wrapper queryWrapper, Pagination pagination) {
        if(pagination != null){
            IPage<T> page = convertToIPage(pagination);
            IPage<Map<String, Object>> resultPage = super.pageMaps(page, queryWrapper);
            // 如果重新执行了count进行查询，则更新pagination中的总数
            if(page.isSearchCount()){
                pagination.setTotalCount(page.getTotal());
            }
            return resultPage.getRecords();
        }
        else{
            List<Map<String, Object>> list = super.listMaps(queryWrapper);
            if(list == null){
                list = Collections.emptyList();
            }
            else if(list.size() > BaseConfig.getBatchSize()){
                log.warn("单次查询记录数量过大，返回结果数={}", list.size());
            }
            return list;
        }
    }

    @Override
    public List<KeyValue> getKeyValueList(Wrapper queryWrapper) {
        String sqlSelect = queryWrapper.getSqlSelect();
        // 最多支持3个属性：k, v, ext
        if(V.isEmpty(sqlSelect) || S.countMatches(sqlSelect, BaseCons.SEPARATOR_COMMA) > 2){
            log.error("调用错误: getKeyValueList必须用select依次指定返回的Key,Value, ext键值字段，如: new QueryWrapper<Dictionary>().lambda().select(Dictionary::getItemName, Dictionary::getItemValue)");
            return Collections.emptyList();
        }
        // 获取mapList
        List<Map<String, Object>> mapList = super.listMaps(queryWrapper);
        if(mapList == null){
            return Collections.emptyList();
        }
        // 转换为Key-Value键值对
        String[] keyValueArray = sqlSelect.split(BaseCons.SEPARATOR_COMMA);
        List<KeyValue> keyValueList = new ArrayList<>(mapList.size());
        for(Map<String, Object> map : mapList){
            if(map.get(keyValueArray[0]) != null){
                KeyValue kv = new KeyValue((String)map.get(keyValueArray[0]), map.get(keyValueArray[1]));
                if(keyValueArray.length > 2){
                    kv.setExt(map.get(keyValueArray[2]));
                }
                keyValueList.add(kv);
            }
        }
        return keyValueList;
    }

    /*@Override
    public <VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> aClass) {
        List<T> entityList = getEntityList(queryWrapper, pagination);
        // 自动转换为VO并绑定关联对象
        List<VO> voList = RelationsBinder.convertAndBind(entityList, voClass);
        return null;
    }*/

    /*@Override
    public <VO> VO getViewObject(Serializable id, Class<VO> aClass) {
        T entity = getEntity(id);
        if(entity == null){
            return null;
        }
        List<T> enityList = new ArrayList<>();
        enityList.add(entity);
        // 绑定
        List<VO> voList = RelationsBinder.convertAndBind(enityList, voClass);
        return null;
    }*/

    /***
     * 转换为IPage
     * @param pagination
     * @return
     */
    protected Page<T> convertToIPage(Pagination pagination){
        if(pagination == null){
            return null;
        }
        Page<T> page = new Page<T>()
                .setCurrent(pagination.getPageIndex())
                .setSize(pagination.getPageSize())
                // 如果前端传递过来了缓存的总数，则本次不再count统计
                .setTotal(pagination.getTotalCount() > 0? -1 : pagination.getTotalCount());
        // 排序
        if(V.notEmpty(pagination.getAscList())){
            pagination.getAscList().forEach(s -> {
                page.addOrder(OrderItem.asc(s));
            });
        }
        if(V.notEmpty(pagination.getDescList())){
            pagination.getDescList().forEach(s -> {
                page.addOrder(OrderItem.desc(s));
            });
        }
        return page;
    }

    /***
     * 打印警告信息
     * @param method
     * @param message
     */
    private void warning(String method, String message){
        log.warn(this.getClass().getName() + "."+ method +" 调用错误: "+message+", 请检查！");
    }

}
