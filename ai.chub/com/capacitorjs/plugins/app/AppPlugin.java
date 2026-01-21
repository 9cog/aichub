package com.capacitorjs.plugins.app;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import androidx.activity.OnBackPressedCallback;
import androidx.core.content.pm.PackageInfoCompat;
import com.getcapacitor.App;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PluginResult;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.InternalUtils;
import com.google.android.gms.common.internal.ImagesContract;
@CapacitorPlugin(name = "App")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AppPlugin extends Plugin {
    private static final String EVENT_BACK_BUTTON = "backButton";
    private static final String EVENT_PAUSE = "pause";
    private static final String EVENT_RESTORED_RESULT = "appRestoredResult";
    private static final String EVENT_RESUME = "resume";
    private static final String EVENT_STATE_CHANGE = "appStateChange";
    private static final String EVENT_URL_OPEN = "appUrlOpen";
    private boolean hasPausedEver = false;

    @Override // com.getcapacitor.Plugin
    public void load() {
        this.bridge.getApp().setStatusChangeListener(new App.AppStatusChangeListener() { // from class: com.capacitorjs.plugins.app.AppPlugin$$ExternalSyntheticLambda0
            @Override // com.getcapacitor.App.AppStatusChangeListener
            public final void onAppStatusChanged(Boolean bool) {
                AppPlugin.this.lambda$load$0(bool);
            }
        });
        this.bridge.getApp().setAppRestoredListener(new App.AppRestoredListener() { // from class: com.capacitorjs.plugins.app.AppPlugin$$ExternalSyntheticLambda1
            @Override // com.getcapacitor.App.AppRestoredListener
            public final void onAppRestored(PluginResult pluginResult) {
                AppPlugin.this.lambda$load$1(pluginResult);
            }
        });
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) { // from class: com.capacitorjs.plugins.app.AppPlugin.1
            @Override // androidx.activity.OnBackPressedCallback
            public void handleOnBackPressed() {
                if (!AppPlugin.this.hasListeners(AppPlugin.EVENT_BACK_BUTTON)) {
                    if (AppPlugin.this.bridge.getWebView().canGoBack()) {
                        AppPlugin.this.bridge.getWebView().goBack();
                        return;
                    }
                    return;
                }
                JSObject jSObject = new JSObject();
                jSObject.put("canGoBack", AppPlugin.this.bridge.getWebView().canGoBack());
                AppPlugin.this.notifyListeners(AppPlugin.EVENT_BACK_BUTTON, jSObject, true);
                AppPlugin.this.bridge.triggerJSEvent("backbutton", "document");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$load$0(Boolean bool) {
        String logTag = getLogTag();
        Logger.debug(logTag, "Firing change: " + bool);
        JSObject jSObject = new JSObject();
        jSObject.put("isActive", (Object) bool);
        notifyListeners(EVENT_STATE_CHANGE, jSObject, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$load$1(PluginResult pluginResult) {
        Logger.debug(getLogTag(), "Firing restored result");
        notifyListeners(EVENT_RESTORED_RESULT, pluginResult.getWrappedResult(), true);
    }

    @PluginMethod
    public void exitApp(PluginCall pluginCall) {
        unsetAppListeners();
        pluginCall.resolve();
        getBridge().getActivity().finish();
    }

    @PluginMethod
    public void getInfo(PluginCall pluginCall) {
        JSObject jSObject = new JSObject();
        try {
            PackageInfo packageInfo = InternalUtils.getPackageInfo(getContext().getPackageManager(), getContext().getPackageName());
            ApplicationInfo applicationInfo = getContext().getApplicationInfo();
            int i = applicationInfo.labelRes;
            jSObject.put("name", i == 0 ? applicationInfo.nonLocalizedLabel.toString() : getContext().getString(i));
            jSObject.put("id", packageInfo.packageName);
            jSObject.put("build", Integer.toString((int) PackageInfoCompat.getLongVersionCode(packageInfo)));
            jSObject.put("version", packageInfo.versionName);
            pluginCall.resolve(jSObject);
        } catch (Exception unused) {
            pluginCall.reject("Unable to get App Info");
        }
    }

    @PluginMethod
    public void getLaunchUrl(PluginCall pluginCall) {
        Uri intentUri = this.bridge.getIntentUri();
        if (intentUri != null) {
            JSObject jSObject = new JSObject();
            jSObject.put(ImagesContract.URL, intentUri.toString());
            pluginCall.resolve(jSObject);
            return;
        }
        pluginCall.resolve();
    }

    @PluginMethod
    public void getState(PluginCall pluginCall) {
        JSObject jSObject = new JSObject();
        jSObject.put("isActive", this.bridge.getApp().isActive());
        pluginCall.resolve(jSObject);
    }

    @PluginMethod
    public void minimizeApp(PluginCall pluginCall) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        getActivity().startActivity(intent);
        pluginCall.resolve();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnNewIntent(Intent intent) {
        super.handleOnNewIntent(intent);
        String action = intent.getAction();
        Uri data = intent.getData();
        if (!"android.intent.action.VIEW".equals(action) || data == null) {
            return;
        }
        JSObject jSObject = new JSObject();
        jSObject.put(ImagesContract.URL, data.toString());
        notifyListeners(EVENT_URL_OPEN, jSObject, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnPause() {
        super.handleOnPause();
        this.hasPausedEver = true;
        notifyListeners(EVENT_PAUSE, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnResume() {
        super.handleOnResume();
        if (this.hasPausedEver) {
            notifyListeners(EVENT_RESUME, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnDestroy() {
        unsetAppListeners();
    }

    private void unsetAppListeners() {
        this.bridge.getApp().setStatusChangeListener(null);
        this.bridge.getApp().setAppRestoredListener(null);
    }
}
