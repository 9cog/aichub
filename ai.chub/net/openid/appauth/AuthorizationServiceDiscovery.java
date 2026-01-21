package net.openid.appauth;

import android.net.Uri;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.openid.appauth.JsonUtil;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AuthorizationServiceDiscovery {
    static final JsonUtil.StringListField ACR_VALUES_SUPPORTED;
    static final JsonUtil.UriField AUTHORIZATION_ENDPOINT;
    static final JsonUtil.StringListField CLAIMS_LOCALES_SUPPORTED;
    static final JsonUtil.BooleanField CLAIMS_PARAMETER_SUPPORTED;
    static final JsonUtil.StringListField CLAIMS_SUPPORTED;
    static final JsonUtil.StringListField CLAIM_TYPES_SUPPORTED;
    static final JsonUtil.StringListField DISPLAY_VALUES_SUPPORTED;
    static final JsonUtil.UriField END_SESSION_ENDPOINT;
    static final JsonUtil.StringListField GRANT_TYPES_SUPPORTED;
    static final JsonUtil.StringListField ID_TOKEN_ENCRYPTION_ALG_VALUES_SUPPORTED;
    static final JsonUtil.StringListField ID_TOKEN_ENCRYPTION_ENC_VALUES_SUPPORTED;
    static final JsonUtil.StringListField ID_TOKEN_SIGNING_ALG_VALUES_SUPPORTED;
    static final JsonUtil.StringField ISSUER;
    static final JsonUtil.UriField JWKS_URI;
    private static final List<String> MANDATORY_METADATA;
    static final JsonUtil.UriField OP_POLICY_URI;
    static final JsonUtil.UriField OP_TOS_URI;
    static final JsonUtil.UriField REGISTRATION_ENDPOINT;
    static final JsonUtil.StringListField REQUEST_OBJECT_ENCRYPTION_ALG_VALUES_SUPPORTED;
    static final JsonUtil.StringListField REQUEST_OBJECT_ENCRYPTION_ENC_VALUES_SUPPORTED;
    static final JsonUtil.StringListField REQUEST_OBJECT_SIGNING_ALG_VALUES_SUPPORTED;
    static final JsonUtil.BooleanField REQUEST_PARAMETER_SUPPORTED;
    static final JsonUtil.BooleanField REQUEST_URI_PARAMETER_SUPPORTED;
    static final JsonUtil.BooleanField REQUIRE_REQUEST_URI_REGISTRATION;
    static final JsonUtil.StringListField RESPONSE_MODES_SUPPORTED;
    static final JsonUtil.StringListField RESPONSE_TYPES_SUPPORTED;
    static final JsonUtil.StringListField SCOPES_SUPPORTED;
    static final JsonUtil.UriField SERVICE_DOCUMENTATION;
    static final JsonUtil.StringListField SUBJECT_TYPES_SUPPORTED;
    static final JsonUtil.UriField TOKEN_ENDPOINT;
    static final JsonUtil.StringListField TOKEN_ENDPOINT_AUTH_METHODS_SUPPORTED;
    static final JsonUtil.StringListField TOKEN_ENDPOINT_AUTH_SIGNING_ALG_VALUES_SUPPORTED;
    static final JsonUtil.StringListField UI_LOCALES_SUPPORTED;
    static final JsonUtil.StringListField USERINFO_ENCRYPTION_ALG_VALUES_SUPPORTED;
    static final JsonUtil.StringListField USERINFO_ENCRYPTION_ENC_VALUES_SUPPORTED;
    static final JsonUtil.UriField USERINFO_ENDPOINT;
    static final JsonUtil.StringListField USERINFO_SIGNING_ALG_VALUES_SUPPORTED;
    public final JSONObject docJson;

    static {
        JsonUtil.StringField str = str("issuer");
        ISSUER = str;
        JsonUtil.UriField uri = uri("authorization_endpoint");
        AUTHORIZATION_ENDPOINT = uri;
        TOKEN_ENDPOINT = uri("token_endpoint");
        END_SESSION_ENDPOINT = uri("end_session_endpoint");
        USERINFO_ENDPOINT = uri("userinfo_endpoint");
        JsonUtil.UriField uri2 = uri("jwks_uri");
        JWKS_URI = uri2;
        REGISTRATION_ENDPOINT = uri("registration_endpoint");
        SCOPES_SUPPORTED = strList("scopes_supported");
        JsonUtil.StringListField strList = strList("response_types_supported");
        RESPONSE_TYPES_SUPPORTED = strList;
        RESPONSE_MODES_SUPPORTED = strList("response_modes_supported");
        GRANT_TYPES_SUPPORTED = strList("grant_types_supported", Arrays.asList(GrantTypeValues.AUTHORIZATION_CODE, GrantTypeValues.IMPLICIT));
        ACR_VALUES_SUPPORTED = strList("acr_values_supported");
        JsonUtil.StringListField strList2 = strList("subject_types_supported");
        SUBJECT_TYPES_SUPPORTED = strList2;
        JsonUtil.StringListField strList3 = strList("id_token_signing_alg_values_supported");
        ID_TOKEN_SIGNING_ALG_VALUES_SUPPORTED = strList3;
        ID_TOKEN_ENCRYPTION_ALG_VALUES_SUPPORTED = strList("id_token_encryption_enc_values_supported");
        ID_TOKEN_ENCRYPTION_ENC_VALUES_SUPPORTED = strList("id_token_encryption_enc_values_supported");
        USERINFO_SIGNING_ALG_VALUES_SUPPORTED = strList("userinfo_signing_alg_values_supported");
        USERINFO_ENCRYPTION_ALG_VALUES_SUPPORTED = strList("userinfo_encryption_alg_values_supported");
        USERINFO_ENCRYPTION_ENC_VALUES_SUPPORTED = strList("userinfo_encryption_enc_values_supported");
        REQUEST_OBJECT_SIGNING_ALG_VALUES_SUPPORTED = strList("request_object_signing_alg_values_supported");
        REQUEST_OBJECT_ENCRYPTION_ALG_VALUES_SUPPORTED = strList("request_object_encryption_alg_values_supported");
        REQUEST_OBJECT_ENCRYPTION_ENC_VALUES_SUPPORTED = strList("request_object_encryption_enc_values_supported");
        TOKEN_ENDPOINT_AUTH_METHODS_SUPPORTED = strList("token_endpoint_auth_methods_supported", Collections.singletonList(ClientSecretBasic.NAME));
        TOKEN_ENDPOINT_AUTH_SIGNING_ALG_VALUES_SUPPORTED = strList("token_endpoint_auth_signing_alg_values_supported");
        DISPLAY_VALUES_SUPPORTED = strList("display_values_supported");
        CLAIM_TYPES_SUPPORTED = strList("claim_types_supported", Collections.singletonList("normal"));
        CLAIMS_SUPPORTED = strList("claims_supported");
        SERVICE_DOCUMENTATION = uri("service_documentation");
        CLAIMS_LOCALES_SUPPORTED = strList("claims_locales_supported");
        UI_LOCALES_SUPPORTED = strList("ui_locales_supported");
        CLAIMS_PARAMETER_SUPPORTED = bool("claims_parameter_supported", false);
        REQUEST_PARAMETER_SUPPORTED = bool("request_parameter_supported", false);
        REQUEST_URI_PARAMETER_SUPPORTED = bool("request_uri_parameter_supported", true);
        REQUIRE_REQUEST_URI_REGISTRATION = bool("require_request_uri_registration", false);
        OP_POLICY_URI = uri("op_policy_uri");
        OP_TOS_URI = uri("op_tos_uri");
        MANDATORY_METADATA = Arrays.asList(str.key, uri.key, uri2.key, strList.key, strList2.key, strList3.key);
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x0017  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public AuthorizationServiceDiscovery(JSONObject discoveryDoc) throws JSONException, MissingArgumentException {
        this.docJson = (JSONObject) Preconditions.checkNotNull(discoveryDoc);
        for (String str : MANDATORY_METADATA) {
            if (!this.docJson.has(str) || this.docJson.get(str) == null) {
                throw new MissingArgumentException(str);
            }
            while (r3.hasNext()) {
            }
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class MissingArgumentException extends Exception {
        private String mMissingField;

        public MissingArgumentException(String field) {
            super("Missing mandatory configuration field: " + field);
            this.mMissingField = field;
        }

        public String getMissingField() {
            return this.mMissingField;
        }
    }

    private <T> T get(JsonUtil.Field<T> field) {
        return (T) JsonUtil.get(this.docJson, field);
    }

    private <T> List<T> get(JsonUtil.ListField<T> field) {
        return JsonUtil.get(this.docJson, field);
    }

    public String getIssuer() {
        return (String) get(ISSUER);
    }

    public Uri getAuthorizationEndpoint() {
        return (Uri) get(AUTHORIZATION_ENDPOINT);
    }

    public Uri getTokenEndpoint() {
        return (Uri) get(TOKEN_ENDPOINT);
    }

    public Uri getEndSessionEndpoint() {
        return (Uri) get(END_SESSION_ENDPOINT);
    }

    public Uri getUserinfoEndpoint() {
        return (Uri) get(USERINFO_ENDPOINT);
    }

    public Uri getJwksUri() {
        return (Uri) get(JWKS_URI);
    }

    public Uri getRegistrationEndpoint() {
        return (Uri) get(REGISTRATION_ENDPOINT);
    }

    public List<String> getScopesSupported() {
        return get(SCOPES_SUPPORTED);
    }

    public List<String> getResponseTypesSupported() {
        return get(RESPONSE_TYPES_SUPPORTED);
    }

    public List<String> getResponseModesSupported() {
        return get(RESPONSE_MODES_SUPPORTED);
    }

    public List<String> getGrantTypesSupported() {
        return get(GRANT_TYPES_SUPPORTED);
    }

    public List<String> getAcrValuesSupported() {
        return get(ACR_VALUES_SUPPORTED);
    }

    public List<String> getSubjectTypesSupported() {
        return get(SUBJECT_TYPES_SUPPORTED);
    }

    public List<String> getIdTokenSigningAlgorithmValuesSupported() {
        return get(ID_TOKEN_SIGNING_ALG_VALUES_SUPPORTED);
    }

    public List<String> getIdTokenEncryptionAlgorithmValuesSupported() {
        return get(ID_TOKEN_ENCRYPTION_ALG_VALUES_SUPPORTED);
    }

    public List<String> getIdTokenEncryptionEncodingValuesSupported() {
        return get(ID_TOKEN_ENCRYPTION_ENC_VALUES_SUPPORTED);
    }

    public List<String> getUserinfoSigningAlgorithmValuesSupported() {
        return get(USERINFO_SIGNING_ALG_VALUES_SUPPORTED);
    }

    public List<String> getUserinfoEncryptionAlgorithmValuesSupported() {
        return get(USERINFO_ENCRYPTION_ALG_VALUES_SUPPORTED);
    }

    public List<String> getUserinfoEncryptionEncodingValuesSupported() {
        return get(USERINFO_ENCRYPTION_ENC_VALUES_SUPPORTED);
    }

    public List<String> getRequestObjectSigningAlgorithmValuesSupported() {
        return get(REQUEST_OBJECT_SIGNING_ALG_VALUES_SUPPORTED);
    }

    public List<String> getRequestObjectEncryptionAlgorithmValuesSupported() {
        return get(REQUEST_OBJECT_ENCRYPTION_ALG_VALUES_SUPPORTED);
    }

    public List<String> getRequestObjectEncryptionEncodingValuesSupported() {
        return get(REQUEST_OBJECT_ENCRYPTION_ENC_VALUES_SUPPORTED);
    }

    public List<String> getTokenEndpointAuthMethodsSupported() {
        return get(TOKEN_ENDPOINT_AUTH_METHODS_SUPPORTED);
    }

    public List<String> getTokenEndpointAuthSigningAlgorithmValuesSupported() {
        return get(TOKEN_ENDPOINT_AUTH_SIGNING_ALG_VALUES_SUPPORTED);
    }

    public List<String> getDisplayValuesSupported() {
        return get(DISPLAY_VALUES_SUPPORTED);
    }

    public List<String> getClaimTypesSupported() {
        return get(CLAIM_TYPES_SUPPORTED);
    }

    public List<String> getClaimsSupported() {
        return get(CLAIMS_SUPPORTED);
    }

    public Uri getServiceDocumentation() {
        return (Uri) get(SERVICE_DOCUMENTATION);
    }

    public List<String> getClaimsLocalesSupported() {
        return get(CLAIMS_LOCALES_SUPPORTED);
    }

    public List<String> getUiLocalesSupported() {
        return get(UI_LOCALES_SUPPORTED);
    }

    public boolean isClaimsParameterSupported() {
        return ((Boolean) get(CLAIMS_PARAMETER_SUPPORTED)).booleanValue();
    }

    public boolean isRequestParameterSupported() {
        return ((Boolean) get(REQUEST_PARAMETER_SUPPORTED)).booleanValue();
    }

    public boolean isRequestUriParameterSupported() {
        return ((Boolean) get(REQUEST_URI_PARAMETER_SUPPORTED)).booleanValue();
    }

    public boolean requireRequestUriRegistration() {
        return ((Boolean) get(REQUIRE_REQUEST_URI_REGISTRATION)).booleanValue();
    }

    public Uri getOpPolicyUri() {
        return (Uri) get(OP_POLICY_URI);
    }

    public Uri getOpTosUri() {
        return (Uri) get(OP_TOS_URI);
    }

    private static JsonUtil.StringField str(String key) {
        return new JsonUtil.StringField(key);
    }

    private static JsonUtil.UriField uri(String key) {
        return new JsonUtil.UriField(key);
    }

    private static JsonUtil.StringListField strList(String key) {
        return new JsonUtil.StringListField(key);
    }

    private static JsonUtil.StringListField strList(String key, List<String> defaults) {
        return new JsonUtil.StringListField(key, defaults);
    }

    private static JsonUtil.BooleanField bool(String key, boolean defaultValue) {
        return new JsonUtil.BooleanField(key, defaultValue);
    }
}
