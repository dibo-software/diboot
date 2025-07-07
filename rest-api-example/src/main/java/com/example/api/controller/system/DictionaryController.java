package com.example.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.Cons;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.OperationCons;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/dictionary")
@BindPermission(name = "数据字典")
@Slf4j
public class DictionaryController extends BaseCrudRestController<Dictionary> {

    @Autowired
    protected DictionaryService dictionaryService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?name=abc&pageSize=20&pageIndex=1&orderBy=name
     * </p>
     *
     * @return
     * @throws Exception
     */
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getViewObjectListMapping(Dictionary entity, Pagination pagination) throws Exception{
        QueryWrapper<Dictionary> queryWrapper = super.buildQueryWrapperByDTO(entity);
        queryWrapper.and(wrapper -> {
                    wrapper.isNull(Cons.ColumnName.parent_id.name()).or().eq(Cons.ColumnName.parent_id.name(), Cons.ID_PREVENT_NULL);
        });
        queryWrapper.orderByDesc(Cons.ColumnName.id.name());
        List<DictionaryVO> voList = dictionaryService.getViewObjectList(queryWrapper, pagination, DictionaryVO.class);
        return JsonResult.OK(voList).bindPagination(pagination);
    }

    /**
     * 根据资源id查询ViewObject
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") String id) throws Exception{
        return super.getViewObject(id, DictionaryVO.class);
    }

    /**
     * 创建资源对象
     *
     * @param entityVO
     * @return JsonResult
     * @throws Exception
     */
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult createEntityMapping(@RequestBody @Valid DictionaryVO entityVO) throws Exception {
        boolean success = dictionaryService.createDictAndChildren(entityVO);
        if(!success){
            return JsonResult.FAIL_OPERATION("保存数据字典失败！");
        }
        return JsonResult.OK(entityVO.getId());
    }

    /**
     * 根据ID更新资源对象
     *
     * @param entityVO
     * @return JsonResult
     * @throws Exception
     */
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id")String id, @Valid @RequestBody DictionaryVO entityVO) throws Exception {
        entityVO.setId(id);
        boolean success = dictionaryService.updateDictAndChildren(entityVO);
        if(!success){
            return JsonResult.FAIL_OPERATION("更新数据字典失败！");
        }
        return JsonResult.OK();
    }

    /**
     * 根据id删除资源对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") String id) throws Exception {
        boolean success = dictionaryService.deleteDictAndChildren(id);
        if (!success) {
            return JsonResult.FAIL_OPERATION("删除数据字典失败！");
        }
        return JsonResult.OK();
    }

    /**
     * 获取数据字典数据列表
     *
     * @param type
     * @return
     * @throws Exception
     */
    @GetMapping("/items/{type}")
    public JsonResult getItems(@PathVariable("type")String type) throws Exception{
        if (V.isEmpty(type)){
            return JsonResult.FAIL_INVALID_PARAM("type参数未指定");
        }
        List<LabelValue> itemsList = dictionaryService.getLabelValueList(type);
        return JsonResult.OK(itemsList);
    }

    /**
     * 获取字典定义列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/definition-list")
    public JsonResult getDictDefinitionList() throws Exception {
        LambdaQueryWrapper<Dictionary> queryWrapper = Wrappers.<Dictionary>lambdaQuery()
                .isNull(Dictionary::getParentId).or().eq(Dictionary::getParentId, Cons.ID_PREVENT_NULL)
                .orderByDesc(Dictionary::getId);
        List<Dictionary> dictionaryList = dictionaryService.getEntityList(queryWrapper);
        return JsonResult.OK(dictionaryList);
    }

    /**
     * 校验类型编码是否重复
     *
     * @param id
     * @param type
     * @return
     */
    @GetMapping("/check-type-duplicate")
    public JsonResult checkTypeDuplicate(@RequestParam(required = false) String id, @RequestParam String type) {
        if (V.notEmpty(type)) {
            LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper();
            wrapper.select(Dictionary::getId).eq(Dictionary::getType, type).isNull(Dictionary::getParentId);
            wrapper.ne(V.notEmpty(id), Dictionary::getId, id);
            boolean alreadyExists = dictionaryService.exists(wrapper);
            if (alreadyExists) {
                return JsonResult.FAIL_VALIDATION("exception.business.dictionary.duplicate-type");
            }
        }
        return JsonResult.OK();
    }
}