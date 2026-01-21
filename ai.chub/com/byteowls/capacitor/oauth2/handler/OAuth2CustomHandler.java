package com.byteowls.capacitor.oauth2.handler;

import android.app.Activity;
import com.getcapacitor.PluginCall;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface OAuth2CustomHandler {
    void getAccessToken(Activity activity, PluginCall pluginCall, AccessTokenCallback accessTokenCallback);

    boolean logout(Activity activity, PluginCall pluginCall);
}
