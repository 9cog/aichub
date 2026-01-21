package com.getcapacitor.plugin;

import android.webkit.JavascriptInterface;
import androidx.browser.trusted.sharing.ShareTarget;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.plugin.util.CapacitorHttpUrlConnection;
import com.getcapacitor.plugin.util.HttpRequestHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@CapacitorPlugin(permissions = {@Permission(alias = "HttpWrite", strings = {"android.permission.WRITE_EXTERNAL_STORAGE"}), @Permission(alias = "HttpRead", strings = {"android.permission.READ_EXTERNAL_STORAGE"})})
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CapacitorHttp extends Plugin {
    private final Map<Runnable, PluginCall> activeRequests = new HashMap();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override // com.getcapacitor.Plugin
    public void load() {
        this.bridge.getWebView().addJavascriptInterface(this, "CapacitorHttpAndroidInterface");
        super.load();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.getcapacitor.Plugin
    public void handleOnDestroy() {
        super.handleOnDestroy();
        for (Map.Entry<Runnable, PluginCall> entry : this.activeRequests.entrySet()) {
            entry.getKey();
            PluginCall value = entry.getValue();
            if (value.getData().has("activeCapacitorHttpUrlConnection")) {
                try {
                    ((CapacitorHttpUrlConnection) value.getData().get("activeCapacitorHttpUrlConnection")).disconnect();
                    value.getData().remove("activeCapacitorHttpUrlConnection");
                } catch (Exception unused) {
                }
            }
            getBridge().releaseCall(value);
        }
        this.activeRequests.clear();
        this.executor.shutdownNow();
    }

    private void http(final PluginCall pluginCall, final String str) {
        Runnable runnable = new Runnable() { // from class: com.getcapacitor.plugin.CapacitorHttp.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    try {
                        pluginCall.resolve(HttpRequestHandler.request(pluginCall, str, CapacitorHttp.this.getBridge()));
                    } catch (Exception e) {
                        pluginCall.reject(e.getLocalizedMessage(), e.getClass().getSimpleName(), e);
                    }
                } finally {
                    CapacitorHttp.this.activeRequests.remove(this);
                }
            }
        };
        if (!this.executor.isShutdown()) {
            this.activeRequests.put(runnable, pluginCall);
            this.executor.submit(runnable);
            return;
        }
        pluginCall.reject("Failed to execute request - Http Plugin was shutdown");
    }

    @JavascriptInterface
    public boolean isEnabled() {
        return getBridge().getConfig().getPluginConfiguration("CapacitorHttp").getBoolean("enabled", false);
    }

    @PluginMethod
    public void request(PluginCall pluginCall) {
        http(pluginCall, null);
    }

    @PluginMethod
    public void get(PluginCall pluginCall) {
        http(pluginCall, ShareTarget.METHOD_GET);
    }

    @PluginMethod
    public void post(PluginCall pluginCall) {
        http(pluginCall, ShareTarget.METHOD_POST);
    }

    @PluginMethod
    public void put(PluginCall pluginCall) {
        http(pluginCall, "PUT");
    }

    @PluginMethod
    public void patch(PluginCall pluginCall) {
        http(pluginCall, "PATCH");
    }

    @PluginMethod
    public void delete(PluginCall pluginCall) {
        http(pluginCall, "DELETE");
    }
}
