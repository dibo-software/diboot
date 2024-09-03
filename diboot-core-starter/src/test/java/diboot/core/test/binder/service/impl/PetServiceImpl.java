package diboot.core.test.binder.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import diboot.core.test.binder.entity.Pet;
import diboot.core.test.binder.mapper.PetMapper;
import diboot.core.test.binder.service.PetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* 宠物 相关Service实现类
* @author MyName
* @version 1.0
* @date 2024-09-03
* Copyright © MyCorp
*/
@Slf4j
@Service
public class PetServiceImpl extends BaseServiceImpl<PetMapper, Pet> implements PetService {

}