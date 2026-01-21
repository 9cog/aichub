package com.byteowls.capacitor.oauth2;

import com.getcapacitor.JSObject;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.TokenResponse;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public abstract class OAuth2Utils {
    public static void assignResponses(JSObject jSObject, String str, AuthorizationResponse authorizationResponse, TokenResponse tokenResponse) {
        if (authorizationResponse != null) {
            jSObject.put("authorization_response", (Object) authorizationResponse.jsonSerialize());
        }
        if (tokenResponse != null) {
            jSObject.put("access_token_response", (Object) tokenResponse.jsonSerialize());
        }
        if (str != null) {
            jSObject.put("access_token", str);
        }
    }
}
