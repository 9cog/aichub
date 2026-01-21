package net.openid.appauth;

import android.net.Uri;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.openid.appauth.internal.UriUtil;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class EndSessionRequest implements AuthorizationManagementRequest {
    private static final Set<String> BUILT_IN_PARAMS = AdditionalParamsProcessor.builtInParams("id_token_hint", "post_logout_redirect_uri", "state", "ui_locales");
    private static final String KEY_ADDITIONAL_PARAMETERS = "additionalParameters";
    private static final String KEY_CONFIGURATION = "configuration";
    private static final String KEY_ID_TOKEN_HINT = "id_token_hint";
    private static final String KEY_POST_LOGOUT_REDIRECT_URI = "post_logout_redirect_uri";
    private static final String KEY_STATE = "state";
    private static final String KEY_UI_LOCALES = "ui_locales";
    static final String PARAM_ID_TOKEN_HINT = "id_token_hint";
    static final String PARAM_POST_LOGOUT_REDIRECT_URI = "post_logout_redirect_uri";
    static final String PARAM_STATE = "state";
    static final String PARAM_UI_LOCALES = "ui_locales";
    public final Map<String, String> additionalParameters;
    public final AuthorizationServiceConfiguration configuration;
    public final String idTokenHint;
    public final Uri postLogoutRedirectUri;
    public final String state;
    public final String uiLocales;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private Map<String, String> mAdditionalParameters = new HashMap();
        private AuthorizationServiceConfiguration mConfiguration;
        private String mIdTokenHint;
        private Uri mPostLogoutRedirectUri;
        private String mState;
        private String mUiLocales;

        public Builder(AuthorizationServiceConfiguration configuration) {
            setAuthorizationServiceConfiguration(configuration);
            setState(AuthorizationManagementUtil.generateRandomState());
        }

        public Builder setAuthorizationServiceConfiguration(AuthorizationServiceConfiguration configuration) {
            this.mConfiguration = (AuthorizationServiceConfiguration) Preconditions.checkNotNull(configuration, "configuration cannot be null");
            return this;
        }

        public Builder setIdTokenHint(String idTokenHint) {
            this.mIdTokenHint = Preconditions.checkNullOrNotEmpty(idTokenHint, "idTokenHint must not be empty");
            return this;
        }

        public Builder setPostLogoutRedirectUri(Uri postLogoutRedirectUri) {
            this.mPostLogoutRedirectUri = postLogoutRedirectUri;
            return this;
        }

        public Builder setState(String state) {
            this.mState = Preconditions.checkNullOrNotEmpty(state, "state must not be empty");
            return this;
        }

        public Builder setUiLocales(String uiLocales) {
            this.mUiLocales = Preconditions.checkNullOrNotEmpty(uiLocales, "uiLocales must be null or not empty");
            return this;
        }

        public Builder setUiLocalesValues(String... uiLocalesValues) {
            if (uiLocalesValues == null) {
                this.mUiLocales = null;
                return this;
            }
            return setUiLocalesValues(Arrays.asList(uiLocalesValues));
        }

        public Builder setUiLocalesValues(Iterable<String> uiLocalesValues) {
            this.mUiLocales = AsciiStringListUtil.iterableToString(uiLocalesValues);
            return this;
        }

        public Builder setAdditionalParameters(Map<String, String> additionalParameters) {
            this.mAdditionalParameters = AdditionalParamsProcessor.checkAdditionalParams(additionalParameters, EndSessionRequest.BUILT_IN_PARAMS);
            return this;
        }

        public EndSessionRequest build() {
            return new EndSessionRequest(this.mConfiguration, this.mIdTokenHint, this.mPostLogoutRedirectUri, this.mState, this.mUiLocales, Collections.unmodifiableMap(new HashMap(this.mAdditionalParameters)));
        }
    }

    private EndSessionRequest(AuthorizationServiceConfiguration configuration, String idTokenHint, Uri postLogoutRedirectUri, String state, String uiLocales, Map<String, String> additionalParameters) {
        this.configuration = configuration;
        this.idTokenHint = idTokenHint;
        this.postLogoutRedirectUri = postLogoutRedirectUri;
        this.state = state;
        this.uiLocales = uiLocales;
        this.additionalParameters = additionalParameters;
    }

    @Override // net.openid.appauth.AuthorizationManagementRequest
    public String getState() {
        return this.state;
    }

    public Set<String> getUiLocales() {
        return AsciiStringListUtil.stringToSet(this.uiLocales);
    }

    @Override // net.openid.appauth.AuthorizationManagementRequest
    public Uri toUri() {
        Uri.Builder buildUpon = this.configuration.endSessionEndpoint.buildUpon();
        UriUtil.appendQueryParameterIfNotNull(buildUpon, "id_token_hint", this.idTokenHint);
        UriUtil.appendQueryParameterIfNotNull(buildUpon, "state", this.state);
        UriUtil.appendQueryParameterIfNotNull(buildUpon, "ui_locales", this.uiLocales);
        Uri uri = this.postLogoutRedirectUri;
        if (uri != null) {
            buildUpon.appendQueryParameter("post_logout_redirect_uri", uri.toString());
        }
        for (Map.Entry<String, String> entry : this.additionalParameters.entrySet()) {
            buildUpon.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return buildUpon.build();
    }

    @Override // net.openid.appauth.AuthorizationManagementRequest
    public JSONObject jsonSerialize() {
        JSONObject jSONObject = new JSONObject();
        JsonUtil.put(jSONObject, KEY_CONFIGURATION, this.configuration.toJson());
        JsonUtil.putIfNotNull(jSONObject, "id_token_hint", this.idTokenHint);
        JsonUtil.putIfNotNull(jSONObject, "post_logout_redirect_uri", this.postLogoutRedirectUri);
        JsonUtil.putIfNotNull(jSONObject, "state", this.state);
        JsonUtil.putIfNotNull(jSONObject, "ui_locales", this.uiLocales);
        JsonUtil.put(jSONObject, KEY_ADDITIONAL_PARAMETERS, JsonUtil.mapToJsonObject(this.additionalParameters));
        return jSONObject;
    }

    @Override // net.openid.appauth.AuthorizationManagementRequest
    public String jsonSerializeString() {
        return jsonSerialize().toString();
    }

    public static EndSessionRequest jsonDeserialize(JSONObject json) throws JSONException {
        Preconditions.checkNotNull(json, "json cannot be null");
        return new Builder(AuthorizationServiceConfiguration.fromJson(json.getJSONObject(KEY_CONFIGURATION))).setIdTokenHint(JsonUtil.getStringIfDefined(json, "id_token_hint")).setPostLogoutRedirectUri(JsonUtil.getUriIfDefined(json, "post_logout_redirect_uri")).setState(JsonUtil.getStringIfDefined(json, "state")).setUiLocales(JsonUtil.getStringIfDefined(json, "ui_locales")).setAdditionalParameters(JsonUtil.getStringMap(json, KEY_ADDITIONAL_PARAMETERS)).build();
    }

    public static EndSessionRequest jsonDeserialize(String jsonStr) throws JSONException {
        Preconditions.checkNotNull(jsonStr, "json string cannot be null");
        return jsonDeserialize(new JSONObject(jsonStr));
    }
}
