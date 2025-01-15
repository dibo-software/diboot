package com.example.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.entity.SystemConfig;
import com.diboot.iam.service.SystemConfigService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@BindPermission(name = "系统配置")
@RestController
@RequestMapping("/system-config")
public class SystemConfigController extends BaseCrudRestController<SystemConfig> {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取系统配置类型列表
     *
     * @return
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping("/category")
    public JsonResult<List<String>> getCategoryListMapping() {
        return JsonResult.OK(systemConfigService.getValuesOfField(Wrappers.lambdaQuery(), SystemConfig::getCategory));
    }

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult<List<SystemConfig>> getViewObjectListMapping(SystemConfig entity) throws Exception {
        QueryWrapper<SystemConfig> queryWrapper = super.buildQueryWrapperByDTO(entity);
        if (V.isEmpty(entity.getCategory())) {
            queryWrapper.lambda().and(query -> query.eq(SystemConfig::getCategory, S.EMPTY).or().isNull(SystemConfig::getCategory));
        }
        return JsonResult.OK(systemConfigService.getEntityList(queryWrapper, null));
    }

    /**
     * 获取指定类型的系统配置信息
     *
     * @param id 类型
     * @return
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult<SystemConfig> getConfigByTypeMapping(@PathVariable String id) {
        return JsonResult.OK(systemConfigService.getEntity(id));
    }

    /**
     * 创建资源对象
     *
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult<?> createEntityMapping(@Valid @RequestBody SystemConfig entity) throws Exception {
        return super.createEntity(entity);
    }

    /**
     * 根据ID更新资源对象
     *
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult<?> updateEntityMapping(@PathVariable("id") String id, @Valid @RequestBody SystemConfig entity) throws Exception {
        return super.updateEntity(id, entity);
    }

    /**
     * 批量更新接口
     *
     * @param list
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping
    public JsonResult<?> updateEntityListMapping(@Valid @RequestBody List<SystemConfig> list) throws Exception {
        return new JsonResult<>(systemConfigService.updateEntities(list));
    }

    /**
     * 根据id删除资源对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult<?> deleteEntityMapping(@PathVariable("id") String id) throws Exception {
        return super.deleteEntity(id);
    }

    /**
     * 获取配置值 用于前端获取配置值
     *
     * @param category 配置类型
     * @param propKey 属性（为空重置整个类型）
     * @return 配置值
     */
    @Log(operation = "获取配置值")
    @GetMapping("/value")
    public JsonResult<?> getConfigMapping(@RequestParam(required = false) String category, @RequestParam(required = false) String propKey) {
        if (S.isEmpty(propKey)) {
            return JsonResult.OK(systemConfigService.getConfigMapByCategory(category));
        }
        return JsonResult.OK(systemConfigService.findConfigValue(category, propKey));
    }

    /**
     * 验证属性名是否重复
     *
     * @param id
     * @param propKey
     * @param category
     * @return
     */
    @GetMapping("/check-prop-key-duplicate")
    public JsonResult<?> checkPropKeyDuplicate(@RequestParam(required = false) String id, @RequestParam String propKey,
                                               @RequestParam(required = false) String category) {

        if (V.isEmpty(propKey)) {
            return JsonResult.OK();
        }
        LambdaQueryWrapper<SystemConfig> wrapper = Wrappers.<SystemConfig>lambdaQuery().eq(SystemConfig::getPropKey, propKey);
        // 如果id存在，那么需要排除当前id进行查询
        wrapper.ne(V.notEmpty(id), SystemConfig::getId, id);
        if (V.isEmpty(category)) {
            wrapper.and(query -> query.eq(SystemConfig::getCategory, S.EMPTY).or().isNull(SystemConfig::getCategory));
        } else {
            wrapper.eq(SystemConfig::getCategory, category);
        }
        if (systemConfigService.exists(wrapper)) {
            return JsonResult.FAIL_VALIDATION((V.isEmpty(category) ? "" : "类别[" + category + "]中") + "属性名[" + propKey + "]已存在");
        }
        return JsonResult.OK();
    }

}
