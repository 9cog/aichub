package com.byteowls.capacitor.oauth2;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class OAuth2RefreshTokenOptions {
    private String accessTokenEndpoint;
    private String appId;
    private String refreshToken;
    private String scope;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String str) {
        this.appId = str;
    }

    public String getAccessTokenEndpoint() {
        return this.accessTokenEndpoint;
    }

    public void setAccessTokenEndpoint(String str) {
        this.accessTokenEndpoint = str;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String str) {
        this.refreshToken = str;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String str) {
        this.scope = str;
    }
}
