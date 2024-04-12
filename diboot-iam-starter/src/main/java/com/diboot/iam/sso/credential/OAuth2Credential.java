
package com.diboot.iam.sso.credential;

import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;

public class OAuth2Credential extends AuthCredential {
    private static final long serialVersionUID = -5020652642432896557L;
    private String authAccount;

    public OAuth2Credential() {
        this.setAuthType(Cons.DICTCODE_AUTH_TYPE.OAuth2.name());
    }

    public String getAuthAccount() {
        return this.authAccount;
    }

    public String getAuthSecret() {
        return null;
    }

    public OAuth2Credential setAuthAccount(final String authAccount) {
        this.authAccount = authAccount;
        return this;
    }
}
