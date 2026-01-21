package net.openid.appauth;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.internal.UriUtil;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AuthorizationResponse extends AuthorizationManagementResponse {
    public static final String EXTRA_RESPONSE = "net.openid.appauth.AuthorizationResponse";
    static final String KEY_ADDITIONAL_PARAMETERS = "additional_parameters";
    static final String KEY_AUTHORIZATION_CODE = "code";
    static final String KEY_EXPIRES_AT = "expires_at";
    static final String KEY_ID_TOKEN = "id_token";
    static final String KEY_REQUEST = "request";
    public static final String TOKEN_TYPE_BEARER = "bearer";
    public final String accessToken;
    public final Long accessTokenExpirationTime;
    public final Map<String, String> additionalParameters;
    public final String authorizationCode;
    public final String idToken;
    public final AuthorizationRequest request;
    public final String scope;
    public final String state;
    public final String tokenType;
    static final String KEY_TOKEN_TYPE = "token_type";
    static final String KEY_STATE = "state";
    static final String KEY_ACCESS_TOKEN = "access_token";
    static final String KEY_EXPIRES_IN = "expires_in";
    static final String KEY_SCOPE = "scope";
    private static final Set<String> BUILT_IN_PARAMS = Collections.unmodifiableSet(new HashSet(Arrays.asList(KEY_TOKEN_TYPE, KEY_STATE, "code", KEY_ACCESS_TOKEN, KEY_EXPIRES_IN, "id_token", KEY_SCOPE)));

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private String mAccessToken;
        private Long mAccessTokenExpirationTime;
        private Map<String, String> mAdditionalParameters = new LinkedHashMap();
        private String mAuthorizationCode;
        private String mIdToken;
        private AuthorizationRequest mRequest;
        private String mScope;
        private String mState;
        private String mTokenType;

        public Builder(AuthorizationRequest request) {
            this.mRequest = (AuthorizationRequest) Preconditions.checkNotNull(request, "authorization request cannot be null");
        }

        public Builder fromUri(Uri uri) {
            return fromUri(uri, SystemClock.INSTANCE);
        }

        Builder fromUri(Uri uri, Clock clock) {
            setState(uri.getQueryParameter(AuthorizationResponse.KEY_STATE));
            setTokenType(uri.getQueryParameter(AuthorizationResponse.KEY_TOKEN_TYPE));
            setAuthorizationCode(uri.getQueryParameter("code"));
            setAccessToken(uri.getQueryParameter(AuthorizationResponse.KEY_ACCESS_TOKEN));
            setAccessTokenExpiresIn(UriUtil.getLongQueryParameter(uri, AuthorizationResponse.KEY_EXPIRES_IN), clock);
            setIdToken(uri.getQueryParameter("id_token"));
            setScope(uri.getQueryParameter(AuthorizationResponse.KEY_SCOPE));
            setAdditionalParameters(AdditionalParamsProcessor.extractAdditionalParams(uri, AuthorizationResponse.BUILT_IN_PARAMS));
            return this;
        }

        public Builder setState(String state) {
            Preconditions.checkNullOrNotEmpty(state, "state must not be empty");
            this.mState = state;
            return this;
        }

        public Builder setTokenType(String tokenType) {
            Preconditions.checkNullOrNotEmpty(tokenType, "tokenType must not be empty");
            this.mTokenType = tokenType;
            return this;
        }

        public Builder setAuthorizationCode(String authorizationCode) {
            Preconditions.checkNullOrNotEmpty(authorizationCode, "authorizationCode must not be empty");
            this.mAuthorizationCode = authorizationCode;
            return this;
        }

        public Builder setAccessToken(String accessToken) {
            Preconditions.checkNullOrNotEmpty(accessToken, "accessToken must not be empty");
            this.mAccessToken = accessToken;
            return this;
        }

        public Builder setAccessTokenExpiresIn(Long expiresIn) {
            return setAccessTokenExpiresIn(expiresIn, SystemClock.INSTANCE);
        }

        public Builder setAccessTokenExpiresIn(Long expiresIn, Clock clock) {
            if (expiresIn == null) {
                this.mAccessTokenExpirationTime = null;
            } else {
                this.mAccessTokenExpirationTime = Long.valueOf(clock.getCurrentTimeMillis() + TimeUnit.SECONDS.toMillis(expiresIn.longValue()));
            }
            return this;
        }

        public Builder setAccessTokenExpirationTime(Long expirationTime) {
            this.mAccessTokenExpirationTime = expirationTime;
            return this;
        }

        public Builder setIdToken(String idToken) {
            Preconditions.checkNullOrNotEmpty(idToken, "idToken cannot be empty");
            this.mIdToken = idToken;
            return this;
        }

        public Builder setScope(String scope) {
            if (TextUtils.isEmpty(scope)) {
                this.mScope = null;
            } else {
                setScopes(scope.split(" +"));
            }
            return this;
        }

        public Builder setScopes(String... scopes) {
            if (scopes == null) {
                this.mScope = null;
            } else {
                setScopes(Arrays.asList(scopes));
            }
            return this;
        }

        public Builder setScopes(Iterable<String> scopes) {
            this.mScope = AsciiStringListUtil.iterableToString(scopes);
            return this;
        }

        public Builder setAdditionalParameters(Map<String, String> additionalParameters) {
            this.mAdditionalParameters = AdditionalParamsProcessor.checkAdditionalParams(additionalParameters, AuthorizationResponse.BUILT_IN_PARAMS);
            return this;
        }

        public AuthorizationResponse build() {
            return new AuthorizationResponse(this.mRequest, this.mState, this.mTokenType, this.mAuthorizationCode, this.mAccessToken, this.mAccessTokenExpirationTime, this.mIdToken, this.mScope, Collections.unmodifiableMap(this.mAdditionalParameters));
        }
    }

    private AuthorizationResponse(AuthorizationRequest request, String state, String tokenType, String authorizationCode, String accessToken, Long accessTokenExpirationTime, String idToken, String scope, Map<String, String> additionalParameters) {
        this.request = request;
        this.state = state;
        this.tokenType = tokenType;
        this.authorizationCode = authorizationCode;
        this.accessToken = accessToken;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.idToken = idToken;
        this.scope = scope;
        this.additionalParameters = additionalParameters;
    }

    public boolean hasAccessTokenExpired() {
        return hasAccessTokenExpired(SystemClock.INSTANCE);
    }

    boolean hasAccessTokenExpired(Clock clock) {
        return this.accessTokenExpirationTime != null && ((Clock) Preconditions.checkNotNull(clock)).getCurrentTimeMillis() > this.accessTokenExpirationTime.longValue();
    }

    public Set<String> getScopeSet() {
        return AsciiStringListUtil.stringToSet(this.scope);
    }

    public TokenRequest createTokenExchangeRequest() {
        return createTokenExchangeRequest(Collections.emptyMap());
    }

    public TokenRequest createTokenExchangeRequest(Map<String, String> additionalExchangeParameters) {
        Preconditions.checkNotNull(additionalExchangeParameters, "additionalExchangeParameters cannot be null");
        if (this.authorizationCode == null) {
            throw new IllegalStateException("authorizationCode not available for exchange request");
        }
        return new TokenRequest.Builder(this.request.configuration, this.request.clientId).setGrantType(GrantTypeValues.AUTHORIZATION_CODE).setRedirectUri(this.request.redirectUri).setCodeVerifier(this.request.codeVerifier).setAuthorizationCode(this.authorizationCode).setAdditionalParameters(additionalExchangeParameters).setNonce(this.request.nonce).build();
    }

    @Override // net.openid.appauth.AuthorizationManagementResponse
    public String getState() {
        return this.state;
    }

    @Override // net.openid.appauth.AuthorizationManagementResponse
    public JSONObject jsonSerialize() {
        JSONObject jSONObject = new JSONObject();
        JsonUtil.put(jSONObject, KEY_REQUEST, this.request.jsonSerialize());
        JsonUtil.putIfNotNull(jSONObject, KEY_STATE, this.state);
        JsonUtil.putIfNotNull(jSONObject, KEY_TOKEN_TYPE, this.tokenType);
        JsonUtil.putIfNotNull(jSONObject, "code", this.authorizationCode);
        JsonUtil.putIfNotNull(jSONObject, KEY_ACCESS_TOKEN, this.accessToken);
        JsonUtil.putIfNotNull(jSONObject, KEY_EXPIRES_AT, this.accessTokenExpirationTime);
        JsonUtil.putIfNotNull(jSONObject, "id_token", this.idToken);
        JsonUtil.putIfNotNull(jSONObject, KEY_SCOPE, this.scope);
        JsonUtil.put(jSONObject, KEY_ADDITIONAL_PARAMETERS, JsonUtil.mapToJsonObject(this.additionalParameters));
        return jSONObject;
    }

    public static AuthorizationResponse jsonDeserialize(JSONObject json) throws JSONException {
        if (!json.has(KEY_REQUEST)) {
            throw new IllegalArgumentException("authorization request not provided and not found in JSON");
        }
        return new Builder(AuthorizationRequest.jsonDeserialize(json.getJSONObject(KEY_REQUEST))).setTokenType(JsonUtil.getStringIfDefined(json, KEY_TOKEN_TYPE)).setAccessToken(JsonUtil.getStringIfDefined(json, KEY_ACCESS_TOKEN)).setAuthorizationCode(JsonUtil.getStringIfDefined(json, "code")).setIdToken(JsonUtil.getStringIfDefined(json, "id_token")).setScope(JsonUtil.getStringIfDefined(json, KEY_SCOPE)).setState(JsonUtil.getStringIfDefined(json, KEY_STATE)).setAccessTokenExpirationTime(JsonUtil.getLongIfDefined(json, KEY_EXPIRES_AT)).setAdditionalParameters(JsonUtil.getStringMap(json, KEY_ADDITIONAL_PARAMETERS)).build();
    }

    public static AuthorizationResponse jsonDeserialize(String jsonStr) throws JSONException {
        return jsonDeserialize(new JSONObject(jsonStr));
    }

    @Override // net.openid.appauth.AuthorizationManagementResponse
    public Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESPONSE, jsonSerializeString());
        return intent;
    }

    public static AuthorizationResponse fromIntent(Intent dataIntent) {
        Preconditions.checkNotNull(dataIntent, "dataIntent must not be null");
        if (dataIntent.hasExtra(EXTRA_RESPONSE)) {
            try {
                return jsonDeserialize(dataIntent.getStringExtra(EXTRA_RESPONSE));
            } catch (JSONException e) {
                throw new IllegalArgumentException("Intent contains malformed auth response", e);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean containsAuthorizationResponse(Intent intent) {
        return intent.hasExtra(EXTRA_RESPONSE);
    }
}
