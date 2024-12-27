/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.listener;

import com.diboot.core.event.ExceptionEvent;
import com.diboot.core.util.*;
import com.diboot.iam.entity.IamOperationLog;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamOperationLogService;
import com.diboot.iam.util.IamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 系统异常事件监听器
 */
@Component
@ConditionalOnProperty(name = "diboot.iam.save-exception-stack-trace", havingValue = "true")
public class ExceptionEventListener implements ApplicationListener<ExceptionEvent> {

    @Autowired
    private IamOperationLogService operationLogService;

    @Override
    public void onApplicationEvent(ExceptionEvent event) {
        IamUser user = null;
        try {
            user = IamSecurityUtils.getCurrentUser();
        }
        catch (Exception e){}
        IamOperationLog operationLog = new IamOperationLog();
        Map<String, Object> msgMap = event.getMsgMap();
        int code = (Integer)msgMap.get("code");
        String msg = (String)msgMap.get("msg");
        msgMap.remove("code");
        msgMap.remove("msg");
        BeanUtils.bindProperties(operationLog, msgMap);
        if(user != null) {
            operationLog.setUserType(user.getClass().getSimpleName())
                    .setUserId(user.getId()).setUserRealname(user.getRealname());
        }
        else {
            operationLog.setUserType("-").setUserId("-1").setUserRealname("-");
        }
        Exception exception = (Exception) event.getSource();
        operationLog
                .setBusinessObj("Exception").setOperation("异常")
                .setErrorMsg(extractStackTrace(exception))
                .setStatusCode(code);
        operationLogService.createEntity(operationLog);
        event.getMsgMap().put("code", code);
        event.getMsgMap().put("msg", msg);
    }

    /**
     * 提取异常堆栈信息
     * @param e
     * @return
     */
    private String extractStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder(e.getClass().getName()).append(": ").append(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\n\tat ").append(element.getClassName()).append(element.getMethodName()).append("(").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")");
        }
        Throwable ourCause = e.getCause();
        if (ourCause != null) {
            sb.append("\nCaused by: ").append(ourCause.getMessage());
        }
        return sb.toString();
    }

}
