package com.example.api.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.cache.BaseCacheManager;
import com.diboot.core.controller.BaseController;
import com.diboot.core.entity.AbstractEntity;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.ClientCredential;
import com.diboot.iam.dto.PwdCredential;
import com.diboot.iam.entity.*;
import com.diboot.iam.entity.route.RouteRecord;
import com.diboot.iam.service.*;
import com.diboot.iam.shiro.IamAuthorizingRealm;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.util.TokenUtils;
import com.diboot.iam.vo.IamUserOrgVO;
import com.diboot.iam.vo.PositionDataScope;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IAM身份认证/申请Token接口
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@BindPermission(name = "登录认证", code = "AUTH")
public class AuthTokenController extends BaseController {
    @Autowired
    private IamUserRoleService iamUserRoleService;
    @Autowired
    private IamUserPositionService iamUserPositionService;
    @Autowired
    private IamUserService iamUserService;
    @Autowired
    private IamRoleResourceService iamRoleResourceService;
    @Autowired
    private IamResourceService iamResourceService;
    @Resource
    private IamAuthorizingRealm iamAuthorizingRealm;

    @Autowired
    @Qualifier("iamCacheManager")
    private BaseCacheManager baseCacheManager;

    @Value("${diboot.login-encrypt.rsa-private-key}")
    private String rsaPrivateKey;

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public void captcha(@RequestParam("traceId") String traceId, HttpServletResponse response) throws Exception {
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 算数验证码
        Captcha captcha = new ArithmeticCaptcha();
        // 验证码存入缓存
        baseCacheManager.putCacheObj(Cons.CACHE_CAPTCHA, traceId, captcha.text());
        // 输出图片流
        captcha.out(response.getOutputStream());
    }

    /**
     * 用户登录获取token
     *
     * @param credential 登录凭证
     * @return 响应（troken）
     */
    @PostMapping("/login")
    public JsonResult<String> login(@RequestBody PwdCredential /* TenantPwdCredential */ credential) {
        // 获取缓存中的验证码
        String traceId = credential.getTraceId();
        String verCode = credential.getCaptcha();
        String captcha = baseCacheManager.getCacheString(Cons.CACHE_CAPTCHA, traceId);
        baseCacheManager.removeCacheObj(Cons.CACHE_CAPTCHA, traceId);
        // 判断验证码
        if (verCode == null || !verCode.trim().toLowerCase().equals(captcha)) {
            return JsonResult.FAIL_VALIDATION("验证码错误");
        }
        credential.setPassword(decrypt(credential.getPassword()));
        return JsonResult.OK(AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.PWD.name()).applyToken(credential));
    }

    /**
     * RSA 解密
     *
     * @param content
     * @return
     */
    private String decrypt(String content) {
        try {
            byte[] decode = Base64.getDecoder().decode(content);
            // base64编码的私钥
            byte[] decoded = Base64.getDecoder().decode(rsaPrivateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decoded));
            // RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(decode), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("解密数据失败: {} : {}", content, e.getMessage());
            throw new BusinessException(Status.FAIL_OPERATION, "解密数据失败！");
        }
    }

    /**
     * 注销/退出
     *
     * @return
     */
    @Log(businessObj = "LoginUser", operation = "退出")
    @PostMapping("/logout")
    public JsonResult<?> logout() {
        String accessToken = TokenUtils.getRequestToken(request);
        IamSecurityUtils.logoutByToken(accessToken);
        return JsonResult.OK();
    }

    /**
     * 获取用户角色权限信息
     *
     * @return 响应（用户信息）
     */
    @GetMapping("/user-info")
    public JsonResult<Map<String, Object>> getUserInfo(@RequestParam(value = "refresh", required = false) boolean refresh,
                                                       @RequestParam(value = "module", required = false) String module) {
        Map<String, Object> data = new HashMap<>();
        // 获取当前登录用户对象
        BaseLoginUser currentUser = IamSecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return JsonResult.OK();
        }
        if (refresh && currentUser instanceof IamUser) {
            iamUserService.refreshUserInfo((IamUser) currentUser);
        }
        data.put("info", Binder.convertAndBindRelations(currentUser, IamUserOrgVO.class));
        // 角色权限数据
        List<IamRole> roles = iamUserRoleService.getUserRoleList(IamUser.class.getSimpleName(), currentUser.getId());
        data.put("roles", roles);
        data.put("positions", currentUser.getPositions());
        data.put("curPosition", currentUser.getExtensionObj());

        // 移动端权限列表
        if ("mobile".equals(module)) {
            LambdaQueryWrapper<IamResource> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(IamResource::getStatus, Cons.DICTCODE_RESOURCE_STATUS.A.name());
            queryWrapper.eq(IamResource::getAppModule, module);
            if (IamSecurityUtils.isSuperAdmin()) {
                data.put("permissions", iamResourceService.getValuesOfField(queryWrapper, IamResource::getResourceCode));
            } else if (roles != null && !roles.isEmpty()) {
                List<String> roleIds = roles.stream().map(AbstractEntity::getId).toList();
                List<String> resourceIds = iamRoleResourceService.getValuesOfField(Wrappers.<IamRoleResource>lambdaQuery().in(IamRoleResource::getRoleId, roleIds), IamRoleResource::getResourceId);
                if (V.notEmpty(resourceIds)) {
                    queryWrapper.in(IamResource::getId, resourceIds);
                    data.put("permissions", iamResourceService.getValuesOfField(queryWrapper, IamResource::getResourceCode));
                }
            }
        }
        return JsonResult.OK(data);
    }

    /**
     * 客户端登录获取token
     *
     * @param credential 登录凭证
     * @return 响应（troken）
     */
    @PostMapping("/client-login")
    public JsonResult<String> login(@RequestBody ClientCredential credential) {
        return JsonResult.OK(AuthServiceFactory.getAuthService(ClientCredential.AUTH_TYPE).applyToken(credential));
    }

    /**
     * 获取前端路由
     *
     * @return
     */
    @GetMapping("/route")
    public JsonResult<List<RouteRecord>> getRouteRecord() {
        List<RouteRecord> routeRecords = iamRoleResourceService.getRouteRecords();
        return JsonResult.OK(routeRecords);
    }

    /**
     * 变更当前岗位为制定岗位
     * @param position
     * @return
     */
    @PostMapping("/switch-position")
    public JsonResult<String> changePosition(@RequestBody LabelValue position, HttpServletRequest request, HttpServletResponse response) {
        IamUser currentUser = IamSecurityUtils.getCurrentUser();
        // 获取当前岗
        LabelValue extensionObj = currentUser.getExtensionObj();
        // 替换为前端岗 ：extensionObj中children 字段存放的是当前用户所有岗位信息，所以不用跟随切换
        extensionObj.setLabel(position.getLabel()).setValue(position.getValue());
        // 扩展信息 前端提交过来的是 linkmap，需要对其进行重新转换为PositionDataScope实体
        if(V.notEmpty(position.getExt())) {
            String positionDataScopeStr = JSON.stringify(position.getExt());
            PositionDataScope positionDataScope = JSON.parseObject(positionDataScopeStr, PositionDataScope.class);
            extensionObj.setExt(positionDataScope);
        }
        currentUser.setExtensionObj(extensionObj);
        String requestToken = TokenUtils.getRequestToken(request);
        String responseToken = TokenUtils.getResponseToken(response);
        String token = V.notEmpty(responseToken) ? responseToken : requestToken;
        iamAuthorizingRealm.refreshAuthenticationCache(token, currentUser);
        return JsonResult.OK();
    }

    /**
     * 心跳接口
     *
     * @return
     */
    @GetMapping("/ping")
    public JsonResult<?> ping() {
        return JsonResult.OK();
    }
}
