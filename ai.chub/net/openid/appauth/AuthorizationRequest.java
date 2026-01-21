package net.openid.appauth;

import android.net.Uri;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.openid.appauth.internal.UriUtil;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AuthorizationRequest implements AuthorizationManagementRequest {
    public static final String CODE_CHALLENGE_METHOD_PLAIN = "plain";
    public static final String CODE_CHALLENGE_METHOD_S256 = "S256";
    private static final String KEY_ADDITIONAL_PARAMETERS = "additionalParameters";
    private static final String KEY_CLIENT_ID = "clientId";
    private static final String KEY_CODE_VERIFIER = "codeVerifier";
    private static final String KEY_CODE_VERIFIER_CHALLENGE = "codeVerifierChallenge";
    private static final String KEY_CODE_VERIFIER_CHALLENGE_METHOD = "codeVerifierChallengeMethod";
    private static final String KEY_CONFIGURATION = "configuration";
    private static final String KEY_DISPLAY = "display";
    private static final String KEY_LOGIN_HINT = "login_hint";
    private static final String KEY_NONCE = "nonce";
    private static final String KEY_PROMPT = "prompt";
    private static final String KEY_REDIRECT_URI = "redirectUri";
    private static final String KEY_RESPONSE_MODE = "responseMode";
    private static final String KEY_RESPONSE_TYPE = "responseType";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_STATE = "state";
    private static final String KEY_UI_LOCALES = "ui_locales";
    static final String PARAM_CLIENT_ID = "client_id";
    static final String PARAM_DISPLAY = "display";
    static final String PARAM_LOGIN_HINT = "login_hint";
    static final String PARAM_NONCE = "nonce";
    static final String PARAM_PROMPT = "prompt";
    static final String PARAM_SCOPE = "scope";
    static final String PARAM_STATE = "state";
    static final String PARAM_UI_LOCALES = "ui_locales";
    public final Map<String, String> additionalParameters;
    public final String clientId;
    public final String codeVerifier;
    public final String codeVerifierChallenge;
    public final String codeVerifierChallengeMethod;
    public final AuthorizationServiceConfiguration configuration;
    public final String display;
    public final String loginHint;
    public final String nonce;
    public final String prompt;
    public final Uri redirectUri;
    public final String responseMode;
    public final String responseType;
    public final String scope;
    public final String state;
    public final String uiLocales;
    static final String PARAM_CODE_CHALLENGE = "code_challenge";
    static final String PARAM_CODE_CHALLENGE_METHOD = "code_challenge_method";
    static final String PARAM_REDIRECT_URI = "redirect_uri";
    static final String PARAM_RESPONSE_MODE = "response_mode";
    static final String PARAM_RESPONSE_TYPE = "response_type";
    private static final Set<String> BUILT_IN_PARAMS = AdditionalParamsProcessor.builtInParams("client_id", PARAM_CODE_CHALLENGE, PARAM_CODE_CHALLENGE_METHOD, "display", "login_hint", "prompt", "ui_locales", PARAM_REDIRECT_URI, PARAM_RESPONSE_MODE, PARAM_RESPONSE_TYPE, "scope", "state");

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Display {
        public static final String PAGE = "page";
        public static final String POPUP = "popup";
        public static final String TOUCH = "touch";
        public static final String WAP = "wap";
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Prompt {
        public static final String CONSENT = "consent";
        public static final String LOGIN = "login";
        public static final String NONE = "none";
        public static final String SELECT_ACCOUNT = "select_account";
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class ResponseMode {
        public static final String FRAGMENT = "fragment";
        public static final String QUERY = "query";
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Scope {
        public static final String ADDRESS = "address";
        public static final String EMAIL = "email";
        public static final String OFFLINE_ACCESS = "offline_access";
        public static final String OPENID = "openid";
        public static final String PHONE = "phone";
        public static final String PROFILE = "profile";
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private Map<String, String> mAdditionalParameters = new HashMap();
        private String mClientId;
        private String mCodeVerifier;
        private String mCodeVerifierChallenge;
        private String mCodeVerifierChallengeMethod;
        private AuthorizationServiceConfiguration mConfiguration;
        private String mDisplay;
        private String mLoginHint;
        private String mNonce;
        private String mPrompt;
        private Uri mRedirectUri;
        private String mResponseMode;
        private String mResponseType;
        private String mScope;
        private String mState;
        private String mUiLocales;

        public Builder(AuthorizationServiceConfiguration configuration, String clientId, String responseType, Uri redirectUri) {
            setAuthorizationServiceConfiguration(configuration);
            setClientId(clientId);
            setResponseType(responseType);
            setRedirectUri(redirectUri);
            setState(AuthorizationManagementUtil.generateRandomState());
            setNonce(AuthorizationManagementUtil.generateRandomState());
            setCodeVerifier(CodeVerifierUtil.generateRandomCodeVerifier());
        }

        public Builder setAuthorizationServiceConfiguration(AuthorizationServiceConfiguration configuration) {
            this.mConfiguration = (AuthorizationServiceConfiguration) Preconditions.checkNotNull(configuration, "configuration cannot be null");
            return this;
        }

        public Builder setClientId(String clientId) {
            this.mClientId = Preconditions.checkNotEmpty(clientId, "client ID cannot be null or empty");
            return this;
        }

        public Builder setDisplay(String display) {
            this.mDisplay = Preconditions.checkNullOrNotEmpty(display, "display must be null or not empty");
            return this;
        }

        public Builder setLoginHint(String loginHint) {
            this.mLoginHint = Preconditions.checkNullOrNotEmpty(loginHint, "login hint must be null or not empty");
            return this;
        }

        public Builder setPrompt(String prompt) {
            this.mPrompt = Preconditions.checkNullOrNotEmpty(prompt, "prompt must be null or non-empty");
            return this;
        }

        public Builder setPromptValues(String... promptValues) {
            if (promptValues == null) {
                this.mPrompt = null;
                return this;
            }
            return setPromptValues(Arrays.asList(promptValues));
        }

        public Builder setPromptValues(Iterable<String> promptValues) {
            this.mPrompt = AsciiStringListUtil.iterableToString(promptValues);
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

        public Builder setResponseType(String responseType) {
            this.mResponseType = Preconditions.checkNotEmpty(responseType, "expected response type cannot be null or empty");
            return this;
        }

        public Builder setRedirectUri(Uri redirectUri) {
            this.mRedirectUri = (Uri) Preconditions.checkNotNull(redirectUri, "redirect URI cannot be null or empty");
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

        public Builder setState(String state) {
            this.mState = Preconditions.checkNullOrNotEmpty(state, "state cannot be empty if defined");
            return this;
        }

        public Builder setNonce(String nonce) {
            this.mNonce = Preconditions.checkNullOrNotEmpty(nonce, "nonce cannot be empty if defined");
            return this;
        }

        public Builder setCodeVerifier(String codeVerifier) {
            if (codeVerifier != null) {
                CodeVerifierUtil.checkCodeVerifier(codeVerifier);
                this.mCodeVerifier = codeVerifier;
                this.mCodeVerifierChallenge = CodeVerifierUtil.deriveCodeVerifierChallenge(codeVerifier);
                this.mCodeVerifierChallengeMethod = CodeVerifierUtil.getCodeVerifierChallengeMethod();
            } else {
                this.mCodeVerifier = null;
                this.mCodeVerifierChallenge = null;
                this.mCodeVerifierChallengeMethod = null;
            }
            return this;
        }

        public Builder setCodeVerifier(String codeVerifier, String codeVerifierChallenge, String codeVerifierChallengeMethod) {
            if (codeVerifier != null) {
                CodeVerifierUtil.checkCodeVerifier(codeVerifier);
                Preconditions.checkNotEmpty(codeVerifierChallenge, "code verifier challenge cannot be null or empty if verifier is set");
                Preconditions.checkNotEmpty(codeVerifierChallengeMethod, "code verifier challenge method cannot be null or empty if verifier is set");
            } else {
                Preconditions.checkArgument(codeVerifierChallenge == null, "code verifier challenge must be null if verifier is null");
                Preconditions.checkArgument(codeVerifierChallengeMethod == null, "code verifier challenge method must be null if verifier is null");
            }
            this.mCodeVerifier = codeVerifier;
            this.mCodeVerifierChallenge = codeVerifierChallenge;
            this.mCodeVerifierChallengeMethod = codeVerifierChallengeMethod;
            return this;
        }

        public Builder setResponseMode(String responseMode) {
            Preconditions.checkNullOrNotEmpty(responseMode, "responseMode must not be empty");
            this.mResponseMode = responseMode;
            return this;
        }

        public Builder setAdditionalParameters(Map<String, String> additionalParameters) {
            this.mAdditionalParameters = AdditionalParamsProcessor.checkAdditionalParams(additionalParameters, AuthorizationRequest.BUILT_IN_PARAMS);
            return this;
        }

        public AuthorizationRequest build() {
            return new AuthorizationRequest(this.mConfiguration, this.mClientId, this.mResponseType, this.mRedirectUri, this.mDisplay, this.mLoginHint, this.mPrompt, this.mUiLocales, this.mScope, this.mState, this.mNonce, this.mCodeVerifier, this.mCodeVerifierChallenge, this.mCodeVerifierChallengeMethod, this.mResponseMode, Collections.unmodifiableMap(new HashMap(this.mAdditionalParameters)));
        }
    }

    private AuthorizationRequest(AuthorizationServiceConfiguration configuration, String clientId, String responseType, Uri redirectUri, String display, String loginHint, String prompt, String uiLocales, String scope, String state, String nonce, String codeVerifier, String codeVerifierChallenge, String codeVerifierChallengeMethod, String responseMode, Map<String, String> additionalParameters) {
        this.configuration = configuration;
        this.clientId = clientId;
        this.responseType = responseType;
        this.redirectUri = redirectUri;
        this.additionalParameters = additionalParameters;
        this.display = display;
        this.loginHint = loginHint;
        this.prompt = prompt;
        this.uiLocales = uiLocales;
        this.scope = scope;
        this.state = state;
        this.nonce = nonce;
        this.codeVerifier = codeVerifier;
        this.codeVerifierChallenge = codeVerifierChallenge;
        this.codeVerifierChallengeMethod = codeVerifierChallengeMethod;
        this.responseMode = responseMode;
    }

    public Set<String> getScopeSet() {
        return AsciiStringListUtil.stringToSet(this.scope);
    }

    public Set<String> getPromptValues() {
        return AsciiStringListUtil.stringToSet(this.prompt);
    }

    public Set<String> getUiLocales() {
        return AsciiStringListUtil.stringToSet(this.uiLocales);
    }

    @Override // net.openid.appauth.AuthorizationManagementRequest
    public String getState() {
        return this.state;
    }

    @Override // net.openid.appauth.AuthorizationManagementRequest
    public Uri toUri() {
        Uri.Builder appendQueryParameter = this.configuration.authorizationEndpoint.buildUpon().appendQueryParameter(PARAM_REDIRECT_URI, this.redirectUri.toString()).appendQueryParameter("client_id", this.clientId).appendQueryParameter(PARAM_RESPONSE_TYPE, this.responseType);
        UriUtil.appendQueryParameterIfNotNull(appendQueryParameter, "display", this.display);
        UriUtil.appendQueryParameterIfNotNull(appendQueryParameter, "login_hint", this.loginHint);
        UriUtil.appendQueryParameterIfNotNull(appendQueryParameter, "prompt", this.prompt);
        UriUtil.appendQueryParameterIfNotNull(appendQueryParameter, "ui_locales", this.uiLocales);
        UriUtil.appendQueryParameterIfNotNull(appendQueryParameter, "state", this.state);
        UriUtil.appendQueryParameterIfNotNull(appendQueryParameter, "nonce", this.nonce);
        UriUtil.appendQueryParameterIfNotNull(appendQueryParameter, "scope", this.scope);
        UriUtil.appendQueryParameterIfNotNull(appendQueryParameter, PARAM_RESPONSE_MODE, this.responseMode);
        if (this.codeVerifier != null) {
            appendQueryParameter.appendQueryParameter(PARAM_CODE_CHALLENGE, this.codeVerifierChallenge).appendQueryParameter(PARAM_CODE_CHALLENGE_METHOD, this.codeVerifierChallengeMethod);
        }
        for (Map.Entry<String, String> entry : this.additionalParameters.entrySet()) {
            appendQueryParameter.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return appendQueryParameter.build();
    }

    @Override // net.openid.appauth.AuthorizationManagementRequest
    public JSONObject jsonSerialize() {
        JSONObject jSONObject = new JSONObject();
        JsonUtil.put(jSONObject, KEY_CONFIGURATION, this.configuration.toJson());
        JsonUtil.put(jSONObject, KEY_CLIENT_ID, this.clientId);
        JsonUtil.put(jSONObject, KEY_RESPONSE_TYPE, this.responseType);
        JsonUtil.put(jSONObject, KEY_REDIRECT_URI, this.redirectUri.toString());
        JsonUtil.putIfNotNull(jSONObject, "display", this.display);
        JsonUtil.putIfNotNull(jSONObject, "login_hint", this.loginHint);
        JsonUtil.putIfNotNull(jSONObject, "scope", this.scope);
        JsonUtil.putIfNotNull(jSONObject, "prompt", this.prompt);
        JsonUtil.putIfNotNull(jSONObject, "ui_locales", this.uiLocales);
        JsonUtil.putIfNotNull(jSONObject, "state", this.state);
        JsonUtil.putIfNotNull(jSONObject, "nonce", this.nonce);
        JsonUtil.putIfNotNull(jSONObject, KEY_CODE_VERIFIER, this.codeVerifier);
        JsonUtil.putIfNotNull(jSONObject, KEY_CODE_VERIFIER_CHALLENGE, this.codeVerifierChallenge);
        JsonUtil.putIfNotNull(jSONObject, KEY_CODE_VERIFIER_CHALLENGE_METHOD, this.codeVerifierChallengeMethod);
        JsonUtil.putIfNotNull(jSONObject, KEY_RESPONSE_MODE, this.responseMode);
        JsonUtil.put(jSONObject, KEY_ADDITIONAL_PARAMETERS, JsonUtil.mapToJsonObject(this.additionalParameters));
        return jSONObject;
    }

    @Override // net.openid.appauth.AuthorizationManagementRequest
    public String jsonSerializeString() {
        return jsonSerialize().toString();
    }

    public static AuthorizationRequest jsonDeserialize(JSONObject json) throws JSONException {
        Preconditions.checkNotNull(json, "json cannot be null");
        Builder additionalParameters = new Builder(AuthorizationServiceConfiguration.fromJson(json.getJSONObject(KEY_CONFIGURATION)), JsonUtil.getString(json, KEY_CLIENT_ID), JsonUtil.getString(json, KEY_RESPONSE_TYPE), JsonUtil.getUri(json, KEY_REDIRECT_URI)).setDisplay(JsonUtil.getStringIfDefined(json, "display")).setLoginHint(JsonUtil.getStringIfDefined(json, "login_hint")).setPrompt(JsonUtil.getStringIfDefined(json, "prompt")).setUiLocales(JsonUtil.getStringIfDefined(json, "ui_locales")).setState(JsonUtil.getStringIfDefined(json, "state")).setNonce(JsonUtil.getStringIfDefined(json, "nonce")).setCodeVerifier(JsonUtil.getStringIfDefined(json, KEY_CODE_VERIFIER), JsonUtil.getStringIfDefined(json, KEY_CODE_VERIFIER_CHALLENGE), JsonUtil.getStringIfDefined(json, KEY_CODE_VERIFIER_CHALLENGE_METHOD)).setResponseMode(JsonUtil.getStringIfDefined(json, KEY_RESPONSE_MODE)).setAdditionalParameters(JsonUtil.getStringMap(json, KEY_ADDITIONAL_PARAMETERS));
        if (json.has("scope")) {
            additionalParameters.setScopes(AsciiStringListUtil.stringToSet(JsonUtil.getString(json, "scope")));
        }
        return additionalParameters.build();
    }

    public static AuthorizationRequest jsonDeserialize(String jsonStr) throws JSONException {
        Preconditions.checkNotNull(jsonStr, "json string cannot be null");
        return jsonDeserialize(new JSONObject(jsonStr));
    }
}
