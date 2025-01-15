package com.example.api.controller.iam;

import com.diboot.core.controller.BaseController;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.dto.SsoAuthorizeInfo;
import com.diboot.iam.sso.SSOManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IAM身份认证/申请Token接口
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@Slf4j
@RestController
@RequestMapping("/auth/sso")
@BindPermission(name = "登录认证", code = "AUTH")
public class SSOClientController extends BaseController {

    /**
     * 加载所有单点登录授权地址对象
     * @return
     */
    @GetMapping("/load-authorize-info-map")
    public JsonResult loadAuthorizeInfoMap() {
        Map<String, SsoAuthorizeInfo> urlMap = new HashMap<>();
        List<SSOManager> managers = ContextHolder.getBeans(SSOManager.class);
        if (V.isEmpty(managers)) {
            return JsonResult.OK(urlMap);
        }
        for (SSOManager manager : managers) {
            urlMap.put(manager.getAuthType(), manager.getAuthorizeInfo(""));
        }
        return JsonResult.OK(urlMap);
    }

    /**
     * 检查对应的单点登录凭证
     * @param map
     * @return
     */
    @PostMapping("/auth-check")
    public JsonResult authCheck(@RequestBody Map map) {
        String authType = String.valueOf(map.get("authType"));
        if (V.isEmpty(authType)) {
            return JsonResult.FAIL_OPERATION("认证失败");
        }
        List<SSOManager> managers = ContextHolder.getBeans(SSOManager.class);
        Map<String, SSOManager> managerMap = BeanUtils.convertToStringKeyObjectMap(managers, SSOManager::getAuthType);
        SSOManager manager = managerMap.get(authType);
        if (manager == null) {
            return JsonResult.FAIL_OPERATION("认证失败");
        }
        String token = manager.getToken(map);
        if (V.isEmpty(token)) {
            return JsonResult.FAIL_OPERATION("认证失败");
        }
        return JsonResult.OK(token).msg("认证成功");
    }

}
