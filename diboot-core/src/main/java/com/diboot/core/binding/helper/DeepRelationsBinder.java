package com.diboot.core.binding.helper;

import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.binding.parser.FieldAnnotation;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;

import java.util.ArrayList;
import java.util.List;

/**
 * 关联深度绑定
 * @author Jerry@dibo.ltd
 * @version v2.1.2
 * @date 2020/08/26
 */
public class DeepRelationsBinder {

    /**
     * 深度绑定
     * @param voList
     * @param deepBindEntityAnnoList
     * @param deepBindEntitiesAnnoList
     * @param <VO>
     */
    public static <VO> void deepBind(List<VO> voList, List<FieldAnnotation> deepBindEntityAnnoList, List<FieldAnnotation> deepBindEntitiesAnnoList) {
        if(V.isEmpty(voList)){
            return;
        }
        // 收集待深度绑定的对象集合, 绑定第二层
        if(V.notEmpty(deepBindEntityAnnoList)){
            for(FieldAnnotation anno : deepBindEntityAnnoList){
                String entityFieldName = anno.getFieldName();
                List entityList = BeanUtils.collectToList(voList, entityFieldName);
                RelationsBinder.bind(entityList, false);
            }
        }
        if(V.notEmpty(deepBindEntitiesAnnoList)){
            for(FieldAnnotation anno : deepBindEntitiesAnnoList){
                String entityFieldName = anno.getFieldName();
                List allEntityList = new ArrayList();
                for(VO vo : voList){
                    List entityList = (List) BeanUtils.getProperty(vo, entityFieldName);
                    if(V.notEmpty(entityList)){
                        allEntityList.addAll(entityList);
                    }
                }
                RelationsBinder.bind(allEntityList, false);
            }
        }
    }

}
