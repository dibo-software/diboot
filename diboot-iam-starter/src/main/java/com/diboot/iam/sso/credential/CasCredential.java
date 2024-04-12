
package com.diboot.iam.sso.credential;

import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;

public class CasCredential extends AuthCredential {
    private static final long serialVersionUID = -5020652642432896556L;
    private String authAccount;

    public CasCredential() {
        this.setAuthType(Cons.DICTCODE_AUTH_TYPE.CAS_SERVER.name());
    }

    public String getAuthAccount() {
        return this.authAccount;
    }

    public String getAuthSecret() {
        return null;
    }

    public CasCredential setAuthAccount(final String authAccount) {
        this.authAccount = authAccount;
        return this;
    }
}
