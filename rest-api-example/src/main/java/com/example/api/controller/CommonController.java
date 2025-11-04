package com.example.api.controller;

import com.diboot.core.controller.BaseController;
import com.diboot.core.dto.RelatedDataDTO;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.S;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.config.Cons;
import com.diboot.iam.util.IamSecurityUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用接口相关 Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {

    /**
     * 字典service
     */
    @Autowired
    protected DictionaryService dictionaryService;

    /**
     * 获取绑定字典数据
     * @param dictTypes
     * @return
     */
    @PostMapping("/load-related-dict")
    public JsonResult<Map<String, List<LabelValue>>> bindDict(@RequestBody List<String> dictTypes) {
        Map<String, List<LabelValue>> map = new HashMap<>(dictTypes.size());
        for (String dictType : dictTypes) {
            map.computeIfAbsent(S.toLowerCaseCamel(dictType) + "Options", k -> dictionaryService.getLabelValueList(dictType));
        }
        return JsonResult.OK(map);
    }

    /**
     * 获取绑定对象数据
     * @param map
     * @return
     */
    @PostMapping("/batch-load-related-data")
    public JsonResult<Map<String, List<LabelValue>>> bindData(@RequestBody @Valid Map<String, RelatedDataDTO> map) {
        Map<String, List<LabelValue>> resultMap = new HashMap<>(map.size());
        map.forEach((k, v) -> resultMap.put(k, loadRelatedData(v)));
        return JsonResult.OK(resultMap);
    }

    /**
     * 异步获取绑定对象数据，支持远程搜索
     * @param relatedDataDTO
     * @param keyword
     * @param parentId
     * @return
     */
    @PostMapping({"/load-related-data", "/load-related-data/{parentId}"})
    public JsonResult<List<LabelValue>> bindDataFilter(@Valid @RequestBody RelatedDataDTO relatedDataDTO,
                                                       @RequestParam(required = false) String keyword,
                                                       @PathVariable(required = false) String parentId) {
        return JsonResult.OK(loadRelatedData(relatedDataDTO, parentId, keyword));
    }

    /**
     * relatedData 自定义权限检查点
     *
     * @param relatedDataDTO
     * @return 返回true放行
     */
    @Override
    protected boolean relatedDataSecurityCheck(RelatedDataDTO relatedDataDTO) {
        // 超级管理员
        if (IamSecurityUtils.getSubject().hasRole(Cons.ROLE_SUPER_ADMIN)) {
            return true;
        }
        // 普通用户，是否检查选项数据的读权限，默认不限制
        /*
        try {
            IamSecurityUtils.getSubject().checkPermission(relatedDataDTO.getType() + ":read");
        } catch (Exception e) {
            log.warn("无权获取 relatedData: {}", relatedDataDTO.getType());
            return false;
        }*/
        return true;
    }

}
