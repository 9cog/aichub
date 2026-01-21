package net.openid.appauth;

import android.net.Uri;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface AuthorizationManagementRequest {
    String getState();

    JSONObject jsonSerialize();

    String jsonSerializeString();

    Uri toUri();
}
