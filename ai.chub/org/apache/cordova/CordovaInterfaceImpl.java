package org.apache.cordova;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CordovaInterfaceImpl implements CordovaInterface {
    private static final String TAG = "CordovaInterfaceImpl";
    protected AppCompatActivity activity;
    protected CordovaPlugin activityResultCallback;
    protected int activityResultRequestCode;
    protected boolean activityWasDestroyed;
    protected String initCallbackService;
    protected CallbackMap permissionResultCallbacks;
    protected PluginManager pluginManager;
    protected Bundle savedPluginState;
    protected ActivityResultHolder savedResult;
    protected ExecutorService threadPool;

    public CordovaInterfaceImpl(AppCompatActivity activity) {
        this(activity, Executors.newCachedThreadPool());
    }

    public CordovaInterfaceImpl(AppCompatActivity activity, ExecutorService threadPool) {
        this.activityWasDestroyed = false;
        this.activity = activity;
        this.threadPool = threadPool;
        this.permissionResultCallbacks = new CallbackMap();
    }

    @Override // org.apache.cordova.CordovaInterface
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
        setActivityResultCallback(command);
        try {
            this.activity.startActivityForResult(intent, requestCode);
        } catch (RuntimeException e) {
            this.activityResultCallback = null;
            throw e;
        }
    }

    @Override // org.apache.cordova.CordovaInterface
    public void setActivityResultCallback(CordovaPlugin plugin) {
        CordovaPlugin cordovaPlugin = this.activityResultCallback;
        if (cordovaPlugin != null) {
            cordovaPlugin.onActivityResult(this.activityResultRequestCode, 0, null);
        }
        this.activityResultCallback = plugin;
    }

    @Override // org.apache.cordova.CordovaInterface
    public AppCompatActivity getActivity() {
        return this.activity;
    }

    @Override // org.apache.cordova.CordovaInterface
    public Context getContext() {
        return this.activity;
    }

    @Override // org.apache.cordova.CordovaInterface
    public Object onMessage(String id, Object data) {
        if ("exit".equals(id)) {
            this.activity.finish();
            return null;
        }
        return null;
    }

    @Override // org.apache.cordova.CordovaInterface
    public ExecutorService getThreadPool() {
        return this.threadPool;
    }

    public void onCordovaInit(PluginManager pluginManager) {
        CoreAndroid coreAndroid;
        this.pluginManager = pluginManager;
        ActivityResultHolder activityResultHolder = this.savedResult;
        if (activityResultHolder == null) {
            if (this.activityWasDestroyed) {
                this.activityWasDestroyed = false;
                if (pluginManager == null || (coreAndroid = (CoreAndroid) pluginManager.getPlugin(CoreAndroid.PLUGIN_NAME)) == null) {
                    return;
                }
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("action", "resume");
                } catch (JSONException e) {
                    LOG.e(TAG, "Failed to create event message", e);
                }
                coreAndroid.sendResumeEvent(new PluginResult(PluginResult.Status.OK, jSONObject));
                return;
            }
            return;
        }
        onActivityResult(activityResultHolder.requestCode, this.savedResult.resultCode, this.savedResult.intent);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        CordovaPlugin cordovaPlugin = this.activityResultCallback;
        if (cordovaPlugin == null && this.initCallbackService != null) {
            this.savedResult = new ActivityResultHolder(requestCode, resultCode, intent);
            PluginManager pluginManager = this.pluginManager;
            if (pluginManager != null && (cordovaPlugin = pluginManager.getPlugin(this.initCallbackService)) != null) {
                cordovaPlugin.onRestoreStateForActivityResult(this.savedPluginState.getBundle(cordovaPlugin.getServiceName()), new ResumeCallback(cordovaPlugin.getServiceName(), this.pluginManager));
            }
        }
        this.activityResultCallback = null;
        if (cordovaPlugin == null) {
            LOG.w(TAG, "Got an activity result, but no plugin was registered to receive it".concat(this.savedResult != null ? " yet!" : "."));
            return false;
        }
        LOG.d(TAG, "Sending activity result to plugin");
        this.initCallbackService = null;
        this.savedResult = null;
        cordovaPlugin.onActivityResult(requestCode, resultCode, intent);
        return true;
    }

    public void setActivityResultRequestCode(int requestCode) {
        this.activityResultRequestCode = requestCode;
    }

    public void onSaveInstanceState(Bundle outState) {
        CordovaPlugin cordovaPlugin = this.activityResultCallback;
        if (cordovaPlugin != null) {
            outState.putString("callbackService", cordovaPlugin.getServiceName());
        }
        PluginManager pluginManager = this.pluginManager;
        if (pluginManager != null) {
            outState.putBundle("plugin", pluginManager.onSaveInstanceState());
        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        this.initCallbackService = savedInstanceState.getString("callbackService");
        this.savedPluginState = savedInstanceState.getBundle("plugin");
        this.activityWasDestroyed = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class ActivityResultHolder {
        private Intent intent;
        private int requestCode;
        private int resultCode;

        public ActivityResultHolder(int requestCode, int resultCode, Intent intent) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.intent = intent;
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        Pair<CordovaPlugin, Integer> andRemoveCallback = this.permissionResultCallbacks.getAndRemoveCallback(requestCode);
        if (andRemoveCallback != null) {
            ((CordovaPlugin) andRemoveCallback.first).onRequestPermissionResult(((Integer) andRemoveCallback.second).intValue(), permissions, grantResults);
        }
    }

    @Override // org.apache.cordova.CordovaInterface
    public void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
        requestPermissions(plugin, requestCode, new String[]{permission});
    }

    @Override // org.apache.cordova.CordovaInterface
    public void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
        getActivity().requestPermissions(permissions, this.permissionResultCallbacks.registerCallback(plugin, requestCode));
    }

    @Override // org.apache.cordova.CordovaInterface
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < 23 || this.activity.checkSelfPermission(permission) == 0;
    }
}
