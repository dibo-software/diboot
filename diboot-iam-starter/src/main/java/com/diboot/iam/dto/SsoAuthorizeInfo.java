package com.diboot.iam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SsoAuthorizeInfo {
    private String url;
    private String state;

    public SsoAuthorizeInfo(String url, String state) {
        this.url = url;
        this.state = state;
    }

    public SsoAuthorizeInfo(String url) {
        this.url = url;
    }
}
