package com.diboot.iam.service;

import com.diboot.core.vo.LabelValue;

import java.util.List;

/**
 * 系统菜单相关共用接口
 * @author mazc@dibo.ltd
 * @version 3.0.1
 * @date 2023-08-15
 */
public interface MenuService {

    /**
     * 获取系统中的所有菜单目录
     * @return
     */
    List<LabelValue> getMenuCatalogues();

}
