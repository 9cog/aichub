package net.openid.appauth;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class TokenResponse {
    static final String KEY_ADDITIONAL_PARAMETERS = "additionalParameters";
    static final String KEY_EXPIRES_AT = "expires_at";
    static final String KEY_ID_TOKEN = "id_token";
    static final String KEY_REFRESH_TOKEN = "refresh_token";
    static final String KEY_REQUEST = "request";
    public static final String TOKEN_TYPE_BEARER = "Bearer";
    public final String accessToken;
    public final Long accessTokenExpirationTime;
    public final Map<String, String> additionalParameters;
    public final String idToken;
    public final String refreshToken;
    public final TokenRequest request;
    public final String scope;
    public final String tokenType;
    static final String KEY_TOKEN_TYPE = "token_type";
    static final String KEY_ACCESS_TOKEN = "access_token";
    static final String KEY_EXPIRES_IN = "expires_in";
    static final String KEY_SCOPE = "scope";
    private static final Set<String> BUILT_IN_PARAMS = new HashSet(Arrays.asList(KEY_TOKEN_TYPE, KEY_ACCESS_TOKEN, KEY_EXPIRES_IN, "refresh_token", "id_token", KEY_SCOPE));

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private String mAccessToken;
        private Long mAccessTokenExpirationTime;
        private Map<String, String> mAdditionalParameters;
        private String mIdToken;
        private String mRefreshToken;
        private TokenRequest mRequest;
        private String mScope;
        private String mTokenType;

        public Builder(TokenRequest request) {
            setRequest(request);
            this.mAdditionalParameters = Collections.emptyMap();
        }

        public Builder fromResponseJsonString(String jsonStr) throws JSONException {
            Preconditions.checkNotEmpty(jsonStr, "json cannot be null or empty");
            return fromResponseJson(new JSONObject(jsonStr));
        }

        public Builder fromResponseJson(JSONObject json) throws JSONException {
            setTokenType(JsonUtil.getString(json, TokenResponse.KEY_TOKEN_TYPE));
            setAccessToken(JsonUtil.getStringIfDefined(json, TokenResponse.KEY_ACCESS_TOKEN));
            setAccessTokenExpirationTime(JsonUtil.getLongIfDefined(json, TokenResponse.KEY_EXPIRES_AT));
            if (json.has(TokenResponse.KEY_EXPIRES_IN)) {
                setAccessTokenExpiresIn(Long.valueOf(json.getLong(TokenResponse.KEY_EXPIRES_IN)));
            }
            setRefreshToken(JsonUtil.getStringIfDefined(json, "refresh_token"));
            setIdToken(JsonUtil.getStringIfDefined(json, "id_token"));
            setScope(JsonUtil.getStringIfDefined(json, TokenResponse.KEY_SCOPE));
            setAdditionalParameters(AdditionalParamsProcessor.extractAdditionalParams(json, TokenResponse.BUILT_IN_PARAMS));
            return this;
        }

        public Builder setRequest(TokenRequest request) {
            this.mRequest = (TokenRequest) Preconditions.checkNotNull(request, "request cannot be null");
            return this;
        }

        public Builder setTokenType(String tokenType) {
            this.mTokenType = Preconditions.checkNullOrNotEmpty(tokenType, "token type must not be empty if defined");
            return this;
        }

        public Builder setAccessToken(String accessToken) {
            this.mAccessToken = Preconditions.checkNullOrNotEmpty(accessToken, "access token cannot be empty if specified");
            return this;
        }

        public Builder setAccessTokenExpiresIn(Long expiresIn) {
            return setAccessTokenExpiresIn(expiresIn, SystemClock.INSTANCE);
        }

        Builder setAccessTokenExpiresIn(Long expiresIn, Clock clock) {
            if (expiresIn == null) {
                this.mAccessTokenExpirationTime = null;
            } else {
                this.mAccessTokenExpirationTime = Long.valueOf(clock.getCurrentTimeMillis() + TimeUnit.SECONDS.toMillis(expiresIn.longValue()));
            }
            return this;
        }

        public Builder setAccessTokenExpirationTime(Long expiresAt) {
            this.mAccessTokenExpirationTime = expiresAt;
            return this;
        }

        public Builder setIdToken(String idToken) {
            this.mIdToken = Preconditions.checkNullOrNotEmpty(idToken, "id token must not be empty if defined");
            return this;
        }

        public Builder setRefreshToken(String refreshToken) {
            this.mRefreshToken = Preconditions.checkNullOrNotEmpty(refreshToken, "refresh token must not be empty if defined");
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
                scopes = new String[0];
            }
            setScopes(Arrays.asList(scopes));
            return this;
        }

        public Builder setScopes(Iterable<String> scopes) {
            this.mScope = AsciiStringListUtil.iterableToString(scopes);
            return this;
        }

        public Builder setAdditionalParameters(Map<String, String> additionalParameters) {
            this.mAdditionalParameters = AdditionalParamsProcessor.checkAdditionalParams(additionalParameters, TokenResponse.BUILT_IN_PARAMS);
            return this;
        }

        public TokenResponse build() {
            return new TokenResponse(this.mRequest, this.mTokenType, this.mAccessToken, this.mAccessTokenExpirationTime, this.mIdToken, this.mRefreshToken, this.mScope, this.mAdditionalParameters);
        }
    }

    TokenResponse(TokenRequest request, String tokenType, String accessToken, Long accessTokenExpirationTime, String idToken, String refreshToken, String scope, Map<String, String> additionalParameters) {
        this.request = request;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.additionalParameters = additionalParameters;
    }

    public Set<String> getScopeSet() {
        return AsciiStringListUtil.stringToSet(this.scope);
    }

    public JSONObject jsonSerialize() {
        JSONObject jSONObject = new JSONObject();
        JsonUtil.put(jSONObject, KEY_REQUEST, this.request.jsonSerialize());
        JsonUtil.putIfNotNull(jSONObject, KEY_TOKEN_TYPE, this.tokenType);
        JsonUtil.putIfNotNull(jSONObject, KEY_ACCESS_TOKEN, this.accessToken);
        JsonUtil.putIfNotNull(jSONObject, KEY_EXPIRES_AT, this.accessTokenExpirationTime);
        JsonUtil.putIfNotNull(jSONObject, "id_token", this.idToken);
        JsonUtil.putIfNotNull(jSONObject, "refresh_token", this.refreshToken);
        JsonUtil.putIfNotNull(jSONObject, KEY_SCOPE, this.scope);
        JsonUtil.put(jSONObject, KEY_ADDITIONAL_PARAMETERS, JsonUtil.mapToJsonObject(this.additionalParameters));
        return jSONObject;
    }

    public String jsonSerializeString() {
        return jsonSerialize().toString();
    }

    public static TokenResponse jsonDeserialize(JSONObject json) throws JSONException {
        if (!json.has(KEY_REQUEST)) {
            throw new IllegalArgumentException("token request not provided and not found in JSON");
        }
        return new Builder(TokenRequest.jsonDeserialize(json.getJSONObject(KEY_REQUEST))).setTokenType(JsonUtil.getStringIfDefined(json, KEY_TOKEN_TYPE)).setAccessToken(JsonUtil.getStringIfDefined(json, KEY_ACCESS_TOKEN)).setAccessTokenExpirationTime(JsonUtil.getLongIfDefined(json, KEY_EXPIRES_AT)).setIdToken(JsonUtil.getStringIfDefined(json, "id_token")).setRefreshToken(JsonUtil.getStringIfDefined(json, "refresh_token")).setScope(JsonUtil.getStringIfDefined(json, KEY_SCOPE)).setAdditionalParameters(JsonUtil.getStringMap(json, KEY_ADDITIONAL_PARAMETERS)).build();
    }

    public static TokenResponse jsonDeserialize(String jsonStr) throws JSONException {
        Preconditions.checkNotEmpty(jsonStr, "jsonStr cannot be null or empty");
        return jsonDeserialize(new JSONObject(jsonStr));
    }
}
