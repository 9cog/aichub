package net.openid.appauth;

import java.util.HashMap;
import java.util.Map;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class ClientSecretPost implements ClientAuthentication {
    public static final String NAME = "client_secret_post";
    static final String PARAM_CLIENT_ID = "client_id";
    static final String PARAM_CLIENT_SECRET = "client_secret";
    private String mClientSecret;

    @Override // net.openid.appauth.ClientAuthentication
    public final Map<String, String> getRequestHeaders(String clientId) {
        return null;
    }

    public ClientSecretPost(String clientSecret) {
        this.mClientSecret = (String) Preconditions.checkNotNull(clientSecret, "clientSecret cannot be null");
    }

    @Override // net.openid.appauth.ClientAuthentication
    public final Map<String, String> getRequestParameters(String clientId) {
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", clientId);
        hashMap.put(PARAM_CLIENT_SECRET, this.mClientSecret);
        return hashMap;
    }
}
