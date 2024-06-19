
/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.ai.models.wenxin;

import com.diboot.ai.models.wenxin.WenXinConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * token 工具
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/7
 */
@Slf4j
public class WenXinToken {
    private final static Map<String, TokenWrapper> TOKEN_CACHE = new ConcurrentHashMap<>(8);

    // token key
    public final static String TOKEN_KEY = "access_token";

    // 默认过期时间为1天  （千帆模型 过期时间为30天）
    private static final int TOKEN_EXPIRE_MILLIS = 7 * 24 * 60 * 60 * 1000;

    // token请求url
    private final static String TOKEN_API = "https://aip.baidubce.com/oauth/2.0/token";

    /**
     * 获取token
     * @param httpClient
     * @param wenXinConfig
     * @return
     * @throws IOException
     */
    public static String getAccessToken(OkHttpClient httpClient, WenXinConfig wenXinConfig) {
        String token = get(TOKEN_KEY);
        if (V.notEmpty(token)) {
            return token;
        }
        try {
            Request request = new Request.Builder()
                    .url(TOKEN_API)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .post(RequestBody.Companion.create("grant_type=client_credentials&client_id=" + wenXinConfig.getApiKey() + "&client_secret=" + wenXinConfig.getSecretKey()
                            , okhttp3.MediaType.parse(MediaType.APPLICATION_FORM_URLENCODED_VALUE)))
                    .build();
            Response response = httpClient.newCall(request).execute();
            String responseBody = response.body().string();
            Map<String, Object> result = JSON.parseObject(responseBody);
            // 响应失败时返回该字段，成功时不返回
            if (V.notEmpty(result.get("error"))) {
                log.debug("请求token失败：{}", responseBody);
                throw new BusinessException(Status.FAIL_OPERATION, "exception.business.modelProvider.fetchTokenFailed");
            }
            token = String.valueOf(result.get(TOKEN_KEY));
            TOKEN_CACHE.put(TOKEN_KEY, new TokenWrapper(token, System.currentTimeMillis() + TOKEN_EXPIRE_MILLIS));
            return token;
        } catch (IOException e) {
            log.debug("请求token异常 ：{}", e);
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.modelProvider.fetchTokenFailed");
        }
    }

    /**
     * 根据key获取token
     *
     * @param key
     * @return
     */
    private static String get(String key) {
        TokenWrapper tokenWrapper = TOKEN_CACHE.get(key);
        if (V.isEmpty(tokenWrapper)) {
            return null;
        }
        // 过期删除缓存
        if (tokenWrapper.isExpired()) {
            return null;
        }
        return tokenWrapper.getToken();
    }


    @AllArgsConstructor
    @Getter
    @Setter
    private static class TokenWrapper {
        private String token;

        private long expiredTime;

        public boolean isExpired() {
            // 如果过期时间为0，表示永远 不回过期
            if (V.equals(expiredTime, 0)) {
                return false;
            }
            return System.currentTimeMillis() > expiredTime;
        }
    }
}
