package com.diboot.example.service;

import com.diboot.core.service.BaseService;
import com.diboot.example.entity.Position;
import com.diboot.example.vo.PositionVO;

/**
 * 职位相关Service
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/5
 */
public interface PositionService extends BaseService<Position> {

    boolean createPosition(PositionVO positionVO);

    boolean updatePosition(PositionVO positionVO);

    boolean deletePosition(Long id);
}
