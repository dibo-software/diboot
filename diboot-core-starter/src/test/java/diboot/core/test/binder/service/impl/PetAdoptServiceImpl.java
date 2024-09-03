package diboot.core.test.binder.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import diboot.core.test.binder.entity.PetAdopt;
import diboot.core.test.binder.mapper.PetAdoptMapper;
import diboot.core.test.binder.service.PetAdoptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* 领养 相关Service实现类
* @author MyName
* @version 1.0
* @date 2024-09-03
* Copyright © MyCorp
*/
@Slf4j
@Service
public class PetAdoptServiceImpl extends BaseServiceImpl<PetAdoptMapper, PetAdopt> implements PetAdoptService {

}