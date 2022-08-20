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
                RelationsBinder.bind(entityList, true);
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
                RelationsBinder.bind(allEntityList, true);
            }
        }
    }

}
