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

    /***
     * 获取列表页数据
     * @param wrapper
     * @param pagination
     * @param orgId
     * @return
     */
    List<PositionVO> getPositionList(Wrapper wrapper, Pagination pagination, Long orgId);

    /***
     * 获取某公司下的所有职位
     * @param orgId
     * @return
     */
    List<Position> getPositionList(Long orgId);

    /***
     * 新建
     * @param positionVO
     * @return
     */
    boolean createPosition(PositionVO positionVO);

    /***
     * 更新
     * @param positionVO
     * @return
     */
    boolean updatePosition(PositionVO positionVO);

    /***
     * 删除
     * @param id
     * @return
     */
    boolean deletePosition(Long id);

    /***
     * 根据组织ID获取职位实体树结构
     * @param orgId
     * @param deptId
     * @return
     */
    List<PositionVO> getEntityTreeList(Long orgId, Long deptId);

    /***
     * 获取职位树结构
     * @param voList
     * @return
     */
    List<Tree> getViewTreeList(List<PositionVO> voList);
}
