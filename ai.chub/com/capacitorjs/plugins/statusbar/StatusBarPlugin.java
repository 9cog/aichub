package com.capacitorjs.plugins.statusbar;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.WebColor;
import java.util.Locale;
@CapacitorPlugin(name = "StatusBar")
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class StatusBarPlugin extends Plugin {
    private StatusBar implementation;

    @Override // com.getcapacitor.Plugin
    public void load() {
        this.implementation = new StatusBar(getActivity());
    }

    @PluginMethod
    public void setStyle(final PluginCall pluginCall) {
        final String string = pluginCall.getString("style");
        if (string == null) {
            pluginCall.reject("Style must be provided");
        } else {
            getBridge().executeOnMainThread(new Runnable() { // from class: com.capacitorjs.plugins.statusbar.StatusBarPlugin$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarPlugin.this.lambda$setStyle$0(string, pluginCall);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setStyle$0(String str, PluginCall pluginCall) {
        this.implementation.setStyle(str);
        pluginCall.resolve();
    }

    @PluginMethod
    public void setBackgroundColor(final PluginCall pluginCall) {
        final String string = pluginCall.getString("color");
        if (string == null) {
            pluginCall.reject("Color must be provided");
        } else {
            getBridge().executeOnMainThread(new Runnable() { // from class: com.capacitorjs.plugins.statusbar.StatusBarPlugin$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarPlugin.this.lambda$setBackgroundColor$1(string, pluginCall);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setBackgroundColor$1(String str, PluginCall pluginCall) {
        try {
            this.implementation.setBackgroundColor(WebColor.parseColor(str.toUpperCase(Locale.ROOT)));
            pluginCall.resolve();
        } catch (IllegalArgumentException unused) {
            pluginCall.reject("Invalid color provided. Must be a hex string (ex: #ff0000");
        }
    }

    @PluginMethod
    public void hide(final PluginCall pluginCall) {
        getBridge().executeOnMainThread(new Runnable() { // from class: com.capacitorjs.plugins.statusbar.StatusBarPlugin$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarPlugin.this.lambda$hide$2(pluginCall);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hide$2(PluginCall pluginCall) {
        this.implementation.hide();
        pluginCall.resolve();
    }

    @PluginMethod
    public void show(final PluginCall pluginCall) {
        getBridge().executeOnMainThread(new Runnable() { // from class: com.capacitorjs.plugins.statusbar.StatusBarPlugin$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarPlugin.this.lambda$show$3(pluginCall);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$3(PluginCall pluginCall) {
        this.implementation.show();
        pluginCall.resolve();
    }

    @PluginMethod
    public void getInfo(PluginCall pluginCall) {
        StatusBarInfo info = this.implementation.getInfo();
        JSObject jSObject = new JSObject();
        jSObject.put("visible", info.isVisible());
        jSObject.put("style", info.getStyle());
        jSObject.put("color", info.getColor());
        jSObject.put("overlays", info.isOverlays());
        pluginCall.resolve(jSObject);
    }

    @PluginMethod
    public void setOverlaysWebView(final PluginCall pluginCall) {
        final Boolean bool = pluginCall.getBoolean("overlay", true);
        getBridge().executeOnMainThread(new Runnable() { // from class: com.capacitorjs.plugins.statusbar.StatusBarPlugin$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarPlugin.this.lambda$setOverlaysWebView$4(bool, pluginCall);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setOverlaysWebView$4(Boolean bool, PluginCall pluginCall) {
        this.implementation.setOverlaysWebView(bool);
        pluginCall.resolve();
    }
}
