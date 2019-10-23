package com.diboot.component.file.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.component.file.entity.BaseFile;
import com.diboot.core.service.BaseService;
import org.springframework.stereotype.Component;

/**
 * @author Lishuaifei
 * @description 基础文件Service
 * @creatime 2019-07-18 15:20
 */
@Component
public interface BaseFileService extends BaseService<BaseFile> {

    /*
    * 自定义获取数据方法，解决查询不出is_deleted=1的数据的问题
    * */
    BaseFile getModel(Wrapper<BaseFile> wrapper);

    /*
     * 自定义更新数据方法，解决无法更新is_deleted=1的数据的问题
     * */
    boolean updateModel(BaseFile model);

}
