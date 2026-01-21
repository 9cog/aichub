package net.openid.appauth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.internal.Logger;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AuthState {
    public static final int EXPIRY_TIME_TOLERANCE_MS = 60000;
    private static final String KEY_AUTHORIZATION_EXCEPTION = "mAuthorizationException";
    private static final String KEY_CONFIG = "config";
    private static final String KEY_LAST_AUTHORIZATION_RESPONSE = "lastAuthorizationResponse";
    private static final String KEY_LAST_REGISTRATION_RESPONSE = "lastRegistrationResponse";
    private static final String KEY_LAST_TOKEN_RESPONSE = "mLastTokenResponse";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_SCOPE = "scope";
    private AuthorizationException mAuthorizationException;
    private AuthorizationServiceConfiguration mConfig;
    private AuthorizationResponse mLastAuthorizationResponse;
    private RegistrationResponse mLastRegistrationResponse;
    private TokenResponse mLastTokenResponse;
    private boolean mNeedsTokenRefreshOverride;
    private List<AuthStateAction> mPendingActions;
    private final Object mPendingActionsSyncObject;
    private String mRefreshToken;
    private String mScope;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface AuthStateAction {
        void execute(String accessToken, String idToken, AuthorizationException ex);
    }

    public AuthState() {
        this.mPendingActionsSyncObject = new Object();
    }

    public AuthState(AuthorizationServiceConfiguration config) {
        this.mPendingActionsSyncObject = new Object();
        this.mConfig = config;
    }

    public AuthState(AuthorizationResponse authResponse, AuthorizationException authError) {
        this.mPendingActionsSyncObject = new Object();
        Preconditions.checkArgument((authError != null) ^ (authResponse != null), "exactly one of authResponse or authError should be non-null");
        this.mPendingActions = null;
        update(authResponse, authError);
    }

    public AuthState(RegistrationResponse regResponse) {
        this.mPendingActionsSyncObject = new Object();
        update(regResponse);
    }

    public AuthState(AuthorizationResponse authResponse, TokenResponse tokenResponse, AuthorizationException authException) {
        this(authResponse, null);
        update(tokenResponse, authException);
    }

    public String getRefreshToken() {
        return this.mRefreshToken;
    }

    public String getScope() {
        return this.mScope;
    }

    public Set<String> getScopeSet() {
        return AsciiStringListUtil.stringToSet(this.mScope);
    }

    public AuthorizationResponse getLastAuthorizationResponse() {
        return this.mLastAuthorizationResponse;
    }

    public TokenResponse getLastTokenResponse() {
        return this.mLastTokenResponse;
    }

    public RegistrationResponse getLastRegistrationResponse() {
        return this.mLastRegistrationResponse;
    }

    public AuthorizationServiceConfiguration getAuthorizationServiceConfiguration() {
        AuthorizationResponse authorizationResponse = this.mLastAuthorizationResponse;
        if (authorizationResponse != null) {
            return authorizationResponse.request.configuration;
        }
        return this.mConfig;
    }

    public String getAccessToken() {
        if (this.mAuthorizationException != null) {
            return null;
        }
        TokenResponse tokenResponse = this.mLastTokenResponse;
        if (tokenResponse != null && tokenResponse.accessToken != null) {
            return this.mLastTokenResponse.accessToken;
        }
        AuthorizationResponse authorizationResponse = this.mLastAuthorizationResponse;
        if (authorizationResponse != null) {
            return authorizationResponse.accessToken;
        }
        return null;
    }

    public Long getAccessTokenExpirationTime() {
        if (this.mAuthorizationException != null) {
            return null;
        }
        TokenResponse tokenResponse = this.mLastTokenResponse;
        if (tokenResponse != null && tokenResponse.accessToken != null) {
            return this.mLastTokenResponse.accessTokenExpirationTime;
        }
        AuthorizationResponse authorizationResponse = this.mLastAuthorizationResponse;
        if (authorizationResponse == null || authorizationResponse.accessToken == null) {
            return null;
        }
        return this.mLastAuthorizationResponse.accessTokenExpirationTime;
    }

    public String getIdToken() {
        if (this.mAuthorizationException != null) {
            return null;
        }
        TokenResponse tokenResponse = this.mLastTokenResponse;
        if (tokenResponse != null && tokenResponse.idToken != null) {
            return this.mLastTokenResponse.idToken;
        }
        AuthorizationResponse authorizationResponse = this.mLastAuthorizationResponse;
        if (authorizationResponse != null) {
            return authorizationResponse.idToken;
        }
        return null;
    }

    public String getClientSecret() {
        RegistrationResponse registrationResponse = this.mLastRegistrationResponse;
        if (registrationResponse != null) {
            return registrationResponse.clientSecret;
        }
        return null;
    }

    public Long getClientSecretExpirationTime() {
        RegistrationResponse registrationResponse = this.mLastRegistrationResponse;
        if (registrationResponse != null) {
            return registrationResponse.clientSecretExpiresAt;
        }
        return null;
    }

    public boolean isAuthorized() {
        return this.mAuthorizationException == null && !(getAccessToken() == null && getIdToken() == null);
    }

    public AuthorizationException getAuthorizationException() {
        return this.mAuthorizationException;
    }

    public boolean getNeedsTokenRefresh() {
        return getNeedsTokenRefresh(SystemClock.INSTANCE);
    }

    boolean getNeedsTokenRefresh(Clock clock) {
        if (this.mNeedsTokenRefreshOverride) {
            return true;
        }
        return getAccessTokenExpirationTime() == null ? getAccessToken() == null : getAccessTokenExpirationTime().longValue() <= clock.getCurrentTimeMillis() + 60000;
    }

    public void setNeedsTokenRefresh(boolean needsTokenRefresh) {
        this.mNeedsTokenRefreshOverride = needsTokenRefresh;
    }

    public boolean hasClientSecretExpired() {
        return hasClientSecretExpired(SystemClock.INSTANCE);
    }

    boolean hasClientSecretExpired(Clock clock) {
        return (getClientSecretExpirationTime() == null || getClientSecretExpirationTime().longValue() == 0 || getClientSecretExpirationTime().longValue() > clock.getCurrentTimeMillis()) ? false : true;
    }

    public void update(AuthorizationResponse authResponse, AuthorizationException authException) {
        Preconditions.checkArgument((authException != null) ^ (authResponse != null), "exactly one of authResponse or authException should be non-null");
        if (authException != null) {
            if (authException.type == 1) {
                this.mAuthorizationException = authException;
                return;
            }
            return;
        }
        this.mLastAuthorizationResponse = authResponse;
        this.mConfig = null;
        this.mLastTokenResponse = null;
        this.mRefreshToken = null;
        this.mAuthorizationException = null;
        this.mScope = authResponse.scope != null ? authResponse.scope : authResponse.request.scope;
    }

    public void update(TokenResponse tokenResponse, AuthorizationException authException) {
        Preconditions.checkArgument((tokenResponse != null) ^ (authException != null), "exactly one of tokenResponse or authException should be non-null");
        AuthorizationException authorizationException = this.mAuthorizationException;
        if (authorizationException != null) {
            Logger.warn("AuthState.update should not be called in an error state (%s), call updatewith the result of the fresh authorization response first", authorizationException);
            this.mAuthorizationException = null;
        }
        if (authException != null) {
            if (authException.type == 2) {
                this.mAuthorizationException = authException;
                return;
            }
            return;
        }
        this.mLastTokenResponse = tokenResponse;
        if (tokenResponse.scope != null) {
            this.mScope = tokenResponse.scope;
        }
        if (tokenResponse.refreshToken != null) {
            this.mRefreshToken = tokenResponse.refreshToken;
        }
    }

    public void update(RegistrationResponse regResponse) {
        this.mLastRegistrationResponse = regResponse;
        this.mConfig = getAuthorizationServiceConfiguration();
        this.mRefreshToken = null;
        this.mScope = null;
        this.mLastAuthorizationResponse = null;
        this.mLastTokenResponse = null;
        this.mAuthorizationException = null;
    }

    public void performActionWithFreshTokens(AuthorizationService service, AuthStateAction action) {
        performActionWithFreshTokens(service, NoClientAuthentication.INSTANCE, Collections.emptyMap(), SystemClock.INSTANCE, action);
    }

    public void performActionWithFreshTokens(AuthorizationService service, ClientAuthentication clientAuth, AuthStateAction action) {
        performActionWithFreshTokens(service, clientAuth, Collections.emptyMap(), SystemClock.INSTANCE, action);
    }

    public void performActionWithFreshTokens(AuthorizationService service, Map<String, String> refreshTokenAdditionalParams, AuthStateAction action) {
        try {
            performActionWithFreshTokens(service, getClientAuthentication(), refreshTokenAdditionalParams, SystemClock.INSTANCE, action);
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod e) {
            action.execute(null, null, AuthorizationException.fromTemplate(AuthorizationException.TokenRequestErrors.CLIENT_ERROR, e));
        }
    }

    public void performActionWithFreshTokens(AuthorizationService service, ClientAuthentication clientAuth, Map<String, String> refreshTokenAdditionalParams, AuthStateAction action) {
        performActionWithFreshTokens(service, clientAuth, refreshTokenAdditionalParams, SystemClock.INSTANCE, action);
    }

    void performActionWithFreshTokens(final AuthorizationService service, final ClientAuthentication clientAuth, final Map<String, String> refreshTokenAdditionalParams, final Clock clock, final AuthStateAction action) {
        Preconditions.checkNotNull(service, "service cannot be null");
        Preconditions.checkNotNull(clientAuth, "client authentication cannot be null");
        Preconditions.checkNotNull(refreshTokenAdditionalParams, "additional params cannot be null");
        Preconditions.checkNotNull(clock, "clock cannot be null");
        Preconditions.checkNotNull(action, "action cannot be null");
        if (!getNeedsTokenRefresh(clock)) {
            action.execute(getAccessToken(), getIdToken(), null);
        } else if (this.mRefreshToken == null) {
            action.execute(null, null, AuthorizationException.fromTemplate(AuthorizationException.AuthorizationRequestErrors.CLIENT_ERROR, new IllegalStateException("No refresh token available and token have expired")));
        } else {
            Preconditions.checkNotNull(this.mPendingActionsSyncObject, "pending actions sync object cannot be null");
            synchronized (this.mPendingActionsSyncObject) {
                List<AuthStateAction> list = this.mPendingActions;
                if (list != null) {
                    list.add(action);
                    return;
                }
                ArrayList arrayList = new ArrayList();
                this.mPendingActions = arrayList;
                arrayList.add(action);
                service.performTokenRequest(createTokenRefreshRequest(refreshTokenAdditionalParams), clientAuth, new AuthorizationService.TokenResponseCallback() { // from class: net.openid.appauth.AuthState.1
                    @Override // net.openid.appauth.AuthorizationService.TokenResponseCallback
                    public void onTokenRequestCompleted(TokenResponse response, AuthorizationException ex) {
                        String str;
                        AuthorizationException authorizationException;
                        String str2;
                        List<AuthStateAction> list2;
                        AuthState.this.update(response, ex);
                        if (ex == null) {
                            AuthState.this.mNeedsTokenRefreshOverride = false;
                            str2 = AuthState.this.getAccessToken();
                            str = AuthState.this.getIdToken();
                            authorizationException = null;
                        } else {
                            str = null;
                            authorizationException = ex;
                            str2 = null;
                        }
                        synchronized (AuthState.this.mPendingActionsSyncObject) {
                            list2 = AuthState.this.mPendingActions;
                            AuthState.this.mPendingActions = null;
                        }
                        for (AuthStateAction authStateAction : list2) {
                            authStateAction.execute(str2, str, authorizationException);
                        }
                    }
                });
            }
        }
    }

    public TokenRequest createTokenRefreshRequest() {
        return createTokenRefreshRequest(Collections.emptyMap());
    }

    public TokenRequest createTokenRefreshRequest(Map<String, String> additionalParameters) {
        if (this.mRefreshToken == null) {
            throw new IllegalStateException("No refresh token available for refresh request");
        }
        AuthorizationResponse authorizationResponse = this.mLastAuthorizationResponse;
        if (authorizationResponse == null) {
            throw new IllegalStateException("No authorization configuration available for refresh request");
        }
        return new TokenRequest.Builder(authorizationResponse.request.configuration, this.mLastAuthorizationResponse.request.clientId).setGrantType(GrantTypeValues.REFRESH_TOKEN).setScope(null).setRefreshToken(this.mRefreshToken).setAdditionalParameters(additionalParameters).build();
    }

    public JSONObject jsonSerialize() {
        JSONObject jSONObject = new JSONObject();
        JsonUtil.putIfNotNull(jSONObject, KEY_REFRESH_TOKEN, this.mRefreshToken);
        JsonUtil.putIfNotNull(jSONObject, KEY_SCOPE, this.mScope);
        AuthorizationServiceConfiguration authorizationServiceConfiguration = this.mConfig;
        if (authorizationServiceConfiguration != null) {
            JsonUtil.put(jSONObject, KEY_CONFIG, authorizationServiceConfiguration.toJson());
        }
        AuthorizationException authorizationException = this.mAuthorizationException;
        if (authorizationException != null) {
            JsonUtil.put(jSONObject, KEY_AUTHORIZATION_EXCEPTION, authorizationException.toJson());
        }
        AuthorizationResponse authorizationResponse = this.mLastAuthorizationResponse;
        if (authorizationResponse != null) {
            JsonUtil.put(jSONObject, KEY_LAST_AUTHORIZATION_RESPONSE, authorizationResponse.jsonSerialize());
        }
        TokenResponse tokenResponse = this.mLastTokenResponse;
        if (tokenResponse != null) {
            JsonUtil.put(jSONObject, KEY_LAST_TOKEN_RESPONSE, tokenResponse.jsonSerialize());
        }
        RegistrationResponse registrationResponse = this.mLastRegistrationResponse;
        if (registrationResponse != null) {
            JsonUtil.put(jSONObject, KEY_LAST_REGISTRATION_RESPONSE, registrationResponse.jsonSerialize());
        }
        return jSONObject;
    }

    public String jsonSerializeString() {
        return jsonSerialize().toString();
    }

    public static AuthState jsonDeserialize(JSONObject json) throws JSONException {
        Preconditions.checkNotNull(json, "json cannot be null");
        AuthState authState = new AuthState();
        authState.mRefreshToken = JsonUtil.getStringIfDefined(json, KEY_REFRESH_TOKEN);
        authState.mScope = JsonUtil.getStringIfDefined(json, KEY_SCOPE);
        if (json.has(KEY_CONFIG)) {
            authState.mConfig = AuthorizationServiceConfiguration.fromJson(json.getJSONObject(KEY_CONFIG));
        }
        if (json.has(KEY_AUTHORIZATION_EXCEPTION)) {
            authState.mAuthorizationException = AuthorizationException.fromJson(json.getJSONObject(KEY_AUTHORIZATION_EXCEPTION));
        }
        if (json.has(KEY_LAST_AUTHORIZATION_RESPONSE)) {
            authState.mLastAuthorizationResponse = AuthorizationResponse.jsonDeserialize(json.getJSONObject(KEY_LAST_AUTHORIZATION_RESPONSE));
        }
        if (json.has(KEY_LAST_TOKEN_RESPONSE)) {
            authState.mLastTokenResponse = TokenResponse.jsonDeserialize(json.getJSONObject(KEY_LAST_TOKEN_RESPONSE));
        }
        if (json.has(KEY_LAST_REGISTRATION_RESPONSE)) {
            authState.mLastRegistrationResponse = RegistrationResponse.jsonDeserialize(json.getJSONObject(KEY_LAST_REGISTRATION_RESPONSE));
        }
        return authState;
    }

    public static AuthState jsonDeserialize(String jsonStr) throws JSONException {
        Preconditions.checkNotEmpty(jsonStr, "jsonStr cannot be null or empty");
        return jsonDeserialize(new JSONObject(jsonStr));
    }

    public ClientAuthentication getClientAuthentication() throws ClientAuthentication.UnsupportedAuthenticationMethod {
        if (getClientSecret() == null) {
            return NoClientAuthentication.INSTANCE;
        }
        if (this.mLastRegistrationResponse.tokenEndpointAuthMethod == null) {
            return new ClientSecretBasic(getClientSecret());
        }
        String str = this.mLastRegistrationResponse.tokenEndpointAuthMethod;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2034587045:
                if (str.equals(ClientSecretPost.NAME)) {
                    c = 0;
                    break;
                }
                break;
            case 3387192:
                if (str.equals("none")) {
                    c = 1;
                    break;
                }
                break;
            case 1338964435:
                if (str.equals(ClientSecretBasic.NAME)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return new ClientSecretPost(getClientSecret());
            case 1:
                return NoClientAuthentication.INSTANCE;
            case 2:
                return new ClientSecretBasic(getClientSecret());
            default:
                throw new ClientAuthentication.UnsupportedAuthenticationMethod(this.mLastRegistrationResponse.tokenEndpointAuthMethod);
        }
    }
}
