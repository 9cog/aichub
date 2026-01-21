package com.byteowls.capacitor.oauth2.handler;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface AccessTokenCallback {
    void onCancel();

    void onError(Exception exc);

    void onSuccess(String str);
}
