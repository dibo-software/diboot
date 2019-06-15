package com.diboot.shiro.jwt;

import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.shiro.util.JwtHelper;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT 认证过滤器
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public class BaseJwtAuthenticationFilter extends BasicHttpAuthenticationFilter {
    private static final Logger logger =  LoggerFactory.getLogger(BaseJwtAuthenticationFilter.class);

    /**
     * Shiro权限拦截核心方法 返回true允许访问，这里使用JWT进行认证
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 获取Token
        String accessToken = JwtHelper.getRequestToken(httpRequest);
        if (V.isEmpty(accessToken)) {
            logger.warn("Token为空！url="+httpRequest.getRequestURL());
            return false;
        }
        //获取username
        String account = JwtHelper.getAccountFromToken(accessToken);
        if(V.notEmpty(account)){
            logger.debug("Token认证成功！account="+account);
            return true;
        }
        logger.debug("Token认证失败！");
        return false;
    }

    /**
     * 当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理
     * @param
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        logger.debug("Token认证： onAccessDenied");
        JsonResult jsonResult = new JsonResult(Status.FAIL_INVALID_TOKEN);
        this.responseJson((HttpServletResponse) response, jsonResult);
        return false;
    }

    /***
     * 返回json格式错误信息
     * @param response
     * @param jsonResult
     */
    protected void responseJson(HttpServletResponse response, JsonResult jsonResult){
        // 处理异步请求
        PrintWriter pw = null;
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            pw = response.getWriter();
            pw.write(JSON.stringify(jsonResult));
            pw.flush();
        }
        catch (IOException e) {
            logger.error("处理异步请求异常", e);
        }
        finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

}
