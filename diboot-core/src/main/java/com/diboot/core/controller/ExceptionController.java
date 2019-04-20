package com.diboot.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常处理类
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
public class ExceptionController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);

	/***
	 * 默认异常处理
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public void handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		// 记录日志
		log.error("发生异常:", ex);
		
		String requestUrl = (String) request.getAttribute("javax.servlet.error.request_uri");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		Object exception = request.getAttribute("javax.servlet.error.exception");
		
		StringBuilder sb = new StringBuilder();
		sb.append("request_uri: [").append(requestUrl).append("] Error occured : ").append("status_code=").append(statusCode)
		.append(";message=").append(request.getAttribute("javax.servlet.error.message")).append(";exception=").append(exception);
		log.warn(sb.toString());
	}
}