package net.openid.appauth;

import android.net.Uri;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class RegistrationResponse {
    static final String KEY_ADDITIONAL_PARAMETERS = "additionalParameters";
    static final String KEY_REQUEST = "request";
    static final String PARAM_CLIENT_ID = "client_id";
    public final Map<String, String> additionalParameters;
    public final String clientId;
    public final Long clientIdIssuedAt;
    public final String clientSecret;
    public final Long clientSecretExpiresAt;
    public final String registrationAccessToken;
    public final Uri registrationClientUri;
    public final RegistrationRequest request;
    public final String tokenEndpointAuthMethod;
    static final String PARAM_CLIENT_SECRET = "client_secret";
    static final String PARAM_CLIENT_SECRET_EXPIRES_AT = "client_secret_expires_at";
    static final String PARAM_REGISTRATION_ACCESS_TOKEN = "registration_access_token";
    static final String PARAM_REGISTRATION_CLIENT_URI = "registration_client_uri";
    static final String PARAM_CLIENT_ID_ISSUED_AT = "client_id_issued_at";
    static final String PARAM_TOKEN_ENDPOINT_AUTH_METHOD = "token_endpoint_auth_method";
    private static final Set<String> BUILT_IN_PARAMS = new HashSet(Arrays.asList("client_id", PARAM_CLIENT_SECRET, PARAM_CLIENT_SECRET_EXPIRES_AT, PARAM_REGISTRATION_ACCESS_TOKEN, PARAM_REGISTRATION_CLIENT_URI, PARAM_CLIENT_ID_ISSUED_AT, PARAM_TOKEN_ENDPOINT_AUTH_METHOD));

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class MissingArgumentException extends Exception {
        private String mMissingField;

        public MissingArgumentException(String field) {
            super("Missing mandatory registration field: " + field);
            this.mMissingField = field;
        }

        public String getMissingField() {
            return this.mMissingField;
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private Map<String, String> mAdditionalParameters = Collections.emptyMap();
        private String mClientId;
        private Long mClientIdIssuedAt;
        private String mClientSecret;
        private Long mClientSecretExpiresAt;
        private String mRegistrationAccessToken;
        private Uri mRegistrationClientUri;
        private RegistrationRequest mRequest;
        private String mTokenEndpointAuthMethod;

        public Builder(RegistrationRequest request) {
            setRequest(request);
        }

        public Builder setRequest(RegistrationRequest request) {
            this.mRequest = (RegistrationRequest) Preconditions.checkNotNull(request, "request cannot be null");
            return this;
        }

        public Builder setClientId(String clientId) {
            Preconditions.checkNotEmpty(clientId, "client ID cannot be null or empty");
            this.mClientId = clientId;
            return this;
        }

        public Builder setClientIdIssuedAt(Long clientIdIssuedAt) {
            this.mClientIdIssuedAt = clientIdIssuedAt;
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            this.mClientSecret = clientSecret;
            return this;
        }

        public Builder setClientSecretExpiresAt(Long clientSecretExpiresAt) {
            this.mClientSecretExpiresAt = clientSecretExpiresAt;
            return this;
        }

        public Builder setRegistrationAccessToken(String registrationAccessToken) {
            this.mRegistrationAccessToken = registrationAccessToken;
            return this;
        }

        public Builder setTokenEndpointAuthMethod(String tokenEndpointAuthMethod) {
            this.mTokenEndpointAuthMethod = tokenEndpointAuthMethod;
            return this;
        }

        public Builder setRegistrationClientUri(Uri registrationClientUri) {
            this.mRegistrationClientUri = registrationClientUri;
            return this;
        }

        public Builder setAdditionalParameters(Map<String, String> additionalParameters) {
            this.mAdditionalParameters = AdditionalParamsProcessor.checkAdditionalParams(additionalParameters, RegistrationResponse.BUILT_IN_PARAMS);
            return this;
        }

        public RegistrationResponse build() {
            return new RegistrationResponse(this.mRequest, this.mClientId, this.mClientIdIssuedAt, this.mClientSecret, this.mClientSecretExpiresAt, this.mRegistrationAccessToken, this.mRegistrationClientUri, this.mTokenEndpointAuthMethod, this.mAdditionalParameters);
        }

        public Builder fromResponseJsonString(String jsonStr) throws JSONException, MissingArgumentException {
            Preconditions.checkNotEmpty(jsonStr, "json cannot be null or empty");
            return fromResponseJson(new JSONObject(jsonStr));
        }

        public Builder fromResponseJson(JSONObject json) throws JSONException, MissingArgumentException {
            setClientId(JsonUtil.getString(json, "client_id"));
            setClientIdIssuedAt(JsonUtil.getLongIfDefined(json, RegistrationResponse.PARAM_CLIENT_ID_ISSUED_AT));
            if (json.has(RegistrationResponse.PARAM_CLIENT_SECRET)) {
                if (!json.has(RegistrationResponse.PARAM_CLIENT_SECRET_EXPIRES_AT)) {
                    throw new MissingArgumentException(RegistrationResponse.PARAM_CLIENT_SECRET_EXPIRES_AT);
                }
                setClientSecret(json.getString(RegistrationResponse.PARAM_CLIENT_SECRET));
                setClientSecretExpiresAt(Long.valueOf(json.getLong(RegistrationResponse.PARAM_CLIENT_SECRET_EXPIRES_AT)));
            }
            String str = RegistrationResponse.PARAM_REGISTRATION_ACCESS_TOKEN;
            if (json.has(RegistrationResponse.PARAM_REGISTRATION_ACCESS_TOKEN) != json.has(RegistrationResponse.PARAM_REGISTRATION_CLIENT_URI)) {
                if (json.has(RegistrationResponse.PARAM_REGISTRATION_ACCESS_TOKEN)) {
                    str = RegistrationResponse.PARAM_REGISTRATION_CLIENT_URI;
                }
                throw new MissingArgumentException(str);
            }
            setRegistrationAccessToken(JsonUtil.getStringIfDefined(json, RegistrationResponse.PARAM_REGISTRATION_ACCESS_TOKEN));
            setRegistrationClientUri(JsonUtil.getUriIfDefined(json, RegistrationResponse.PARAM_REGISTRATION_CLIENT_URI));
            setTokenEndpointAuthMethod(JsonUtil.getStringIfDefined(json, RegistrationResponse.PARAM_TOKEN_ENDPOINT_AUTH_METHOD));
            setAdditionalParameters(AdditionalParamsProcessor.extractAdditionalParams(json, RegistrationResponse.BUILT_IN_PARAMS));
            return this;
        }
    }

    private RegistrationResponse(RegistrationRequest request, String clientId, Long clientIdIssuedAt, String clientSecret, Long clientSecretExpiresAt, String registrationAccessToken, Uri registrationClientUri, String tokenEndpointAuthMethod, Map<String, String> additionalParameters) {
        this.request = request;
        this.clientId = clientId;
        this.clientIdIssuedAt = clientIdIssuedAt;
        this.clientSecret = clientSecret;
        this.clientSecretExpiresAt = clientSecretExpiresAt;
        this.registrationAccessToken = registrationAccessToken;
        this.registrationClientUri = registrationClientUri;
        this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
        this.additionalParameters = additionalParameters;
    }

    public static RegistrationResponse fromJson(RegistrationRequest request, String jsonStr) throws JSONException, MissingArgumentException {
        Preconditions.checkNotEmpty(jsonStr, "jsonStr cannot be null or empty");
        return fromJson(request, new JSONObject(jsonStr));
    }

    public static RegistrationResponse fromJson(RegistrationRequest request, JSONObject json) throws JSONException, MissingArgumentException {
        Preconditions.checkNotNull(request, "registration request cannot be null");
        return new Builder(request).fromResponseJson(json).build();
    }

    public JSONObject jsonSerialize() {
        JSONObject jSONObject = new JSONObject();
        JsonUtil.put(jSONObject, KEY_REQUEST, this.request.jsonSerialize());
        JsonUtil.put(jSONObject, "client_id", this.clientId);
        JsonUtil.putIfNotNull(jSONObject, PARAM_CLIENT_ID_ISSUED_AT, this.clientIdIssuedAt);
        JsonUtil.putIfNotNull(jSONObject, PARAM_CLIENT_SECRET, this.clientSecret);
        JsonUtil.putIfNotNull(jSONObject, PARAM_CLIENT_SECRET_EXPIRES_AT, this.clientSecretExpiresAt);
        JsonUtil.putIfNotNull(jSONObject, PARAM_REGISTRATION_ACCESS_TOKEN, this.registrationAccessToken);
        JsonUtil.putIfNotNull(jSONObject, PARAM_REGISTRATION_CLIENT_URI, this.registrationClientUri);
        JsonUtil.putIfNotNull(jSONObject, PARAM_TOKEN_ENDPOINT_AUTH_METHOD, this.tokenEndpointAuthMethod);
        JsonUtil.put(jSONObject, KEY_ADDITIONAL_PARAMETERS, JsonUtil.mapToJsonObject(this.additionalParameters));
        return jSONObject;
    }

    public String jsonSerializeString() {
        return jsonSerialize().toString();
    }

    public static RegistrationResponse jsonDeserialize(JSONObject json) throws JSONException {
        Preconditions.checkNotNull(json, "json cannot be null");
        if (!json.has(KEY_REQUEST)) {
            throw new IllegalArgumentException("registration request not found in JSON");
        }
        return new Builder(RegistrationRequest.jsonDeserialize(json.getJSONObject(KEY_REQUEST))).setClientId(JsonUtil.getString(json, "client_id")).setClientIdIssuedAt(JsonUtil.getLongIfDefined(json, PARAM_CLIENT_ID_ISSUED_AT)).setClientSecret(JsonUtil.getStringIfDefined(json, PARAM_CLIENT_SECRET)).setClientSecretExpiresAt(JsonUtil.getLongIfDefined(json, PARAM_CLIENT_SECRET_EXPIRES_AT)).setRegistrationAccessToken(JsonUtil.getStringIfDefined(json, PARAM_REGISTRATION_ACCESS_TOKEN)).setRegistrationClientUri(JsonUtil.getUriIfDefined(json, PARAM_REGISTRATION_CLIENT_URI)).setTokenEndpointAuthMethod(JsonUtil.getStringIfDefined(json, PARAM_TOKEN_ENDPOINT_AUTH_METHOD)).setAdditionalParameters(JsonUtil.getStringMap(json, KEY_ADDITIONAL_PARAMETERS)).build();
    }

    public static RegistrationResponse jsonDeserialize(String jsonStr) throws JSONException {
        Preconditions.checkNotEmpty(jsonStr, "jsonStr cannot be null or empty");
        return jsonDeserialize(new JSONObject(jsonStr));
    }

    public boolean hasClientSecretExpired() {
        return hasClientSecretExpired(SystemClock.INSTANCE);
    }

    boolean hasClientSecretExpired(Clock clock) {
        return this.clientSecretExpiresAt != null && Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(((Clock) Preconditions.checkNotNull(clock)).getCurrentTimeMillis())).longValue() > this.clientSecretExpiresAt.longValue();
    }
}
