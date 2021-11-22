package com.diboot.core.service;

import com.diboot.core.dto.CascaderDTO;
import com.diboot.core.vo.LabelValue;

import java.util.List;

/**
 * 级联Service，如需要，业务对象实现即可
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/11/22  14:26
 */
public interface CascaderService {

    /**
     * 获取级联的配置数据
     *
     * @param cascaderDTO
     * @return
     * @throws Exception
     */
    List<LabelValue> getCascaderLabelValue(CascaderDTO cascaderDTO) throws Exception;
}
