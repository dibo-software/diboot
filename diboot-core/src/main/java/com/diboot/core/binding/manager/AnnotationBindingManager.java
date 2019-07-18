package com.diboot.core.binding.manager;

import com.diboot.core.binding.RelationsBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 绑定管理器 (已废弃，请调用RelationsBinder)
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/3/30
 * @see RelationsBinder
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
