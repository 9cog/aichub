package net.openid.appauth;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class RegistrationRequest {
    public static final String APPLICATION_TYPE_NATIVE = "native";
    static final String KEY_ADDITIONAL_PARAMETERS = "additionalParameters";
    static final String KEY_CONFIGURATION = "configuration";
    public static final String SUBJECT_TYPE_PAIRWISE = "pairwise";
    public static final String SUBJECT_TYPE_PUBLIC = "public";
    public final Map<String, String> additionalParameters;
    public final String applicationType;
    public final AuthorizationServiceConfiguration configuration;
    public final List<String> grantTypes;
    public final JSONObject jwks;
    public final Uri jwksUri;
    public final List<Uri> redirectUris;
    public final List<String> responseTypes;
    public final String subjectType;
    public final String tokenEndpointAuthenticationMethod;
    static final String PARAM_REDIRECT_URIS = "redirect_uris";
    static final String PARAM_RESPONSE_TYPES = "response_types";
    static final String PARAM_GRANT_TYPES = "grant_types";
    static final String PARAM_APPLICATION_TYPE = "application_type";
    static final String PARAM_SUBJECT_TYPE = "subject_type";
    static final String PARAM_JWKS_URI = "jwks_uri";
    static final String PARAM_JWKS = "jwks";
    static final String PARAM_TOKEN_ENDPOINT_AUTHENTICATION_METHOD = "token_endpoint_auth_method";
    private static final Set<String> BUILT_IN_PARAMS = AdditionalParamsProcessor.builtInParams(PARAM_REDIRECT_URIS, PARAM_RESPONSE_TYPES, PARAM_GRANT_TYPES, PARAM_APPLICATION_TYPE, PARAM_SUBJECT_TYPE, PARAM_JWKS_URI, PARAM_JWKS, PARAM_TOKEN_ENDPOINT_AUTHENTICATION_METHOD);

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private AuthorizationServiceConfiguration mConfiguration;
        private List<String> mGrantTypes;
        private JSONObject mJwks;
        private Uri mJwksUri;
        private List<String> mResponseTypes;
        private String mSubjectType;
        private String mTokenEndpointAuthenticationMethod;
        private List<Uri> mRedirectUris = new ArrayList();
        private Map<String, String> mAdditionalParameters = Collections.emptyMap();

        public Builder(AuthorizationServiceConfiguration configuration, List<Uri> redirectUri) {
            setConfiguration(configuration);
            setRedirectUriValues(redirectUri);
        }

        public Builder setConfiguration(AuthorizationServiceConfiguration configuration) {
            this.mConfiguration = (AuthorizationServiceConfiguration) Preconditions.checkNotNull(configuration);
            return this;
        }

        public Builder setRedirectUriValues(Uri... redirectUriValues) {
            return setRedirectUriValues(Arrays.asList(redirectUriValues));
        }

        public Builder setRedirectUriValues(List<Uri> redirectUriValues) {
            Preconditions.checkCollectionNotEmpty(redirectUriValues, "redirectUriValues cannot be null");
            this.mRedirectUris = redirectUriValues;
            return this;
        }

        public Builder setResponseTypeValues(String... responseTypeValues) {
            return setResponseTypeValues(Arrays.asList(responseTypeValues));
        }

        public Builder setResponseTypeValues(List<String> responseTypeValues) {
            this.mResponseTypes = responseTypeValues;
            return this;
        }

        public Builder setGrantTypeValues(String... grantTypeValues) {
            return setGrantTypeValues(Arrays.asList(grantTypeValues));
        }

        public Builder setGrantTypeValues(List<String> grantTypeValues) {
            this.mGrantTypes = grantTypeValues;
            return this;
        }

        public Builder setSubjectType(String subjectType) {
            this.mSubjectType = subjectType;
            return this;
        }

        public Builder setJwksUri(Uri jwksUri) {
            this.mJwksUri = jwksUri;
            return this;
        }

        public Builder setJwks(JSONObject jwks) {
            this.mJwks = jwks;
            return this;
        }

        public Builder setTokenEndpointAuthenticationMethod(String tokenEndpointAuthenticationMethod) {
            this.mTokenEndpointAuthenticationMethod = tokenEndpointAuthenticationMethod;
            return this;
        }

        public Builder setAdditionalParameters(Map<String, String> additionalParameters) {
            this.mAdditionalParameters = AdditionalParamsProcessor.checkAdditionalParams(additionalParameters, RegistrationRequest.BUILT_IN_PARAMS);
            return this;
        }

        public RegistrationRequest build() {
            AuthorizationServiceConfiguration authorizationServiceConfiguration = this.mConfiguration;
            List unmodifiableList = Collections.unmodifiableList(this.mRedirectUris);
            List<String> list = this.mResponseTypes;
            if (list != null) {
                list = Collections.unmodifiableList(list);
            }
            List<String> list2 = list;
            List<String> list3 = this.mGrantTypes;
            if (list3 != null) {
                list3 = Collections.unmodifiableList(list3);
            }
            return new RegistrationRequest(authorizationServiceConfiguration, unmodifiableList, list2, list3, this.mSubjectType, this.mJwksUri, this.mJwks, this.mTokenEndpointAuthenticationMethod, Collections.unmodifiableMap(this.mAdditionalParameters));
        }
    }

    private RegistrationRequest(AuthorizationServiceConfiguration configuration, List<Uri> redirectUris, List<String> responseTypes, List<String> grantTypes, String subjectType, Uri jwksUri, JSONObject jwks, String tokenEndpointAuthenticationMethod, Map<String, String> additionalParameters) {
        this.configuration = configuration;
        this.redirectUris = redirectUris;
        this.responseTypes = responseTypes;
        this.grantTypes = grantTypes;
        this.subjectType = subjectType;
        this.jwksUri = jwksUri;
        this.jwks = jwks;
        this.tokenEndpointAuthenticationMethod = tokenEndpointAuthenticationMethod;
        this.additionalParameters = additionalParameters;
        this.applicationType = APPLICATION_TYPE_NATIVE;
    }

    public String toJsonString() {
        JSONObject jsonSerializeParams = jsonSerializeParams();
        for (Map.Entry<String, String> entry : this.additionalParameters.entrySet()) {
            JsonUtil.put(jsonSerializeParams, entry.getKey(), entry.getValue());
        }
        return jsonSerializeParams.toString();
    }

    public JSONObject jsonSerialize() {
        JSONObject jsonSerializeParams = jsonSerializeParams();
        JsonUtil.put(jsonSerializeParams, KEY_CONFIGURATION, this.configuration.toJson());
        JsonUtil.put(jsonSerializeParams, KEY_ADDITIONAL_PARAMETERS, JsonUtil.mapToJsonObject(this.additionalParameters));
        return jsonSerializeParams;
    }

    public String jsonSerializeString() {
        return jsonSerialize().toString();
    }

    private JSONObject jsonSerializeParams() {
        JSONObject jSONObject = new JSONObject();
        JsonUtil.put(jSONObject, PARAM_REDIRECT_URIS, JsonUtil.toJsonArray(this.redirectUris));
        JsonUtil.put(jSONObject, PARAM_APPLICATION_TYPE, this.applicationType);
        List<String> list = this.responseTypes;
        if (list != null) {
            JsonUtil.put(jSONObject, PARAM_RESPONSE_TYPES, JsonUtil.toJsonArray(list));
        }
        List<String> list2 = this.grantTypes;
        if (list2 != null) {
            JsonUtil.put(jSONObject, PARAM_GRANT_TYPES, JsonUtil.toJsonArray(list2));
        }
        JsonUtil.putIfNotNull(jSONObject, PARAM_SUBJECT_TYPE, this.subjectType);
        JsonUtil.putIfNotNull(jSONObject, PARAM_JWKS_URI, this.jwksUri);
        JsonUtil.putIfNotNull(jSONObject, PARAM_JWKS, this.jwks);
        JsonUtil.putIfNotNull(jSONObject, PARAM_TOKEN_ENDPOINT_AUTHENTICATION_METHOD, this.tokenEndpointAuthenticationMethod);
        return jSONObject;
    }

    public static RegistrationRequest jsonDeserialize(JSONObject json) throws JSONException {
        Preconditions.checkNotNull(json, "json must not be null");
        return new Builder(AuthorizationServiceConfiguration.fromJson(json.getJSONObject(KEY_CONFIGURATION)), JsonUtil.getUriList(json, PARAM_REDIRECT_URIS)).setResponseTypeValues(JsonUtil.getStringListIfDefined(json, PARAM_RESPONSE_TYPES)).setGrantTypeValues(JsonUtil.getStringListIfDefined(json, PARAM_GRANT_TYPES)).setSubjectType(JsonUtil.getStringIfDefined(json, PARAM_SUBJECT_TYPE)).setJwksUri(JsonUtil.getUriIfDefined(json, PARAM_JWKS_URI)).setJwks(JsonUtil.getJsonObjectIfDefined(json, PARAM_JWKS)).setTokenEndpointAuthenticationMethod(JsonUtil.getStringIfDefined(json, PARAM_TOKEN_ENDPOINT_AUTHENTICATION_METHOD)).setAdditionalParameters(JsonUtil.getStringMap(json, KEY_ADDITIONAL_PARAMETERS)).build();
    }

    public static RegistrationRequest jsonDeserialize(String jsonStr) throws JSONException {
        Preconditions.checkNotEmpty(jsonStr, "jsonStr must not be empty or null");
        return jsonDeserialize(new JSONObject(jsonStr));
    }
}
