package com.byteowls.capacitor.oauth2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import com.byteowls.capacitor.oauth2.handler.AccessTokenCallback;
import com.byteowls.capacitor.oauth2.handler.OAuth2CustomHandler;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.Map;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.EndSessionRequest;
import net.openid.appauth.EndSessionResponse;
import net.openid.appauth.GrantTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;
import okhttp3.HttpUrl;
import org.json.JSONException;
@CapacitorPlugin(name = "OAuth2Client")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class OAuth2ClientPlugin extends Plugin {
    private static final String ERR_ANDROID_NO_BROWSER = "ERR_ANDROID_NO_BROWSER";
    private static final String ERR_ANDROID_RESULT_NULL = "ERR_ANDROID_NO_INTENT";
    private static final String ERR_AUTHORIZATION_FAILED = "ERR_AUTHORIZATION_FAILED";
    private static final String ERR_CUSTOM_HANDLER_LOGIN = "ERR_CUSTOM_HANDLER_LOGIN";
    private static final String ERR_CUSTOM_HANDLER_LOGOUT = "ERR_CUSTOM_HANDLER_LOGOUT";
    private static final String ERR_GENERAL = "ERR_GENERAL";
    private static final String ERR_NO_ACCESS_TOKEN = "ERR_NO_ACCESS_TOKEN";
    private static final String ERR_NO_AUTHORIZATION_CODE = "ERR_NO_AUTHORIZATION_CODE";
    private static final String ERR_PARAM_NO_ACCESS_TOKEN_ENDPOINT = "ERR_PARAM_NO_ACCESS_TOKEN_ENDPOINT";
    private static final String ERR_PARAM_NO_APP_ID = "ERR_PARAM_NO_APP_ID";
    private static final String ERR_PARAM_NO_AUTHORIZATION_BASE_URL = "ERR_PARAM_NO_AUTHORIZATION_BASE_URL";
    private static final String ERR_PARAM_NO_REDIRECT_URL = "ERR_PARAM_NO_REDIRECT_URL";
    private static final String ERR_PARAM_NO_REFRESH_TOKEN = "ERR_PARAM_NO_REFRESH_TOKEN";
    private static final String ERR_PARAM_NO_RESPONSE_TYPE = "ERR_PARAM_NO_RESPONSE_TYPE";
    private static final String ERR_STATES_NOT_MATCH = "ERR_STATES_NOT_MATCH";
    private static final String PARAM_ACCESS_TOKEN_ENDPOINT = "accessTokenEndpoint";
    private static final String PARAM_ADDITIONAL_PARAMETERS = "additionalParameters";
    private static final String PARAM_ADDITIONAL_RESOURCE_HEADERS = "additionalResourceHeaders";
    private static final String PARAM_ANDROID_CUSTOM_HANDLER_CLASS = "android.customHandlerClass";
    private static final String PARAM_ANDROID_HANDLE_RESULT_ON_ACTIVITY_RESULT = "android.handleResultOnActivityResult";
    private static final String PARAM_ANDROID_HANDLE_RESULT_ON_NEW_INTENT = "android.handleResultOnNewIntent";
    private static final String PARAM_APP_ID = "appId";
    private static final String PARAM_AUTHORIZATION_BASE_URL = "authorizationBaseUrl";
    private static final String PARAM_DISPLAY = "display";
    private static final String PARAM_ID_TOKEN = "id_token";
    private static final String PARAM_LOGIN_HINT = "login_hint";
    private static final String PARAM_LOGOUT_URL = "logoutUrl";
    private static final String PARAM_LOGS_ENABLED = "logsEnabled";
    private static final String PARAM_PKCE_ENABLED = "pkceEnabled";
    private static final String PARAM_PROMPT = "prompt";
    private static final String PARAM_REDIRECT_URL = "redirectUrl";
    private static final String PARAM_REFRESH_TOKEN = "refreshToken";
    private static final String PARAM_RESOURCE_URL = "resourceUrl";
    private static final String PARAM_RESPONSE_MODE = "response_mode";
    private static final String PARAM_RESPONSE_TYPE = "responseType";
    private static final String PARAM_SCOPE = "scope";
    private static final String PARAM_STATE = "state";
    private static final String USER_CANCELLED = "USER_CANCELLED";
    private AuthorizationService authService;
    private AuthState authState;
    private String callbackId;
    private OAuth2Options oauth2Options;

    @PluginMethod
    public void refreshToken(final PluginCall pluginCall) {
        disposeAuthService();
        OAuth2RefreshTokenOptions buildRefreshTokenOptions = buildRefreshTokenOptions(pluginCall.getData());
        if (buildRefreshTokenOptions.getAppId() == null) {
            pluginCall.reject(ERR_PARAM_NO_APP_ID);
        } else if (buildRefreshTokenOptions.getAccessTokenEndpoint() == null) {
            pluginCall.reject(ERR_PARAM_NO_ACCESS_TOKEN_ENDPOINT);
        } else if (buildRefreshTokenOptions.getRefreshToken() == null) {
            pluginCall.reject(ERR_PARAM_NO_REFRESH_TOKEN);
        } else {
            this.authService = new AuthorizationService(getContext());
            AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(Uri.parse(HttpUrl.FRAGMENT_ENCODE_SET), Uri.parse(buildRefreshTokenOptions.getAccessTokenEndpoint()));
            if (this.authState == null) {
                this.authState = new AuthState(authorizationServiceConfiguration);
            }
            this.authService.performTokenRequest(new TokenRequest.Builder(authorizationServiceConfiguration, buildRefreshTokenOptions.getAppId()).setGrantType(GrantTypeValues.REFRESH_TOKEN).setScope(buildRefreshTokenOptions.getScope()).setRefreshToken(buildRefreshTokenOptions.getRefreshToken()).build(), new AuthorizationService.TokenResponseCallback() { // from class: com.byteowls.capacitor.oauth2.OAuth2ClientPlugin$$ExternalSyntheticLambda2
                @Override // net.openid.appauth.AuthorizationService.TokenResponseCallback
                public final void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException authorizationException) {
                    OAuth2ClientPlugin.this.lambda$refreshToken$0(pluginCall, tokenResponse, authorizationException);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshToken$0(PluginCall pluginCall, TokenResponse tokenResponse, AuthorizationException authorizationException) {
        this.authState.update(tokenResponse, authorizationException);
        String str = ERR_GENERAL;
        if (authorizationException != null) {
            if (authorizationException.error != null) {
                str = authorizationException.error;
            }
            pluginCall.reject(str, String.valueOf(authorizationException.code), authorizationException);
        } else if (tokenResponse != null) {
            try {
                pluginCall.resolve(new JSObject(tokenResponse.jsonSerializeString()));
            } catch (JSONException e) {
                pluginCall.reject(ERR_GENERAL, e);
            }
        } else {
            pluginCall.reject(ERR_NO_ACCESS_TOKEN);
        }
    }

    @PluginMethod
    public void authenticate(final PluginCall pluginCall) {
        this.callbackId = pluginCall.getCallbackId();
        disposeAuthService();
        OAuth2Options buildAuthenticateOptions = buildAuthenticateOptions(pluginCall.getData());
        this.oauth2Options = buildAuthenticateOptions;
        if (buildAuthenticateOptions.getCustomHandlerClass() != null) {
            if (this.oauth2Options.isLogsEnabled()) {
                String logTag = getLogTag();
                Log.i(logTag, "Entering custom handler: " + this.oauth2Options.getCustomHandlerClass().getClass().getName());
            }
            try {
                ((OAuth2CustomHandler) Class.forName(this.oauth2Options.getCustomHandlerClass()).newInstance()).getAccessToken(getActivity(), pluginCall, new AccessTokenCallback() { // from class: com.byteowls.capacitor.oauth2.OAuth2ClientPlugin.1
                    @Override // com.byteowls.capacitor.oauth2.handler.AccessTokenCallback
                    public void onSuccess(String str) {
                        new ResourceUrlAsyncTask(pluginCall, OAuth2ClientPlugin.this.oauth2Options, OAuth2ClientPlugin.this.getLogTag(), null, null).execute(str);
                    }

                    @Override // com.byteowls.capacitor.oauth2.handler.AccessTokenCallback
                    public void onCancel() {
                        pluginCall.reject(OAuth2ClientPlugin.USER_CANCELLED);
                    }

                    @Override // com.byteowls.capacitor.oauth2.handler.AccessTokenCallback
                    public void onError(Exception exc) {
                        pluginCall.reject(OAuth2ClientPlugin.ERR_CUSTOM_HANDLER_LOGIN, exc);
                    }
                });
            } catch (ClassNotFoundException e) {
                e = e;
                pluginCall.reject(ERR_CUSTOM_HANDLER_LOGIN, e);
            } catch (IllegalAccessException e2) {
                e = e2;
                pluginCall.reject(ERR_CUSTOM_HANDLER_LOGIN, e);
            } catch (InstantiationException e3) {
                e = e3;
                pluginCall.reject(ERR_CUSTOM_HANDLER_LOGIN, e);
            } catch (Exception e4) {
                pluginCall.reject(ERR_GENERAL, e4);
            }
        } else if (this.oauth2Options.getAppId() == null) {
            pluginCall.reject(ERR_PARAM_NO_APP_ID);
        } else if (this.oauth2Options.getAuthorizationBaseUrl() == null) {
            pluginCall.reject(ERR_PARAM_NO_AUTHORIZATION_BASE_URL);
        } else if (this.oauth2Options.getResponseType() == null) {
            pluginCall.reject(ERR_PARAM_NO_RESPONSE_TYPE);
        } else if (this.oauth2Options.getRedirectUrl() == null) {
            pluginCall.reject(ERR_PARAM_NO_REDIRECT_URL);
        } else {
            Uri parse = Uri.parse(this.oauth2Options.getAuthorizationBaseUrl());
            AuthorizationServiceConfiguration authorizationServiceConfiguration = new AuthorizationServiceConfiguration(parse, this.oauth2Options.getAccessTokenEndpoint() != null ? Uri.parse(this.oauth2Options.getAccessTokenEndpoint()) : parse);
            if (this.authState == null) {
                this.authState = new AuthState(authorizationServiceConfiguration);
            }
            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(authorizationServiceConfiguration, this.oauth2Options.getAppId(), this.oauth2Options.getResponseType(), Uri.parse(this.oauth2Options.getRedirectUrl()));
            if (this.oauth2Options.getState() != null) {
                builder.setState(this.oauth2Options.getState());
            }
            builder.setScope(this.oauth2Options.getScope());
            if (this.oauth2Options.isPkceEnabled()) {
                builder.setCodeVerifier(this.oauth2Options.getPkceCodeVerifier());
            } else {
                builder.setCodeVerifier(null);
            }
            if (this.oauth2Options.getPrompt() != null) {
                builder.setPrompt(this.oauth2Options.getPrompt());
            }
            if (this.oauth2Options.getLoginHint() != null) {
                builder.setLoginHint(this.oauth2Options.getLoginHint());
            }
            if (this.oauth2Options.getResponseMode() != null) {
                builder.setResponseMode(this.oauth2Options.getResponseMode());
            }
            if (this.oauth2Options.getDisplay() != null) {
                builder.setDisplay(this.oauth2Options.getDisplay());
            }
            if (this.oauth2Options.getAdditionalParameters() != null) {
                try {
                    builder.setAdditionalParameters(this.oauth2Options.getAdditionalParameters());
                } catch (IllegalArgumentException e5) {
                    Log.e(getLogTag(), "Additional parameter error", e5);
                }
            }
            AuthorizationRequest build = builder.build();
            AuthorizationService authorizationService = new AuthorizationService(getContext());
            this.authService = authorizationService;
            try {
                Intent authorizationRequestIntent = authorizationService.getAuthorizationRequestIntent(build);
                this.bridge.saveCall(pluginCall);
                startActivityForResult(pluginCall, authorizationRequestIntent, "handleIntentResult");
            } catch (ActivityNotFoundException e6) {
                pluginCall.reject(ERR_ANDROID_NO_BROWSER, e6);
            } catch (Exception e7) {
                Log.e(getLogTag(), "Unexpected exception on open browser for authorization request!");
                pluginCall.reject(ERR_GENERAL, e7);
            }
        }
    }

    @PluginMethod
    public void logout(PluginCall pluginCall) {
        String str = (String) ConfigUtils.getParam(String.class, pluginCall.getData(), PARAM_ANDROID_CUSTOM_HANDLER_CLASS);
        if (str != null && str.length() > 0) {
            try {
                if (((OAuth2CustomHandler) Class.forName(str).newInstance()).logout(getActivity(), pluginCall)) {
                    pluginCall.resolve();
                } else {
                    pluginCall.reject(ERR_CUSTOM_HANDLER_LOGOUT);
                }
                return;
            } catch (ClassNotFoundException e) {
                e = e;
                pluginCall.reject(ERR_CUSTOM_HANDLER_LOGOUT, e);
                return;
            } catch (IllegalAccessException e2) {
                e = e2;
                pluginCall.reject(ERR_CUSTOM_HANDLER_LOGOUT, e);
                return;
            } catch (InstantiationException e3) {
                e = e3;
                pluginCall.reject(ERR_CUSTOM_HANDLER_LOGOUT, e);
                return;
            } catch (Exception e4) {
                pluginCall.reject(ERR_GENERAL, e4);
                return;
            }
        }
        String str2 = (String) ConfigUtils.getParam(String.class, pluginCall.getData(), "id_token");
        if (str2 == null) {
            disposeAuthService();
            discardAuthState();
            pluginCall.resolve();
            return;
        }
        OAuth2Options buildAuthenticateOptions = buildAuthenticateOptions(pluginCall.getData());
        this.oauth2Options = buildAuthenticateOptions;
        Uri parse = Uri.parse(buildAuthenticateOptions.getAuthorizationBaseUrl());
        EndSessionRequest build = new EndSessionRequest.Builder(new AuthorizationServiceConfiguration(parse, this.oauth2Options.getAccessTokenEndpoint() != null ? Uri.parse(this.oauth2Options.getAccessTokenEndpoint()) : parse)).setIdTokenHint(str2).setPostLogoutRedirectUri(Uri.parse(this.oauth2Options.getLogoutUrl())).build();
        AuthorizationService authorizationService = new AuthorizationService(getContext());
        this.authService = authorizationService;
        try {
            Intent endSessionRequestIntent = authorizationService.getEndSessionRequestIntent(build);
            this.bridge.saveCall(pluginCall);
            startActivityForResult(pluginCall, endSessionRequestIntent, "handleEndSessionIntentResult");
        } catch (ActivityNotFoundException e5) {
            pluginCall.reject(ERR_ANDROID_NO_BROWSER, e5);
        } catch (Exception e6) {
            Log.e(getLogTag(), "Unexpected exception on open browser for logout request!");
            pluginCall.reject(ERR_GENERAL, e6);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnNewIntent(Intent intent) {
        PluginCall savedCall;
        OAuth2Options oAuth2Options = this.oauth2Options;
        if (oAuth2Options == null || !oAuth2Options.isHandleResultOnNewIntent() || (savedCall = this.bridge.getSavedCall(this.callbackId)) == null) {
            return;
        }
        handleAuthorizationRequestActivity(intent, savedCall);
    }

    @ActivityCallback
    private void handleIntentResult(PluginCall pluginCall, ActivityResult activityResult) {
        OAuth2Options oAuth2Options = this.oauth2Options;
        if (oAuth2Options == null || !oAuth2Options.isHandleResultOnActivityResult()) {
            return;
        }
        if (activityResult.getResultCode() == 0) {
            pluginCall.reject(USER_CANCELLED);
        } else {
            handleAuthorizationRequestActivity(activityResult.getData(), pluginCall);
        }
    }

    @ActivityCallback
    private void handleEndSessionIntentResult(PluginCall pluginCall, ActivityResult activityResult) {
        if (activityResult.getResultCode() == 0) {
            pluginCall.reject(USER_CANCELLED);
        } else if (activityResult.getData() != null) {
            try {
                JSObject jSObject = new JSObject(EndSessionResponse.fromIntent(activityResult.getData()).jsonSerializeString());
                disposeAuthService();
                discardAuthState();
                pluginCall.resolve(jSObject);
            } catch (Exception e) {
                Log.e(getLogTag(), "Unexpected exception on handling result for logout request!");
                pluginCall.reject(ERR_GENERAL, e);
            }
        }
    }

    void handleAuthorizationRequestActivity(Intent intent, final PluginCall pluginCall) {
        if (intent != null) {
            try {
                final AuthorizationResponse fromIntent = AuthorizationResponse.fromIntent(intent);
                AuthorizationException fromIntent2 = AuthorizationException.fromIntent(intent);
                this.authState.update(fromIntent, fromIntent2);
                if (fromIntent2 != null) {
                    if (fromIntent2.code == AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.code) {
                        pluginCall.reject(USER_CANCELLED);
                        return;
                    } else if (fromIntent2.code == AuthorizationException.AuthorizationRequestErrors.STATE_MISMATCH.code) {
                        if (this.oauth2Options.isLogsEnabled()) {
                            String logTag = getLogTag();
                            Log.i(logTag, "State from web options: " + this.oauth2Options.getState());
                            if (fromIntent != null) {
                                String logTag2 = getLogTag();
                                Log.i(logTag2, "State returned from provider: " + fromIntent.state);
                            }
                        }
                        pluginCall.reject(ERR_STATES_NOT_MATCH);
                        return;
                    } else {
                        pluginCall.reject(ERR_GENERAL, fromIntent2);
                        return;
                    }
                } else if (fromIntent != null) {
                    if (this.oauth2Options.isLogsEnabled()) {
                        String logTag3 = getLogTag();
                        Log.i(logTag3, "Authorization response:\n" + fromIntent.jsonSerializeString());
                    }
                    if (this.oauth2Options.getAccessTokenEndpoint() != null) {
                        this.authService = new AuthorizationService(getContext());
                        try {
                            this.authService.performTokenRequest(fromIntent.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() { // from class: com.byteowls.capacitor.oauth2.OAuth2ClientPlugin$$ExternalSyntheticLambda0
                                @Override // net.openid.appauth.AuthorizationService.TokenResponseCallback
                                public final void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException authorizationException) {
                                    OAuth2ClientPlugin.this.lambda$handleAuthorizationRequestActivity$2(pluginCall, fromIntent, tokenResponse, authorizationException);
                                }
                            });
                            return;
                        } catch (Exception e) {
                            pluginCall.reject(ERR_NO_AUTHORIZATION_CODE, e);
                            return;
                        }
                    }
                    resolveAuthorizationResponse(pluginCall, fromIntent);
                    return;
                } else {
                    pluginCall.reject(ERR_NO_AUTHORIZATION_CODE);
                    return;
                }
            } catch (Exception e2) {
                pluginCall.reject(ERR_GENERAL, e2);
                return;
            }
        }
        pluginCall.reject(ERR_ANDROID_RESULT_NULL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleAuthorizationRequestActivity$2(final PluginCall pluginCall, final AuthorizationResponse authorizationResponse, final TokenResponse tokenResponse, AuthorizationException authorizationException) {
        this.authState.update(tokenResponse, authorizationException);
        if (authorizationException != null) {
            pluginCall.reject(ERR_AUTHORIZATION_FAILED, String.valueOf(authorizationException.code), authorizationException);
        } else if (tokenResponse != null) {
            if (this.oauth2Options.isLogsEnabled()) {
                String logTag = getLogTag();
                Log.i(logTag, "Access token response:\n" + tokenResponse.jsonSerializeString());
            }
            this.authState.performActionWithFreshTokens(this.authService, new AuthState.AuthStateAction() { // from class: com.byteowls.capacitor.oauth2.OAuth2ClientPlugin$$ExternalSyntheticLambda1
                @Override // net.openid.appauth.AuthState.AuthStateAction
                public final void execute(String str, String str2, AuthorizationException authorizationException2) {
                    OAuth2ClientPlugin.this.lambda$handleAuthorizationRequestActivity$1(pluginCall, authorizationResponse, tokenResponse, str, str2, authorizationException2);
                }
            });
        } else {
            resolveAuthorizationResponse(pluginCall, authorizationResponse);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleAuthorizationRequestActivity$1(PluginCall pluginCall, AuthorizationResponse authorizationResponse, TokenResponse tokenResponse, String str, String str2, AuthorizationException authorizationException) {
        new ResourceUrlAsyncTask(pluginCall, this.oauth2Options, getLogTag(), authorizationResponse, tokenResponse).execute(str);
    }

    private void resolveAuthorizationResponse(PluginCall pluginCall, AuthorizationResponse authorizationResponse) {
        JSObject jSObject = new JSObject();
        OAuth2Utils.assignResponses(jSObject, null, authorizationResponse, null);
        pluginCall.resolve(jSObject);
    }

    OAuth2Options buildAuthenticateOptions(JSObject jSObject) {
        OAuth2Options oAuth2Options = new OAuth2Options();
        oAuth2Options.setAppId(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_APP_ID)));
        oAuth2Options.setAuthorizationBaseUrl(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_AUTHORIZATION_BASE_URL)));
        oAuth2Options.setResponseType(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_RESPONSE_TYPE)));
        oAuth2Options.setRedirectUrl(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_REDIRECT_URL)));
        Boolean bool = (Boolean) ConfigUtils.getOverwrittenAndroidParam(Boolean.class, jSObject, PARAM_LOGS_ENABLED);
        boolean z = false;
        oAuth2Options.setLogsEnabled(bool != null && bool.booleanValue());
        oAuth2Options.setResourceUrl(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_RESOURCE_URL)));
        oAuth2Options.setAccessTokenEndpoint(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_ACCESS_TOKEN_ENDPOINT)));
        Boolean bool2 = (Boolean) ConfigUtils.getOverwrittenAndroidParam(Boolean.class, jSObject, PARAM_PKCE_ENABLED);
        if (bool2 != null && bool2.booleanValue()) {
            z = true;
        }
        oAuth2Options.setPkceEnabled(z);
        if (oAuth2Options.isPkceEnabled()) {
            oAuth2Options.setPkceCodeVerifier(ConfigUtils.getRandomString(64));
        }
        oAuth2Options.setScope(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_SCOPE)));
        oAuth2Options.setState(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_STATE)));
        if (oAuth2Options.getState() == null) {
            oAuth2Options.setState(ConfigUtils.getRandomString(20));
        }
        Map<String, String> overwrittenAndroidParamMap = ConfigUtils.getOverwrittenAndroidParamMap(jSObject, PARAM_ADDITIONAL_PARAMETERS);
        if (!overwrittenAndroidParamMap.isEmpty()) {
            for (Map.Entry<String, String> entry : overwrittenAndroidParamMap.entrySet()) {
                String key = entry.getKey();
                if (PARAM_DISPLAY.equals(key)) {
                    oAuth2Options.setDisplay(entry.getValue());
                } else if (PARAM_LOGIN_HINT.equals(key)) {
                    oAuth2Options.setLoginHint(entry.getValue());
                } else if (PARAM_PROMPT.equals(key)) {
                    oAuth2Options.setPrompt(entry.getValue());
                } else if (PARAM_RESPONSE_MODE.equals(key)) {
                    oAuth2Options.setResponseMode(entry.getValue());
                } else {
                    oAuth2Options.addAdditionalParameter(key, entry.getValue());
                }
            }
        }
        oAuth2Options.setAdditionalResourceHeaders(ConfigUtils.getOverwrittenAndroidParamMap(jSObject, PARAM_ADDITIONAL_RESOURCE_HEADERS));
        oAuth2Options.setCustomHandlerClass(ConfigUtils.trimToNull(ConfigUtils.getParamString(jSObject, PARAM_ANDROID_CUSTOM_HANDLER_CLASS)));
        oAuth2Options.setHandleResultOnNewIntent(((Boolean) ConfigUtils.getParam(Boolean.class, jSObject, PARAM_ANDROID_HANDLE_RESULT_ON_NEW_INTENT, false)).booleanValue());
        oAuth2Options.setHandleResultOnActivityResult(((Boolean) ConfigUtils.getParam(Boolean.class, jSObject, PARAM_ANDROID_HANDLE_RESULT_ON_ACTIVITY_RESULT, false)).booleanValue());
        if (!oAuth2Options.isHandleResultOnNewIntent() && !oAuth2Options.isHandleResultOnActivityResult()) {
            oAuth2Options.setHandleResultOnActivityResult(true);
        }
        return oAuth2Options;
    }

    OAuth2RefreshTokenOptions buildRefreshTokenOptions(JSObject jSObject) {
        OAuth2RefreshTokenOptions oAuth2RefreshTokenOptions = new OAuth2RefreshTokenOptions();
        oAuth2RefreshTokenOptions.setAppId(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_APP_ID)));
        oAuth2RefreshTokenOptions.setAccessTokenEndpoint(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_ACCESS_TOKEN_ENDPOINT)));
        oAuth2RefreshTokenOptions.setScope(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_SCOPE)));
        oAuth2RefreshTokenOptions.setRefreshToken(ConfigUtils.trimToNull((String) ConfigUtils.getOverwrittenAndroidParam(String.class, jSObject, PARAM_REFRESH_TOKEN)));
        return oAuth2RefreshTokenOptions;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnStop() {
        super.handleOnStop();
        disposeAuthService();
    }

    private void disposeAuthService() {
        AuthorizationService authorizationService = this.authService;
        if (authorizationService != null) {
            authorizationService.dispose();
            this.authService = null;
        }
    }

    private void discardAuthState() {
        if (this.authState != null) {
            this.authState = null;
        }
    }
}
