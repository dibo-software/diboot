package com.diboot.iam.service.impl;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.iam.service.BaseIamService;
import lombok.extern.slf4j.Slf4j;

/**
* 自定义BaseService接口实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Slf4j
public class BaseIamServiceImpl<M extends BaseCrudMapper<T>, T> extends BaseServiceImpl<M, T> implements BaseIamService<T> {

}
