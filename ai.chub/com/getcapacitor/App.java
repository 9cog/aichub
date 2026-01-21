package com.getcapacitor;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class App {
    private AppRestoredListener appRestoredListener;
    private boolean isActive = false;
    private AppStatusChangeListener statusChangeListener;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface AppRestoredListener {
        void onAppRestored(PluginResult pluginResult);
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface AppStatusChangeListener {
        void onAppStatusChanged(Boolean bool);
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setStatusChangeListener(AppStatusChangeListener appStatusChangeListener) {
        this.statusChangeListener = appStatusChangeListener;
    }

    public void setAppRestoredListener(AppRestoredListener appRestoredListener) {
        this.appRestoredListener = appRestoredListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void fireRestoredResult(PluginResult pluginResult) {
        AppRestoredListener appRestoredListener = this.appRestoredListener;
        if (appRestoredListener != null) {
            appRestoredListener.onAppRestored(pluginResult);
        }
    }

    public void fireStatusChange(boolean z) {
        this.isActive = z;
        AppStatusChangeListener appStatusChangeListener = this.statusChangeListener;
        if (appStatusChangeListener != null) {
            appStatusChangeListener.onAppStatusChanged(Boolean.valueOf(z));
        }
    }
}
