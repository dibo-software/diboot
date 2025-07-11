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
package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.entity.IamUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* 登录记录 VO定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Getter
@Setter
@Accessors(chain = true)
public class IamLoginTraceVO extends IamLoginTrace  {
    private static final long serialVersionUID = -753084580143028183L;

    private static final String USER_AGENT_BROWSER_REGEX = "((?:MSIE |Trident/.*?rv:|Edge/|Edg/|Firefox/|Chrome/|Safari/|Opera/|OPR/|YaBrowser/)([\\d\\.]+))";
    private static final String USER_AGENT_OS_REGEX = "\\(([^;]*?);";

    public enum ONLINE_STATUS {
        ONLINE,
        LOGOUT,
        UNKNOWN,
        INVALID,
    }

    /**
     * 认证方式
     */
    @BindDict(type="AUTH_TYPE", field = "authType")
    private LabelValue authTypeLabel;

    // 在线状态
    private String onlineStatus;

    /**
     * 用户姓名
     */
    @BindField(entity = IamUser.class, field = "realname", condition = "this.user_type='IamUser' AND this.user_id=id")
    private String userIdLabel;

    /**
     * 获取浏览器信息
     * @return
     */
    public String getBrowserInfo() {
        String userAgent = getUserAgent();
        if (V.isEmpty(userAgent)) {
            return "";
        }
        Pattern pattern = Pattern.compile(USER_AGENT_BROWSER_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userAgent);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "Unknown";
    }

    /**
     * 获取操作系统信息
     * @return
     */
    public String getOsInfo() {
        String userAgent = getUserAgent();
        if (V.isEmpty(userAgent)) {
            return "";
        }
        Pattern pattern = Pattern.compile(USER_AGENT_OS_REGEX);
        Matcher matcher = pattern.matcher(userAgent);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "Unknown";
    }

    public boolean isExpired(int expiresInMinutes) {
        LocalDateTime expirationTime = getCreateTime().plusMinutes(expiresInMinutes);
        LocalDateTime now = LocalDateTime.now();
        return expirationTime.isBefore(now);
    }

}