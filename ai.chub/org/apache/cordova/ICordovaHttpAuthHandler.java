package org.apache.cordova;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ICordovaHttpAuthHandler {
    void cancel();

    void proceed(String username, String password);
}
