package com.diboot.core.binding;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关联字段绑定
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/19
 */
public class FieldBinder<T> extends BaseBinder<T> {
    private static final Logger log = LoggerFactory.getLogger(FieldBinder.class);

    /**
     * VO对象绑定赋值的属性名列表
     */
    private List<String> annoObjectSetterPropNameList;
    /**
     * DO/Entity对象对应的getter取值属性名列表
     */
    private List<String> referencedGetterColumnNameList;

    /***
     * 构造方法
     * @param serviceInstance
     * @param voList
     */
    public FieldBinder(BaseService<T> serviceInstance, List voList){
        this.referencedService = serviceInstance;
        this.annoObjectList = voList;
        this.queryWrapper = new QueryWrapper<T>();
    }

    /***
     * 指定VO绑定属性赋值的setter和DO/Entity取值的getter方法
     * @param toVoSetter VO中调用赋值的setter方法
     * @param <T1> VO类型
     * @param <T2> DO类型
     * @param <R> set方法参数类型
     * @return
     */
    public <T1,T2,R> FieldBinder<T> link(IGetter<T2> fromDoGetter, ISetter<T1, R> toVoSetter){
        return link(BeanUtils.convertToFieldName(fromDoGetter), BeanUtils.convertToFieldName(toVoSetter));
    }

    /***
     * 指定VO绑定赋值的setter属性名和DO/Entity取值的getter属性名
     * @param toVoField VO中调用赋值的setter属性名
     * @return
     */
    public FieldBinder<T> link(String fromDoField, String toVoField){
        if(annoObjectSetterPropNameList == null){
            annoObjectSetterPropNameList = new ArrayList<>();
        }
        annoObjectSetterPropNameList.add(toVoField);
        if(referencedGetterColumnNameList == null){
            referencedGetterColumnNameList = new ArrayList<>();
        }
        referencedGetterColumnNameList.add(S.toSnakeCase(fromDoField));
        return this;
    }

    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(referencedGetterColumnNameList == null){
            log.error("调用错误：字段绑定必须调用link()指定字段赋值和取值的setter/getter方法！");
            return;
        }
        // 解析默认主键字段名
        String referencedEntityPkName = S.toSnakeCase(referencedEntityPrimaryKey);
        String annoObjectFkFieldName = S.toLowerCaseCamel(annoObjectForeignKey);
        // 提取主键pk列表
        List pkList = BeanUtils.collectToList(annoObjectList, annoObjectFkFieldName);
        // 构建查询条件
        List<String> selectColumns = new ArrayList<>(referencedGetterColumnNameList.size()+1);
        selectColumns.add(referencedEntityPkName);
        selectColumns.addAll(referencedGetterColumnNameList);
        queryWrapper.select(S.toStringArray(selectColumns)).in(referencedEntityPkName, pkList);
        // 获取匹配结果的mapList
        List<Map<String, Object>> mapList = referencedService.getMapList(queryWrapper);
        if(V.isEmpty(mapList)){
            return;
        }
        // 将结果list转换成map
        Map<String, Map<String, Object>> referencedEntityPk2DataMap = new HashMap<>(mapList.size());
        // 转换列名为字段名（MyBatis-plus的getMapList结果会将列名转成驼峰式）
        String referencedEntityPkFieldName = S.toLowerCaseCamel(referencedEntityPkName);
        for(Map<String, Object> map : mapList){
            Object pkVal = map.get(referencedEntityPkFieldName);
            if(pkVal != null){
                referencedEntityPk2DataMap.put(String.valueOf(pkVal), map);
            }
        }
        // 遍历list并赋值
        for(Object annoObject : annoObjectList){
            // 将数字类型转换成字符串，以便解决类型不一致的问题
            String annoObjectId = BeanUtils.getStringProperty(annoObject, annoObjectFkFieldName);
            Map<String, Object> relationMap = referencedEntityPk2DataMap.get(annoObjectId);
            if(relationMap != null){
                for(int i = 0; i< annoObjectSetterPropNameList.size(); i++){
                    BeanUtils.setProperty(annoObject, annoObjectSetterPropNameList.get(i), relationMap.get(S.toLowerCaseCamel(referencedGetterColumnNameList.get(i))));
                }
            }
        }
    }

}
