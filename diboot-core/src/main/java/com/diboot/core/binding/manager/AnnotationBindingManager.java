package com.diboot.core.binding.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.BaseBinder;
import com.diboot.core.binding.EntityBinder;
import com.diboot.core.binding.EntityListBinder;
import com.diboot.core.binding.FieldBinder;
import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.parser.BindAnnotationGroup;
import com.diboot.core.binding.parser.ConditionManager;
import com.diboot.core.binding.parser.FieldAnnotation;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * 绑定管理器 (已废弃，请调用RelationsBinder)
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/3/30
 * @see com.diboot.core.binding.manager.RelationsBinder
 */
@Deprecated
public class AnnotationBindingManager {
    private static final Logger log = LoggerFactory.getLogger(AnnotationBindingManager.class);

    /**
     * 自动转换和绑定VO中的注解关联
     * @param entityList
     * @param voClass
     * @param <E>
     * @param <VO>
     * @return
     */
    public static <E, VO> List<VO> autoConvertAndBind(List<E> entityList, Class<VO> voClass){
        return RelationsBinder.convertAndBind(entityList, voClass);
    }

    /**
     * 自动绑定关联对象
     * @return
     * @throws Exception
     */
    public static <VO> void autoBind(List<VO> voList){
        RelationsBinder.bind(voList);
    }

}
