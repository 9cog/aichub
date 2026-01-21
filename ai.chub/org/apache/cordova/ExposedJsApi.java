package org.apache.cordova;

import org.json.JSONException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ExposedJsApi {
    String exec(int bridgeSecret, String service, String action, String callbackId, String arguments) throws JSONException, IllegalAccessException;

    String retrieveJsMessages(int bridgeSecret, boolean fromOnlineEvent) throws IllegalAccessException;

    void setNativeToJsBridgeMode(int bridgeSecret, int value) throws IllegalAccessException;
}
