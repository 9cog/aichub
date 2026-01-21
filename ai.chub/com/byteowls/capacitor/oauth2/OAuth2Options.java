package com.byteowls.capacitor.oauth2;

import java.util.HashMap;
import java.util.Map;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class OAuth2Options {
    private String accessTokenEndpoint;
    private Map<String, String> additionalParameters;
    private Map<String, String> additionalResourceHeaders;
    private String appId;
    private String authorizationBaseUrl;
    private String customHandlerClass;
    private String display;
    private boolean handleResultOnActivityResult = true;
    private boolean handleResultOnNewIntent;
    private String loginHint;
    private String logoutUrl;
    private boolean logsEnabled;
    private String pkceCodeVerifier;
    private boolean pkceEnabled;
    private String prompt;
    private String redirectUrl;
    private String resourceUrl;
    private String responseMode;
    private String responseType;
    private String scope;
    private String state;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String str) {
        this.appId = str;
    }

    public String getAuthorizationBaseUrl() {
        return this.authorizationBaseUrl;
    }

    public void setAuthorizationBaseUrl(String str) {
        this.authorizationBaseUrl = str;
    }

    public String getAccessTokenEndpoint() {
        return this.accessTokenEndpoint;
    }

    public void setAccessTokenEndpoint(String str) {
        this.accessTokenEndpoint = str;
    }

    public String getResourceUrl() {
        return this.resourceUrl;
    }

    public void setResourceUrl(String str) {
        this.resourceUrl = str;
    }

    public boolean isLogsEnabled() {
        return this.logsEnabled;
    }

    public void setLogsEnabled(boolean z) {
        this.logsEnabled = z;
    }

    public String getResponseType() {
        return this.responseType;
    }

    public void setResponseType(String str) {
        this.responseType = str;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String str) {
        this.scope = str;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String str) {
        this.state = str;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    public void setRedirectUrl(String str) {
        this.redirectUrl = str;
    }

    public String getCustomHandlerClass() {
        return this.customHandlerClass;
    }

    public void setCustomHandlerClass(String str) {
        this.customHandlerClass = str;
    }

    public boolean isPkceEnabled() {
        return this.pkceEnabled;
    }

    public void setPkceEnabled(boolean z) {
        this.pkceEnabled = z;
    }

    public String getPkceCodeVerifier() {
        return this.pkceCodeVerifier;
    }

    public void setPkceCodeVerifier(String str) {
        this.pkceCodeVerifier = str;
    }

    public Map<String, String> getAdditionalParameters() {
        return this.additionalParameters;
    }

    public void setAdditionalParameters(Map<String, String> map) {
        this.additionalParameters = map;
    }

    public void addAdditionalParameter(String str, String str2) {
        if (str == null || str2 == null) {
            return;
        }
        if (this.additionalParameters == null) {
            this.additionalParameters = new HashMap();
        }
        this.additionalParameters.put(str, str2);
    }

    public String getDisplay() {
        return this.display;
    }

    public void setDisplay(String str) {
        this.display = str;
    }

    public String getLoginHint() {
        return this.loginHint;
    }

    public void setLoginHint(String str) {
        this.loginHint = str;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public void setPrompt(String str) {
        this.prompt = str;
    }

    public String getResponseMode() {
        return this.responseMode;
    }

    public void setResponseMode(String str) {
        this.responseMode = str;
    }

    public boolean isHandleResultOnNewIntent() {
        return this.handleResultOnNewIntent;
    }

    public void setHandleResultOnNewIntent(boolean z) {
        this.handleResultOnNewIntent = z;
    }

    public boolean isHandleResultOnActivityResult() {
        return this.handleResultOnActivityResult;
    }

    public void setHandleResultOnActivityResult(boolean z) {
        this.handleResultOnActivityResult = z;
    }

    public Map<String, String> getAdditionalResourceHeaders() {
        return this.additionalResourceHeaders;
    }

    public void setAdditionalResourceHeaders(Map<String, String> map) {
        this.additionalResourceHeaders = map;
    }

    public void addAdditionalResourceHeader(String str, String str2) {
        if (str == null || str2 == null) {
            return;
        }
        if (this.additionalResourceHeaders == null) {
            this.additionalResourceHeaders = new HashMap();
        }
        this.additionalResourceHeaders.put(str, str2);
    }

    public String getLogoutUrl() {
        return this.logoutUrl;
    }
}
