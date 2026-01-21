package net.openid.appauth;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import java.security.SecureRandom;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.EndSessionResponse;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
class AuthorizationManagementUtil {
    public static final String REQUEST_TYPE_AUTHORIZATION = "authorization";
    public static final String REQUEST_TYPE_END_SESSION = "end_session";
    private static final int STATE_LENGTH = 16;

    AuthorizationManagementUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateRandomState() {
        byte[] bArr = new byte[16];
        new SecureRandom().nextBytes(bArr);
        return Base64.encodeToString(bArr, 11);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String requestTypeFor(AuthorizationManagementRequest request) {
        if (request instanceof AuthorizationRequest) {
            return REQUEST_TYPE_AUTHORIZATION;
        }
        if (request instanceof EndSessionRequest) {
            return REQUEST_TYPE_END_SESSION;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AuthorizationManagementRequest requestFrom(String jsonStr, String type) throws JSONException {
        Preconditions.checkNotNull(jsonStr, "jsonStr can not be null");
        JSONObject jSONObject = new JSONObject(jsonStr);
        if (REQUEST_TYPE_AUTHORIZATION.equals(type)) {
            return AuthorizationRequest.jsonDeserialize(jSONObject);
        }
        if (REQUEST_TYPE_END_SESSION.equals(type)) {
            return EndSessionRequest.jsonDeserialize(jSONObject);
        }
        throw new IllegalArgumentException("No AuthorizationManagementRequest found matching to this json schema");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AuthorizationManagementResponse responseWith(AuthorizationManagementRequest request, Uri uri) {
        if (request instanceof AuthorizationRequest) {
            return new AuthorizationResponse.Builder((AuthorizationRequest) request).fromUri(uri).build();
        }
        if (request instanceof EndSessionRequest) {
            return new EndSessionResponse.Builder((EndSessionRequest) request).fromUri(uri).build();
        }
        throw new IllegalArgumentException("Malformed request or uri");
    }

    static AuthorizationManagementResponse responseFrom(Intent dataIntent) {
        if (EndSessionResponse.containsEndSessionResponse(dataIntent)) {
            return EndSessionResponse.fromIntent(dataIntent);
        }
        if (AuthorizationResponse.containsAuthorizationResponse(dataIntent)) {
            return AuthorizationResponse.fromIntent(dataIntent);
        }
        throw new IllegalArgumentException("Malformed intent");
    }
}
