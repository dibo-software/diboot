package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Position;
import com.diboot.example.entity.Tree;
import com.diboot.example.vo.PositionVO;

import java.util.List;

/**
 * 职位相关Service
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/5
 */
public interface PositionService extends BaseService<Position> {

    //获取列表页数据
    List<PositionVO> getPositionList(Wrapper wrapper, Pagination pagination, Long orgId);

    //获取某公司下的所有职位
    List<Position> getPositionList(Long orgId);

    //新建
    boolean createPosition(PositionVO positionVO);

    //更新
    boolean updatePosition(PositionVO positionVO);

    //删除
    boolean deletePosition(Long id);

    //根据组织ID获取职位实体树
    List<PositionVO> getEntityTreeList(Long orgId);

    //根据组织ID获取职位树
    List<Tree> getViewTreeList(List<PositionVO> voList);
}
