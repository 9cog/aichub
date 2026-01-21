package org.apache.cordova;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface CordovaInterface {
    AppCompatActivity getActivity();

    Context getContext();

    ExecutorService getThreadPool();

    boolean hasPermission(String permission);

    Object onMessage(String id, Object data);

    void requestPermission(CordovaPlugin plugin, int requestCode, String permission);

    void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions);

    void setActivityResultCallback(CordovaPlugin plugin);

    void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode);
}
