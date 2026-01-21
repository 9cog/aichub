package net.openid.appauth;

import android.content.Intent;
import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class EndSessionResponse extends AuthorizationManagementResponse {
    public static final String EXTRA_RESPONSE = "net.openid.appauth.EndSessionResponse";
    static final String KEY_REQUEST = "request";
    static final String KEY_STATE = "state";
    public final EndSessionRequest request;
    public final String state;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private EndSessionRequest mRequest;
        private String mState;

        public Builder(EndSessionRequest request) {
            setRequest(request);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder fromUri(Uri uri) {
            setState(uri.getQueryParameter(EndSessionResponse.KEY_STATE));
            return this;
        }

        public Builder setRequest(EndSessionRequest request) {
            this.mRequest = (EndSessionRequest) Preconditions.checkNotNull(request, "request cannot be null");
            return this;
        }

        public Builder setState(String state) {
            this.mState = Preconditions.checkNullOrNotEmpty(state, "state must not be empty");
            return this;
        }

        public EndSessionResponse build() {
            return new EndSessionResponse(this.mRequest, this.mState);
        }
    }

    private EndSessionResponse(EndSessionRequest request, String state) {
        this.request = request;
        this.state = state;
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
        return jSONObject;
    }

    public static EndSessionResponse jsonDeserialize(JSONObject json) throws JSONException {
        if (!json.has(KEY_REQUEST)) {
            throw new IllegalArgumentException("authorization request not provided and not found in JSON");
        }
        return new EndSessionResponse(EndSessionRequest.jsonDeserialize(json.getJSONObject(KEY_REQUEST)), JsonUtil.getStringIfDefined(json, KEY_STATE));
    }

    public static EndSessionResponse jsonDeserialize(String jsonStr) throws JSONException {
        return jsonDeserialize(new JSONObject(jsonStr));
    }

    @Override // net.openid.appauth.AuthorizationManagementResponse
    public Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESPONSE, jsonSerializeString());
        return intent;
    }

    public static EndSessionResponse fromIntent(Intent dataIntent) {
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
    public static boolean containsEndSessionResponse(Intent intent) {
        return intent.hasExtra(EXTRA_RESPONSE);
    }
}
